package com.emanu.nosemueran.data

/** Calendario orientativo de siembra para la zona central de Chile (Santiago y alrededores). */
data class MonthGuide(
    val month: Int,
    val name: String,
    val tip: String
)

object SowingCalendar {

    const val ZONE_NOTE =
        "Calendario orientativo para la zona central de Chile (Santiago). " +
            "Si estás en otra zona, corré las fechas: más al sur, unas semanas después; más al norte, antes."

    val months: List<MonthGuide> = listOf(
        MonthGuide(
            1, "Enero",
            "Pleno verano y riego diario. Cosechá tomate, zapallito, pepino y lechuga. " +
                "Regá temprano o al atardecer y poné media sombra a las hojas tiernas."
        ),
        MonthGuide(
            2, "Febrero",
            "Mes de cosecha. Guardá semillas de las mejores plantas de tomate y pimentón. " +
                "Empezá a preparar la tierra para la huerta de otoño."
        ),
        MonthGuide(
            3, "Marzo",
            "Arranca la huerta de otoño: acelga, espinaca, rabanito, betarraga, lechuga y cebolla en almácigo. " +
                "Bajan las temperaturas y el riego puede espaciarse."
        ),
        MonthGuide(
            4, "Abril",
            "Mes de ajo, habas y arvejas. Aprovechá para abonar con compost. " +
                "Empezan a aparecer caracoles y babosas: revisá de noche."
        ),
        MonthGuide(
            5, "Mayo",
            "Se vienen las heladas. Cubrí las lechugas y cosechá lo que quede de tomate y pimentón. " +
                "Sembrá habas y arvejas para que la tierra trabaje en invierno."
        ),
        MonthGuide(
            6, "Junio",
            "Con heladas la huerta crece lento. Protegé lo sembrado, regá poco y a media mañana. " +
                "Buen momento para planificar y preparar almácigos bajo techo."
        ),
        MonthGuide(
            7, "Julio",
            "Almácigo de tomate, pimentón y ají en lugar abrigado o invernadero. " +
                "Seguí cosechando acelga, espinaca y habas."
        ),
        MonthGuide(
            8, "Agosto",
            "Último mes de heladas fuertes. Sembrá papa temprana, zanahoria, lechuga y rabanito. " +
                "Endurecé los almácigos sacándolos al sol unas horas por día."
        ),
        MonthGuide(
            9, "Septiembre",
            "Primavera: trasplantá tomate, pimentón y ají. Sembrá zapallo, zapallito, maíz y pepino. " +
                "Ojo con las heladas tardías: tené una manta a mano."
        ),
        MonthGuide(
            10, "Octubre",
            "Todo crece. Riego constante, tutores para tomate y pepino, y aporque de la papa. " +
                "Revisá el envés de las hojas por pulgones."
        ),
        MonthGuide(
            11, "Noviembre",
            "Riego diario y control de pulgones. Aporcá las papas y cosechá rabanito, lechuga y acelga. " +
                "Poné sombra liviana a la lechuga si el sol pega fuerte."
        ),
        MonthGuide(
            12, "Diciembre",
            "Cosecha de tomate, zapallito y frutilla. Regá temprano y no dejes que la tierra se seque. " +
                "Empezá a pensar en la huerta del próximo otoño."
        )
    )

    fun forMonth(month: Int): MonthGuide = months[(month - 1).coerceIn(0, 11)]

    val monthNames: List<String> = months.map { it.name }

    fun shortName(month: Int): String = monthNames[(month - 1).coerceIn(0, 11)].take(3)
}
