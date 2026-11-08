package com.emanu.nosemueran.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color

/** Porcentajes de color detectados en la foto (0..100). */
data class LeafStats(
    val green: Float,
    val yellow: Float,
    val brown: Float,
    val dark: Float
)

data class Diagnosis(
    val title: String,
    val message: String,
    val tips: List<String>,
    val level: Int // 0 = bien, 1 = atención, 2 = urgente
)

object ImageAnalysis {

    fun analyzeFile(path: String): LeafStats? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        while (bounds.outWidth / sample > 360 || bounds.outHeight / sample > 360) {
            sample *= 2
        }
        val bitmap = BitmapFactory.decodeFile(
            path,
            BitmapFactory.Options().apply { inSampleSize = sample }
        ) ?: return null

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        var green = 0
        var yellow = 0
        var brown = 0
        var dark = 0
        var other = 0
        val hsv = FloatArray(3)

        for (pixel in pixels) {
            val alpha = Color.alpha(pixel)
            if (alpha < 40) continue
            Color.colorToHSV(pixel, hsv)
            val h = hsv[0]
            val s = hsv[1]
            val v = hsv[2]

            when {
                // Fondos claros: paredes, cielo, mesa blanca
                s < 0.10f && v > 0.72f -> other++
                // Zonas muy oscuras con algo de color: manchas.
                // Se exige saturación para no contar sombras grises o tierra.
                v < 0.12f && s > 0.15f -> dark++
                // Hojas verdes
                h in 65f..175f && s > 0.16f && v > 0.10f -> green++
                // Amarilleo
                h in 40f..65f && s > 0.30f && v > 0.55f -> yellow++
                // Marrones / necrosis
                h in 4f..45f && s > 0.22f && v in 0.12f..0.68f -> brown++
                else -> other++
            }
        }

        val leaf = green + yellow + brown + dark
        if (leaf < 80) return null

        fun pct(value: Int): Float = value * 100f / leaf

        LeafStats(
            green = pct(green),
            yellow = pct(yellow),
            brown = pct(brown),
            dark = pct(dark)
        )
    }.getOrNull()

    fun diagnose(
        stats: LeafStats?,
        soilDry: Boolean,
        yellowLeaves: Boolean,
        brownSpots: Boolean,
        directSun: Boolean
    ): Diagnosis {
        val yellowing = yellowLeaves || (stats != null && stats.yellow >= 9f)
        val browning = brownSpots || (stats != null && stats.brown >= 9f)
        val darkSpots = stats != null && stats.dark >= 12f

        val problems = mutableListOf<String>()
        val tips = mutableListOf<String>()

        if (yellowing) {
            if (soilDry) {
                problems += "Hojas amarillas con tierra seca: probablemente le falta agua."
            } else {
                problems += "Hojas amarillas con tierra húmeda: puede ser exceso de riego o falta de nutrientes."
            }
            tips += "Regá cuando los primeros 2-3 cm de tierra estén secos, no por calendario."
        }

        if (browning) {
            if (directSun) {
                problems += "Manchas marrones con sol directo: puede ser una quemadura."
            } else {
                problems += "Manchas marrones: puede ser un hongo, aire muy seco o riego irregular."
            }
            tips += "Evitá mojar las hojas y mejorá la ventilación del lugar."
        }

        if (darkSpots) {
            problems += "Se ven zonas oscuras: revisá si son manchas blandas, típicas de hongos."
            tips += "Si la mancha está blanda, sacá esa hoja y aislá la planta unos días."
        }

        if (soilDry && !yellowing && !browning) {
            problems += "La tierra se ve seca: toca regar pronto."
            tips += "Regá despacio hasta que salga agua por el drenaje."
        }

        if (problems.isEmpty()) {
            return Diagnosis(
                title = "Se ve saludable",
                message = "No detecté signos claros de problemas. Las hojas se ven verdes y parejas.",
                tips = listOf(
                    "Seguí con el riego habitual de esta planta.",
                    "Revisá el envés de las hojas una vez por semana por si aparecen bichitos.",
                    "Sacale una foto cada tanto y compará: es la mejor forma de detectar cambios."
                ),
                level = 0
            )
        }

        if (yellowing || browning) {
            tips += "Fijate que la maceta tenga drenaje: el agua no debe quedar en el plato."
        }
        tips += "Observá 7 días y sacá una foto nueva para comparar la evolución."

        return Diagnosis(
            title = if (problems.size >= 2) "Hay que prestarle atención" else "Algo para revisar",
            message = problems.joinToString(" "),
            tips = tips.distinct(),
            level = if (problems.size >= 2) 2 else 1
        )
    }
}
