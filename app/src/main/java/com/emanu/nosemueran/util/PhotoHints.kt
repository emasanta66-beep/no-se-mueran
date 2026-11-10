package com.emanu.nosemueran.util

import com.emanu.nosemueran.data.SpeciesCatalog

/**
 * Atajos de especie a partir de los colores de la foto.
 *
 * No es reconocimiento de imagen: la app mira cuánto rojo, naranja, verde,
 * marrón y zonas oscuras hay y propone las especies más probables para esa
 * mezcla. La persona siempre confirma o elige otra del catálogo.
 */
object PhotoHints {

    fun candidates(analysis: PhotoAnalysis, limit: Int = 4): List<String> {
        val c = analysis.colors
        val names = when {
            // Frutos rojos o naranjas: tomate, pimentón, ají, frutilla.
            c.red + c.orange >= 8f ->
                listOf("Tomate", "Pimentón", "Ají", "Frutilla")
            // Mucha tierra o tons marrones y poco verde: raíces y tubérculos.
            c.brown + c.dark >= 40f && c.green < 28f ->
                listOf("Papa", "Zanahoria", "Betarraga", "Rabanito")
            // Verde con amarillo: hojas tiernas.
            c.yellow >= 16f ->
                listOf("Lechuga", "Acelga", "Espinaca", "Repollo")
            // Verde abundante: hojas.
            c.green >= 40f ->
                listOf("Lechuga", "Acelga", "Espinaca", "Albahaca")
            c.green >= 18f ->
                listOf("Tomate", "Lechuga", "Papa", "Zanahoria")
            else ->
                listOf("Lechuga", "Tomate", "Papa", "Zanahoria")
        }
        return names.distinct().filter { SpeciesCatalog.find(it) != null }.take(limit)
    }
}
