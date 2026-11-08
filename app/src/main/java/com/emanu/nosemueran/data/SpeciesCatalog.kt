package com.emanu.nosemueran.data

data class Species(
    val name: String,
    val scientific: String,
    val emoji: String,
    val light: String,
    val water: String,
    val wateringDays: Int,
    val humidity: String,
    val temp: String,
    val difficulty: String,
    val description: String,
    val tips: List<String>
)

/** Catálogo offline de plantas de interior y huerta urbana. */
object SpeciesCatalog {

    val all: List<Species> = listOf(
        Species(
            "Potus", "Epipremnum aureum", "🌿",
            "Luz indirecta", "Cuando la tierra esté seca", 7,
            "Media", "18-28 °C", "Muy fácil",
            "Trepadora resistente y generosa. Ideal para estantes altos, donde sus hojas caen en cascada. Purifica el aire y perdona los olvidos de riego.",
            listOf("Tolera poca luz, pero crece más con luz indirecta.", "Dejá que la tierra se seque entre riegos.", "Podés propagarla en agua con un esqueje con nudo.")
        ),
        Species(
            "Sansevieria", "Dracaena trifasciata", "🪴",
            "Tolera casi todo", "Riego escaso", 14,
            "Baja", "15-30 °C", "Muy fácil",
            "La reina de los que se olvidan de regar. Hojas verticales y duras, perfectas para rincones con poca luz. Tolera sequías largas.",
            listOf("Regá muy poco en invierno, casi una vez al mes.", "Nunca dejes agua estancada en el plato.", "Prefiere macetas ajustadas: le gusta estar apretada.")
        ),
        Species(
            "Monstera", "Monstera deliciosa", "🌱",
            "Luz indirecta abundante", "Riego moderado", 7,
            "Media-alta", "18-27 °C", "Fácil",
            "La clásica de hojas agujereadas. Crece rápido si tiene luz y espacio. Necesita un tutor o guía para trepar.",
            listOf("Rociá sus hojas de vez en cuando.", "Limpiá el polvo de las hojas para que respire.", "Girá la maceta cada tanto para que crezca pareja.")
        ),
        Species(
            "Cactus", "Cactaceae", "🌵",
            "Sol directo", "Riego escaso", 21,
            "Baja", "10-35 °C", "Muy fácil",
            "Guarda agua en su cuerpo y odia el exceso de riego. Necesita mucho sol y un sustrato bien drenado.",
            listOf("Regá solo cuando la tierra esté completamente seca.", "En invierno casi no necesita agua.", "Usá maceta con agujero y sustrato para cactus.")
        ),
        Species(
            "Suculenta", "Echeveria", "🪴",
            "Sol suave", "Riego escaso", 14,
            "Baja", "15-30 °C", "Fácil",
            "Rosetas carnosas que almacenan agua. Perfectas para macetas chicas y ventanas soleadas.",
            listOf("No mojes el centro de la roseta: se pudre.", "Mejor poco riego y mucha luz.", "Se reproduce sola con hijuelos.")
        ),
        Species(
            "Aloe vera", "Aloe vera", "🌵",
            "Sol suave", "Riego escaso", 14,
            "Baja", "15-30 °C", "Muy fácil",
            "Suculenta medicinal de hojas gruesas. Crece en macetas y perdona bastante la sequía.",
            listOf("Regá profundo pero espaciado.", "Evitá el sol fuerte del mediodía en verano.", "Separá los hijuelos cuando estén grandes.")
        ),
        Species(
            "Helecho", "Nephrolepis exaltata", "🌿",
            "Semisombra", "Tierra siempre húmeda", 3,
            "Alta", "16-24 °C", "Media",
            "Hojas verdes y livianas que aman la humedad. Ideal para baños con luz o cocinas.",
            listOf("No dejes que la tierra se seque del todo.", "Rociá las hojas o poné un plato con piedras y agua.", "Evitá el sol directo: quema sus frondas.")
        ),
        Species(
            "Ficus lira", "Ficus lyrata", "🌳",
            "Luz indirecta abundante", "Riego moderado", 7,
            "Media", "18-26 °C", "Media",
            "Hojas grandes con forma de violín. Elegante y algo caprichoso con los cambios de lugar.",
            listOf("No lo muevas de sitio una vez que está bien.", "Regá cuando la capa superior esté seca.", "Evitá corrientes de aire frío.")
        ),
        Species(
            "Ficus elastica", "Ficus elastica", "🌳",
            "Luz indirecta", "Riego moderado", 7,
            "Media", "18-27 °C", "Fácil",
            "Hojas gruesas y brillantes, muy resistente. Crece alto y se adapta bien a interiores.",
            listOf("Limpiá las hojas con un paño húmedo.", "Riego parejo: ni encharcar ni dejar secar del todo.", "Rota la maceta para una copa pareja.")
        ),
        Species(
            "Cuna de Moisés", "Spathiphyllum", "🌼",
            "Semisombra", "Riego frecuente", 5,
            "Alta", "18-25 °C", "Fácil",
            "Avisa cuando tiene sed: baja las hojas. Con riego vuelve como si nada. Florece con flores blancas.",
            listOf("Si las hojas caen, revisá la tierra: suele ser sed.", "Rociá hojas en ambientes con calefacción.", "No la pongas al sol directo.")
        ),
        Species(
            "Orquídea", "Phalaenopsis", "🌸",
            "Luz filtrada", "Riego por inmersión", 7,
            "Alta", "18-27 °C", "Media",
            "Flores que duran meses. Vive en corteza, no en tierra común, y necesita aire en las raíces.",
            listOf("Sumergí la maceta 10 minutos y dejá escurrir.", "No mojes el centro de las hojas.", "Cortá la vara cuando se seque, no antes.")
        ),
        Species(
            "Bambú de la suerte", "Dracaena sanderiana", "🎍",
            "Luz indirecta", "Agua limpia o tierra húmeda", 7,
            "Media", "18-28 °C", "Muy fácil",
            "Tallos verdes que crecen en agua o tierra. Muy resistente y de bajo mantenimiento.",
            listOf("Si está en agua, cambiala cada 2 semanas.", "Evitá el sol directo: amarillea los tallos.", "Usá agua sin cloro, reposada un día.")
        ),
        Species(
            "Calathea", "Goeppertia orbifolia", "🍃",
            "Luz filtrada", "Tierra húmeda", 4,
            "Muy alta", "18-26 °C", "Alta",
            "Hojas decoradas que se mueven con la luz. Es exigente con la humedad ambiental.",
            listOf("Mantené la tierra húmeda pero sin charcos.", "Usá humidificador o rociá seguido.", "Evitá agua dura: mancha las hojas.")
        ),
        Species(
            "Pilea", "Pilea peperomioides", "🪴",
            "Luz indirecta", "Riego moderado", 7,
            "Media", "18-26 °C", "Fácil",
            "Hojas redondas como monedas. Le encanta girar hacia la luz y regala hijuelos.",
            listOf("Girá la maceta una vez por semana.", "Regá cuando la tierra esté casi seca.", "Trasplantá los hijuelos cuando tengan 5 cm.")
        ),
        Species(
            "Tradescantia", "Tradescantia zebrina", "🌿",
            "Luz indirecta", "Riego moderado", 6,
            "Media", "16-28 °C", "Muy fácil",
            "Hojas rayadas violetas y verdes. Crece rapidísimo y se propaga con solo un tallo.",
            listOf("Podá para que no se vacíe por dentro.", "Un esqueje en agua enraíza en días.", "El sol fuerte decolora las hojas.")
        ),
        Species(
            "Begonia", "Begonia rex", "🌺",
            "Semisombra", "Riego moderado", 5,
            "Alta", "18-24 °C", "Media",
            "Follaje de colores increíbles. Le gusta la humedad ambiental y la luz suave.",
            listOf("No mojes las hojas: manchas y hongos.", "Regá desde abajo con plato.", "Buena ventilación sin corrientes.")
        ),
        Species(
            "Geranio", "Pelargonium hortorum", "🌺",
            "Sol directo", "Riego moderado", 4,
            "Media", "15-28 °C", "Fácil",
            "Clásico de balcones. Florece casi todo el año con sol y riegos regulares.",
            listOf("Sacá las flores secas para que siga floreciendo.", "Regá temprano, evitá mojar las hojas.", "Podá a fines de invierno.")
        ),
        Species(
            "Lavanda", "Lavandula angustifolia", "💜",
            "Sol directo", "Riego escaso", 10,
            "Baja", "15-30 °C", "Fácil",
            "Aroma relajante y flores violetas. Necesita sol, aire y poca agua.",
            listOf("Sustrato arenoso y muy drenado.", "No la riegues en exceso: se pudre.", "Podá después de la floración.")
        ),
        Species(
            "Menta", "Mentha spicata", "🌿",
            "Sol o semisombra", "Riego frecuente", 3,
            "Media", "15-26 °C", "Muy fácil",
            "Crece sin parar y se usa en infusiones. Mejor en maceta propia: invade todo.",
            listOf("Cortá las puntas para que se ramifique.", "No dejes que se seque la tierra.", "Separala de otras plantas: se expande.")
        ),
        Species(
            "Albahaca", "Ocimum basilicum", "🌿",
            "Sol", "Riego frecuente", 2,
            "Media", "18-30 °C", "Fácil",
            "Aromática de verano. Con sol y agua constante da hojas todo el tiempo.",
            listOf("Cortá las flores para que no se ponga amarga.", "Regá a diario en verano.", "No tolera el frío ni las heladas.")
        ),
        Species(
            "Romero", "Salvia rosmarinus", "🌿",
            "Sol directo", "Riego escaso", 7,
            "Baja", "10-30 °C", "Fácil",
            "Perenne, aromático y muy resistente. Ideal para macetas grandes al sol.",
            listOf("Mejor secarlo de más que de menos.", "Sustrato con piedra y buen drenaje.", "Podá las puntas para mantenerlo compacto.")
        ),
        Species(
            "Tomate cherry", "Solanum lycopersicum", "🍅",
            "Sol directo", "Riego frecuente", 2,
            "Media", "18-30 °C", "Media",
            "Da tomates chicos y dulces en balcón. Necesita sol pleno, tutor y riego constante.",
            listOf("Regá parejo: los cambios bruscos parten los frutos.", "Tutoreá la planta desde chica.", "Fertilizá cada 15 días en fructificación.")
        ),
        Species(
            "Palmera areca", "Dypsis lutescens", "🌴",
            "Luz indirecta", "Riego frecuente", 5,
            "Alta", "18-28 °C", "Fácil",
            "Hojas largas y tropicales. Suma frescura y verde a cualquier rincón con luz.",
            listOf("Rociá sus hojas en ambientes secos.", "No la pongas cerca de la calefacción.", "Riego moderado pero sin dejar secar del todo.")
        ),
        Species(
            "Dracaena", "Dracaena marginata", "🪴",
            "Luz indirecta", "Riego moderado", 10,
            "Media", "18-28 °C", "Fácil",
            "Tallo fino y hojas con bordes rojizos. Muy resistente y de crecimiento lento.",
            listOf("Tolera olvidos de riego.", "Usá agua sin cloro o reposada.", "Evitá el sol fuerte directo.")
        ),
        Species(
            "Filodendro", "Philodendron hederaceum", "🌿",
            "Luz indirecta", "Riego moderado", 7,
            "Media", "18-28 °C", "Fácil",
            "Trepadora de hojas acorazonadas, pariente del potus. Crece bien en semisombra.",
            listOf("Dejá secar la capa superior antes de regar.", "Guíala con un tutor para que trepe.", "Es tóxica para mascotas: cuidado.")
        ),
        Species(
            "Anturio", "Anthurium andraeanum", "🌺",
            "Luz indirecta", "Riego moderado", 7,
            "Alta", "20-28 °C", "Media",
            "Flores rojas brillantes y hojas verdes. Le gusta el calor y la humedad.",
            listOf("No mojes la flor: se mancha.", "Rociá alrededor, no sobre las flores.", "Sustrato suelto y con buen drenaje.")
        ),
        Species(
            "Violeta africana", "Saintpaulia ionantha", "🌸",
            "Luz filtrada", "Riego por debajo", 4,
            "Media", "18-25 °C", "Media",
            "Flores pequeñas y abundantes. Prefiere macetas chicas y riego por el plato.",
            listOf("Regá por el plato para no mojar las hojas.", "Luz suave todo el año.", "Sacá flores y hojas secas.")
        ),
        Species(
            "Rosa", "Rosa chinensis", "🌹",
            "Sol directo", "Riego frecuente", 4,
            "Media", "15-28 °C", "Media",
            "Flores clásicas de balcón. Necesita sol, riego y una poda para repetir floración.",
            listOf("Regá en la base, nunca las flores.", "Revisá los brotes nuevos por pulgones.", "Podá en invierno para fortalecer.")
        ),
        Species(
            "Malamadre", "Chlorophytum comosum", "🌿",
            "Luz indirecta", "Riego moderado", 7,
            "Media", "15-28 °C", "Muy fácil",
            "Cuelga hijuelos por todas partes. Una de las plantas más agradecidas para empezar.",
            listOf("Los hijuelos se pueden plantar en agua.", "Tolera olvidos, pero crece más con riego.", "Puntas marrones = agua muy dura o aire seco.")
        )
    )

    fun find(name: String): Species? =
        all.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }

    fun emojiFor(speciesName: String): String =
        find(speciesName)?.emoji ?: "🌿"
}
