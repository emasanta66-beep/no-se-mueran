package com.emanu.nosemueran.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.emanu.nosemueran.MainActivity
import com.emanu.nosemueran.R
import com.emanu.nosemueran.data.Plant

object Notifications {

    const val CHANNEL_ID = "riego"
    private const val NOTIFICATION_ID = 2001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de riego",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos cuando a tus plantas les toca agua"
                enableVibration(true)
            }
            context.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    /** Devuelve true solo si la notificación llegó a publicarse. */
    fun notifyDue(context: Context, plants: List<Plant>): Boolean {
        if (plants.isEmpty()) return false
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return false
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val names = plants.take(3).joinToString(", ") { it.name }
        val extra = if (plants.size > 3) " y ${plants.size - 3} más" else ""
        val text = if (plants.size == 1) {
            "${plants.first().name} necesita agua. Tocá para verla."
        } else {
            "$names$extra necesitan agua."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_leaf)
            .setContentTitle("💧 Hora de regar")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setColor(0xFF2F5D3E.toInt())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        return runCatching {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            true
        }.getOrDefault(false)
    }
}
