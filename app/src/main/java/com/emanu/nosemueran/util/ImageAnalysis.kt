package com.emanu.nosemueran.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.emanu.nosemueran.data.Species
import com.emanu.nosemueran.data.SpeciesCategory
import kotlin.math.roundToInt

/** Porcentajes de color detectados en las hojas (0..100). */
data class LeafStats(
    val green: Float,
    val yellow: Float,
    val brown: Float,
    val dark: Float
)

/** Proporción de cada familia de color en toda la foto (0..100). */
data class ColorProfile(
    val green: Float,
    val yellow: Float,
    val brown: Float,
    val dark: Float,
    val red: Float,
    val orange: Float,
    val purple: Float,
    val light: Float
)

data class PhotoAnalysis(
    val leaf: LeafStats,
    val colors: ColorProfile
)

data class Diagnosis(
    val title: String,
    val message: String,
    val tips: List<String>,
    val level: Int // 0 = bien, 1 = atención, 2 = urgente
)

/** Respuestas del cuestionario de diagnóstico. */
data class DiagnoseAnswers(
    val soilDry: Boolean = false,
    val overwatered: Boolean = false,
    val yellowLeaves: Boolean = false,
    val brownSpots: Boolean = false,
    val directSun: Boolean = false,
    val deformedLeaves: Boolean = false,
    val bugs: Boolean = false,
    val whitePowder: Boolean = false,
    val softStem: Boolean = false,
    val cold: Boolean = false,
    val recentlyTransplanted: Boolean = false,
    val noDrainage: Boolean = false,
    val fertilized: Boolean = false,
    val holesInLeaves: Boolean = false,
    val spottedFruit: Boolean = false
)

object ImageAnalysis {

    fun analyzeFile(path: String): PhotoAnalysis? = runCatching {
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
        var red = 0
        var orange = 0
        var purple = 0
        var light = 0
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
                s < 0.10f && v > 0.72f -> light++
                // Zonas muy oscuras con algo de color: manchas.
                // Se exige saturación para no contar sombras grises o tierra.
                v < 0.12f && s > 0.15f -> dark++
                // Frutos rojos (tomate, frutilla, ají maduro)
                (h >= 345f || h <= 12f) && s > 0.40f && v > 0.25f -> red++
                // Frutos y raíces naranjas (pimentón, zanahoria, zapallo)
                h in 12f..40f && s > 0.45f && v > 0.45f -> orange++
                // Flores violetas
                h in 260f..330f && s > 0.15f -> purple++
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

        val leafStats = LeafStats(
            green = pct(green),
            yellow = pct(yellow),
            brown = pct(brown),
            dark = pct(dark)
        )

        val total = leaf + red + orange + purple + light + other
        if (total <= 0) return null

        fun share(value: Int): Float = value * 100f / total

        PhotoAnalysis(
            leaf = leafStats,
            colors = ColorProfile(
                green = share(green),
                yellow = share(yellow),
                brown = share(brown),
                dark = share(dark),
                red = share(red),
                orange = share(orange),
                purple = share(purple),
                light = share(light)
            )
        )
    }.getOrNull()

