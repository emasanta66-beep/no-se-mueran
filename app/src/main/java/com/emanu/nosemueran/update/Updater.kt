package com.emanu.nosemueran.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import com.emanu.nosemueran.MainActivity
import com.emanu.nosemueran.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionCode: Long,
    val versionName: String,
    val downloadUrl: String,
    val notes: String
)

/**
 * Busca la última versión publicada en GitHub, la descarga y abre el
 * instalador de Android. Solo se usa cuando el usuario pide buscar una
 * actualización (o cuando corre el chequeo diario opcional).
 */
object Updater {

    private const val OWNER = "emasanta66-beep"
    private const val REPO = "no-se-mueran"
    private const val API_URL = "https://api.github.com/repos/$OWNER/$REPO/releases/latest"
    private const val APK_NAME = "NoSeMueran.apk"
    private const val USER_AGENT = "NoSeMueran-Android"

    private const val UPDATE_CHANNEL = "actualizaciones"
    private const val UPDATE_NOTIFICATION_ID = 2002

    private const val PREFS = "actualizador"
    private const val KEY_ULTIMO_AVISO = "ultimo_aviso_version"

    const val EXTRA_OPEN_UPDATES = "abrir_actualizaciones"

    fun currentVersionCode(context: Context): Long =
        PackageInfoCompat.getLongVersionCode(
            context.packageManager.getPackageInfo(context.packageName, 0)
        )

    fun currentVersionName(context: Context): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"

    /** Devuelve la actualización disponible, o null si ya está al día. */
    suspend fun check(context: Context): Result<UpdateInfo?> = withContext(Dispatchers.IO) {
        runCatching {
            val json = JSONObject(get(API_URL))
            val tag = json.optString("tag_name").trim().removePrefix("v").removePrefix("V")
            val remoteCode = tag.toLongOrNull() ?: return@runCatching null
            if (remoteCode <= currentVersionCode(context)) return@runCatching null

            val assets = json.optJSONArray("assets") ?: return@runCatching null
            var downloadUrl: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.optString("name")
                if (name.equals(APK_NAME, ignoreCase = true) ||
                    name.endsWith(".apk", ignoreCase = true)
                ) {
                    downloadUrl = asset.optString("browser_download_url")
                        .takeIf { it.isNotBlank() }
                    if (downloadUrl != null) break
                }
            }
            val url = downloadUrl ?: return@runCatching null

            UpdateInfo(
                versionCode = remoteCode,
                versionName = json.optString("name").ifBlank { "Versión $remoteCode" },
                downloadUrl = url,
                notes = json.optString("body").trim()
            )
        }
    }

    /** Descarga la APK y verifica paquete, versión y firma antes de devolverla. */
    suspend fun download(
        context: Context,
        info: UpdateInfo,
        onProgress: (Int?) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.cacheDir, "updates").apply { mkdirs() }
            dir.listFiles()?.forEach { it.delete() }
            val target = File(dir, "update.apk")

            val conn = open(info.downloadUrl)
            try {
                val code = conn.responseCode
                if (code !in 200..299) {
                    throw IllegalStateException("La descarga falló (código $code).")
                }
                val total = conn.contentLengthLong
                var done = 0L
                var lastPercent = -1
                // Si GitHub no informa el tamaño, mostramos progreso indeterminado.
                if (total <= 0L) onProgress(null)
                conn.inputStream.use { input ->
                    FileOutputStream(target).use { output ->
                        val buffer = ByteArray(64 * 1024)
                        while (true) {
                            val read = input.read(buffer)
                            if (read < 0) break
                            output.write(buffer, 0, read)
                            done += read
                            if (total > 0L) {
                                val percent = ((done * 100) / total).toInt()
                                if (percent != lastPercent) {
                                    lastPercent = percent
                                    onProgress(percent)
                                }
                            }
                        }
                        output.flush()
                    }
                }
            } finally {
                conn.disconnect()
            }

            if (target.length() <= 0L) {
                throw IllegalStateException("El archivo descargado vino vacío.")
            }
            verify(context, target, info.versionCode)
            target
        }
    }

    fun canInstallPackages(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            context.packageManager.canRequestPackageInstalls()

    fun openInstallPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context.packageName}")
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }

    fun install(context: Context, apk: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apk
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /** Borra APKs viejas del caché (se llama al abrir la app). */
    fun cleanup(context: Context) {
        runCatching {
            val dir = File(context.cacheDir, "updates")
            val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
            dir.listFiles()
                ?.filter { it.lastModified() < cutoff }
                ?.forEach { it.delete() }
            if (dir.exists() && dir.listFiles()?.isEmpty() == true) dir.delete()
        }
    }

    /** Aviso opcional del chequeo diario. Devuelve true si se publicó. */
    fun notifyUpdate(context: Context, info: UpdateInfo): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false

        // No repetir el aviso todos los días por la misma versión.
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getLong(KEY_ULTIMO_AVISO, 0L) >= info.versionCode) return false

        ensureUpdateChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_UPDATES, true)
        }
        val pending = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = "${info.versionName} ya está disponible. Tocá para actualizar."
        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_leaf)
            .setContentTitle("🌱 Hay una versión nueva")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setColor(0xFF2F5D3E.toInt())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        return runCatching {
            NotificationManagerCompat.from(context).notify(UPDATE_NOTIFICATION_ID, notification)
            prefs.edit().putLong(KEY_ULTIMO_AVISO, info.versionCode).apply()
            true
        }.getOrDefault(false)
    }

    fun cancelUpdateNotification(context: Context) {
        runCatching { NotificationManagerCompat.from(context).cancel(UPDATE_NOTIFICATION_ID) }
    }

    // ---------- interno ----------

    private fun open(url: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 25_000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
        }

    private fun get(url: String): String {
        val conn = open(url)
        try {
            val code = conn.responseCode
            if (code == 404) {
                throw IllegalStateException("Todavía no hay actualizaciones publicadas.")
            }
            if (code !in 200..299) {
                throw IllegalStateException("GitHub respondió con error $code.")
            }
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    private fun verify(context: Context, apk: File, expectedCode: Long) {
        val pm = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        val archive = pm.getPackageArchiveInfo(apk.absolutePath, flags)
            ?: throw IllegalStateException("El archivo descargado no es una APK válida.")
        if (archive.packageName != context.packageName) {
            throw IllegalStateException("La actualización no corresponde a esta app.")
        }
        if (PackageInfoCompat.getLongVersionCode(archive) != expectedCode) {
            throw IllegalStateException("La versión descargada no coincide con la publicada.")
        }

        val installed = pm.getPackageInfo(context.packageName, flags)
        val installedSigners = signers(installed)
        val archiveSigners = signers(archive)
        if (installedSigners.isEmpty() ||
            installedSigners.any { a -> archiveSigners.none { b -> a.contentEquals(b) } }
        ) {
            throw IllegalStateException("La firma de la actualización no coincide con la app instalada.")
        }
    }

    private fun signers(info: PackageInfo): List<ByteArray> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.signingInfo?.apkContentsSigners?.map { it.toByteArray() } ?: emptyList()
        } else {
            @Suppress("DEPRECATION")
            info.signatures?.map { it.toByteArray() } ?: emptyList()
        }

    private fun ensureUpdateChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPDATE_CHANNEL,
                "Actualizaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisa cuando hay una versión nueva de la app"
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }
}