    /**
     * Recomendación rápida a partir de la foto y las respuestas.
     * No identifica especies: cruza señales visibles con lo que contó la persona.
     */
    fun diagnose(
        stats: LeafStats?,
        answers: DiagnoseAnswers,
        species: Species? = null
    ): Diagnosis {
        val yellowing = answers.yellowLeaves || (stats != null && stats.yellow >= 9f)
        val browning = answers.brownSpots || (stats != null && stats.brown >= 9f)
        val darkSpots = stats != null && stats.dark >= 12f

        val problems = mutableListOf<String>()
        val tips = mutableListOf<String>()
        var urgent = false

        if (yellowing) {
            when {
                answers.overwatered -> problems +=
                    "Hojas amarillas después de regar de más: probablemente las raíces se están ahogando."
                answers.soilDry -> problems +=
                    "Hojas amarillas con tierra seca: probablemente le falta agua."
                answers.fertilized -> problems +=
                    "Hojas amarillas con fertilizante reciente: puede ser exceso de sales."
                else -> problems +=
                    "Hojas amarillas con tierra húmeda: puede ser exceso de riego o falta de nutrientes."
            }
            tips += "Regá cuando los primeros 3-4 cm de tierra estén secos, no por calendario."
        }

        if (browning) {
            when {
                answers.directSun -> problems +=
                    "Manchas marrones con sol directo: puede ser una quemadura."
                answers.whitePowder -> problems +=
                    "Manchas marrones con polvo blanco: apunta a un hongo."
                else -> problems +=
                    "Manchas marrones: puede ser un hongo, aire muy seco o riego irregular."
            }
            tips += "Evitá mojar las hojas y mejorá la ventilación del lugar."
        }

        if (answers.whitePowder) {
            problems += "Polvo blanco o gris sobre las hojas: es típico de oídio, un hongo."
            tips += "Sacá las hojas más afectadas y no mojes el follaje al regar."
            tips += "Separala un poco de las otras plantas hasta que mejore."
        }

        if (answers.bugs) {
            problems += "Ves bichitos: pueden ser pulgones, mosquitas blancas o arañuelas."
            tips += "Rociá con agua y una gota de jabón neutro, y volvé a revisar a los 3 días."
            tips += "Revisá el envés de las hojas: ahí suelen esconderse."
        }

        if (answers.holesInLeaves) {
            problems += "Hojas con agujeros: lo más probable es caracoles, babosas u orugas."
            tips += "Revisá de noche con linterna: es cuando salen a comer."
            tips += "Poné una trampa con cerveza o un anillo de ceniza alrededor de la planta."
        }

        if (answers.deformedLeaves) {
            problems += "Hojas nuevas deformadas o rizadas: puede ser pulgón, ácaros o falta de nutrientes."
            tips += "Mirá los brotes con lupa: si hay puntitos que se mueven, son ácaros."
        }

        if (answers.softStem) {
            problems += "Tallos blandos u oscuros: es pudrición, casi siempre por exceso de agua."
            urgent = true
            tips += "Sacá la planta de la maceta, cortá la parte blanda y dejá secar la tierra."
        }

        if (answers.noDrainage) {
            problems += "La maceta no tiene drenaje: el agua se acumula y pudre las raíces."
            tips += "Usá maceta con agujeros o poné una capa de piedras y no dejes agua en el plato."
        }

        if (answers.cold) {
            problems += "Frío o helada: las hojas quemadas o caídas suelen ser por eso."
            tips += "Cubrí la planta de noche con una manta o media botella cortada."
            tips += "Regá a media mañana: el agua fría de la noche empeora el daño."
        }

        if (answers.recentlyTransplanted) {
            problems += "Trasplante reciente: algo de decaimiento es normal los primeros días."
            tips += "Mantené la tierra húmeda sin encharcar y evitá el sol fuerte una semana."
        }

        if (answers.spottedFruit) {
            problems += "Frutos con manchas: en tomate y pimentón suele ser sol directo o riego irregular."
            tips += "Regá parejo y dale algo de sombra a los frutos en las horas de más calor."
        }

        if (darkSpots) {
            problems += "Se ven zonas oscuras: revisá si son manchas blandas, típicas de hongos."
            tips += "Si la mancha está blanda, sacá esa hoja y aislá la planta unos días."
        }

        if (answers.soilDry && !yellowing && !browning) {
            problems += "La tierra se ve seca: toca regar pronto."
            tips += "Regá despacio hasta que salga agua por el drenaje."
        }

        // Consejo según la especie concreta.
        tips += speciesAdvice(species, answers, yellowing, browning)

        if (problems.isEmpty()) {
            return Diagnosis(
                title = "Se ve saludable",
                message = "No detecté signos claros de problemas. Las hojas se ven verdes y parejas.",
                tips = (listOf(
                    "Seguí con el riego habitual de esta planta.",
                    "Revisá el envés de las hojas una vez por semana por si aparecen bichitos.",
                    "Sacale una foto cada tanto y compará: es la mejor forma de detectar cambios."
                ) + speciesAdvice(species, answers, false, false)).distinct(),
                level = 0
            )
        }

        if (yellowing || browning) {
            tips += "Fijate que la maceta tenga drenaje: el agua no debe quedar en el plato."
        }
        tips += "Observá 7 días y sacá una foto nueva para comparar la evolución."

        return Diagnosis(
            title = when {
                urgent -> "Hay que actuar hoy"
                problems.size >= 3 -> "Varios frentes para revisar"
                problems.size >= 2 -> "Hay que prestarle atención"
                else -> "Algo para revisar"
            },
            message = problems.joinToString(" "),
            tips = tips.filter { it.isNotBlank() }.distinct(),
            level = if (urgent || problems.size >= 3) 2 else 1
        )
    }

    private fun speciesAdvice(
        species: Species?,
        answers: DiagnoseAnswers,
        yellowing: Boolean,
        browning: Boolean
    ): String {
        if (species == null) return ""
        return when (species.name) {
            "Tomate", "Tomate cherry" -> when {
                answers.spottedFruit ->
                    "En tomate, la mancha negra en la punta del fruto suele ser pudrición apical: regá parejo y agregá cáscara de huevo molida o calcio."
                browning || answers.whitePowder ->
                    "En tomate, manchas marrones con halo suelen ser tizón: sacá las hojas afectadas y no mojes el follaje."
                else -> "Tutoreá y sacá los chupones para que el aire circule y maduren mejor los frutos."
            }
            "Papa" -> if (yellowing) {
                "En papa, el amarilleo general puede ser falta de nitrógeno o exceso de agua: revisá que el surco drene y aporcá."
            } else {
                "Aporcá con tierra cuando el brote tenga 20 cm: así los tubérculos no se ponen verdes."
            }
            "Lechuga" -> if (answers.holesInLeaves) {
                "En lechuga, los agujeros son casi siempre caracoles o babosas: revisá de noche y poné trampa de cerveza."
            } else {
                "En verano dale media sombra: el sol fuerte la hace subir a flor y amargarse."
            }
            "Zanahoria" -> if (answers.deformedLeaves) {
                "En zanahoria, hojas rizadas pueden ser mosca de la zanahoria: sembrá más ralo y no dejes restos de cultivo."
            } else {
                "La zanahoria necesita tierra suelta y sin piedras: si está dura, regá más parejo."
            }
            "Pimentón", "Ají" -> if (answers.spottedFruit || answers.directSun) {
                "En pimentón y ají, el sol fuerte quema los frutos: dale sombra liviana en las horas de más calor."
            } else {
                "Al pimentón no le gusta el exceso de agua: dejá secar la capa superior entre riegos."
            }
            "Frutilla" -> "A la frutilla ponele paja o pasto seco debajo: evita que los frutos toquen la tierra húmeda."
            "Cebolla", "Ajo" -> "Cebolla y ajo se pudren con agua de más: regá poco y dejá de regar cuando las hojas empiezan a caer."
            "Zapallo", "Zapallito", "Pepino", "Maíz" -> "Los cultivos de fruto necesitan riego parejo y sol: evitá mojar las hojas para no favorecer hongos."
            "Albahaca", "Menta", "Cilantro", "Perejil" -> "Cortá las puntas seguido: las aromáticas se ponen más tupidas y no se ponen amargas."
            else -> when (species.category) {
                SpeciesCategory.Huerta -> "En la huerta, rotates los cultivos: no pongas la misma especie dos temporadas seguidas en el mismo lugar."
                SpeciesCategory.Aromatica -> "Las aromáticas prefieren tierra suelta y poco riego: el exceso de agua les apaga el aroma."
                SpeciesCategory.Frutal -> "A los frutales les gusta el sol pleno y el riego profundo y espaciado."
                SpeciesCategory.Interior -> "Las plantas de interior sufren más por exceso de riego que por falta: revisá la tierra antes de regar."
            }
        }
    }

    /** Texto corto con los porcentajes principales, para mostrar debajo de la foto. */
    fun summary(stats: LeafStats): String {
        val parts = listOfNotNull(
            "verde ${stats.green.roundToInt()}%" to stats.green,
            "amarillo ${stats.yellow.roundToInt()}%" to stats.yellow,
            "marrón ${stats.brown.roundToInt()}%" to stats.brown,
            "zonas oscuras ${stats.dark.roundToInt()}%" to stats.dark
        ).sortedByDescending { it.second }.take(2).map { it.first }
        return parts.joinToString(" y ")
    }
}
