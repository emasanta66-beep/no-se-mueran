package com.emanu.nosemueran.data

enum class SpeciesCategory(val label: String, val emoji: String) {
    Interior("Interior", "🪴"),
    Aromatica("Aromáticas", "🌿"),
    Huerta("Huerta", "🥬"),
    Frutal("Frutales", "🍓")
}

enum class SowingMethod(val label: String) {
    Directa("Siembra directa"),
    Almacigo("Almácigo y trasplante"),
    Ambas("Directa o almácigo")
}

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
    val tips: List<String>,
    val category: SpeciesCategory = SpeciesCategory.Interior,
    /** Meses (1 a 12) para sembrar en la zona central de Chile. */
    val sowMonths: List<Int> = emptyList(),
    val harvestDays: Int? = null,
    val sowingMethod: SowingMethod? = null,
    val sowingNote: String = ""
) {
    val sowsInCentralChile: Boolean get() = sowMonths.isNotEmpty()
}

/** Catálogo offline de plantas de interior, aromáticas, huerta y frutales. */
object SpeciesCatalog {

    val all: List<Species> = listOf(
        // ---------- Interior ----------
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
        ),

        // ---------- Aromáticas ----------
        Species(
            "Lavanda", "Lavandula angustifolia", "💜",
            "Sol directo", "Riego escaso", 10,
            "Baja", "15-30 °C", "Fácil",
            "Aroma relajante y flores violetas. Necesita sol, aire y poca agua.",
            listOf("Sustrato arenoso y muy drenado.", "No la riegues en exceso: se pudre.", "Podá después de la floración."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(3, 4, 5, 9, 10),
            harvestDays = 120,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Mejor por plantines o esquejes en otoño y primavera."
        ),
        Species(
            "Menta", "Mentha spicata", "🌿",
            "Sol o semisombra", "Riego frecuente", 3,
            "Media", "15-26 °C", "Muy fácil",
            "Crece sin parar y se usa en infusiones. Mejor en maceta propia: invade todo.",
            listOf("Cortá las puntas para que se ramifique.", "No dejes que se seque la tierra.", "Separala de otras plantas: se expande."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(3, 4, 5, 8, 9, 10, 11),
            harvestDays = 60,
            sowingMethod = SowingMethod.Ambas,
            sowingNote = "Con un esqueje con raíz prende enseguida."
        ),
        Species(
            "Albahaca", "Ocimum basilicum", "🌿",
            "Sol", "Riego frecuente", 2,
            "Media", "18-30 °C", "Fácil",
            "Aromática de verano. Con sol y agua constante da hojas todo el tiempo.",
            listOf("Cortá las flores para que no se ponga amarga.", "Regá a diario en verano.", "No tolera el frío ni las heladas."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(9, 10, 11, 12),
            harvestDays = 60,
            sowingMethod = SowingMethod.Ambas,
            sowingNote = "Sembrá después de las últimas heladas."
        ),
        Species(
            "Romero", "Salvia rosmarinus", "🌿",
            "Sol directo", "Riego escaso", 7,
            "Baja", "10-30 °C", "Fácil",
            "Perenne, aromático y muy resistente. Ideal para macetas grandes al sol.",
            listOf("Mejor secarlo de más que de menos.", "Sustrato con piedra y buen drenaje.", "Podá las puntas para mantenerlo compacto."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(3, 4, 9, 10),
            harvestDays = 120,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Es lento: conviene comprar plantín o esqueje."
        ),
        Species(
            "Cilantro", "Coriandrum sativum", "🌿",
            "Sol suave o semisombra", "Riego frecuente", 3,
            "Media", "15-27 °C", "Muy fácil",
            "De crecimiento rápido, ideal para macetas y huertas chicas. Se corta y vuelve a brotar.",
            listOf("Sembrá cada 3 semanas para tener siempre.", "Cortá las hojas de afuera hacia adentro.", "El calor fuerte lo hace florecer rápido."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(3, 4, 5, 8, 9, 10),
            harvestDays = 50,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "No le gusta el trasplante: sembralo donde va a quedar."
        ),
        Species(
            "Perejil", "Petroselinum crispum", "🌿",
            "Sol suave o semisombra", "Riego moderado", 4,
            "Media", "15-27 °C", "Fácil",
            "Bienal y muy rendidor. Con un par de plantas alcanza para la cocina.",
            listOf("Tarda en germinar: mantené la tierra húmeda.", "Cortá los tallos externos.", "En verano dale algo de sombra."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(3, 4, 5, 8, 9, 10, 11),
            harvestDays = 75,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Remojá las semillas un día antes para acelerar."
        ),
        Species(
            "Orégano", "Origanum vulgare", "🌿",
            "Sol directo", "Riego escaso", 7,
            "Baja", "15-30 °C", "Muy fácil",
            "Perenne, rústico y aromático. Con sol y poco riego dura años.",
            listOf("Cosechá antes de que florezca.", "Podá para que se mantenga tupido.", "Tolera tierra pobre."),
            category = SpeciesCategory.Aromatica,
            sowMonths = listOf(9, 10),
            harvestDays = 90,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Se puede dividir una planta grande en primavera."
        ),

        // ---------- Huerta ----------
        Species(
            "Tomate", "Solanum lycopersicum", "🍅",
            "Sol directo", "Riego frecuente", 2,
            "Media", "18-30 °C", "Media",
            "El rey de la huerta de primavera y verano. Necesita sol pleno, tutor y riego parejo. Da frutos desde el verano hasta que baje el calor.",
            listOf("Tutoreá con caña o cordel desde chica.", "Regá en la base y parejo: evita que los frutos se partan.", "Sacá los chupones y las hojas de abajo para que circule aire."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(7, 8, 9),
            harvestDays = 120,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Almácigo en julio-agosto bajo techo; trasplantá en septiembre cuando no haya heladas."
        ),
        Species(
            "Tomate cherry", "Solanum lycopersicum", "🍅",
            "Sol directo", "Riego frecuente", 2,
            "Media", "18-30 °C", "Fácil",
            "Da tomates chicos y dulces en balcón o huerta. Más rústico que el tomate grande y muy productivo.",
            listOf("Regá parejo: los cambios bruscos parten los frutos.", "Tutoreá la planta desde chica.", "Fertilizá cada 15 días en fructificación."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(7, 8, 9),
            harvestDays = 100,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Ideal para macetas grandes de 20 litros o más."
        ),
        Species(
            "Papa", "Solanum tuberosum", "🥔",
            "Sol directo", "Riego moderado", 4,
            "Media", "10-25 °C", "Fácil",
            "Se siembra con papas semilla brotadas y se cosecha a los 4 meses. Lo importante es aporcar y no dejarla con agua estancada.",
            listOf("Usá papas semilla certificadas, no las del supermercado.", "Aporcá con tierra cuando el brote tenga 20 cm.", "No la riegues en exceso: se pudre el tubérculo."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(8, 9),
            harvestDays = 120,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Enterrá los tubérculos brotados a 10 cm, con ojo para arriba. Cosecha en diciembre-enero."
        ),
        Species(
            "Lechuga", "Lactuca sativa", "🥬",
            "Sol suave", "Riego frecuente", 2,
            "Media", "10-25 °C", "Muy fácil",
            "La hortaliza más agradecida para empezar. Con media sombra en verano y riego constante se cosecha en dos meses.",
            listOf("Sembrá pocas cada 15 días para tener siempre.", "En verano dale sombra: el sol fuerte la hace florecer.", "Regá temprano en la mañana."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
            harvestDays = 60,
            sowingMethod = SowingMethod.Ambas,
            sowingNote = "Se da todo el año en Santiago; rinde mejor en otoño y primavera."
        ),
        Species(
            "Pimentón", "Capsicum annuum", "🫑",
            "Sol directo", "Riego moderado", 3,
            "Media", "18-30 °C", "Media",
            "Necesita calor y mucho sol. En la zona central conviene almácigo temprano y trasplante después de las heladas.",
            listOf("Trasplantá cuando el suelo esté tibio.", "Tutoreá si carga mucho fruto.", "Evitá mojar las hojas para prevenir hongos."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(7, 8, 9),
            harvestDays = 130,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Almácigo julio-agosto; cosecha desde enero."
        ),
        Species(
            "Ají", "Capsicum frutescens", "🌶",
            "Sol directo", "Riego moderado", 3,
            "Media", "18-32 °C", "Media",
            "Pariente del pimentón, más picante y resistente. Le gusta el calor y da frutos hasta el otoño.",
            listOf("Almácigo temprano y trasplante con calor.", "No lo riegues de más: se cae la flor.", "Cosechá los ajíes maduros para que siga produciendo."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(7, 8, 9),
            harvestDays = 130,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Mismo manejo que el pimentón."
        ),
        Species(
            "Zanahoria", "Daucus carota", "🥕",
            "Sol directo", "Riego moderado", 4,
            "Media", "10-25 °C", "Fácil",
            "Se siembra directa y necesita tierra suelta, sin piedras, para que la raíz crezca derecha.",
            listOf("Sembrá ralo y raleá a 5 cm cuando tenga 5 cm de alto.", "Mantené la tierra húmeda hasta que nazca.", "No uses abono fresco: deforma la raíz."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(1, 2, 3, 4, 8, 9, 10, 11, 12),
            harvestDays = 90,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Evitá sembrar en pleno verano: prefiere fin de verano y primavera."
        ),
        Species(
            "Acelga", "Beta vulgaris var. cicla", "🍃",
            "Sol o semisombra", "Riego frecuente", 3,
            "Media", "10-25 °C", "Muy fácil",
            "Corta y vuelve: con pocas plantas tenés hojas todo el año. Muy resistente a las heladas suaves.",
            listOf("Cortá las hojas externas dejando el centro.", "Riego constante para hojas tiernas.", "Se da bien casi todo el año."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
            harvestDays = 60,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "En la zona central va todo el año, mejor de otoño a primavera."
        ),
        Species(
            "Espinaca", "Spinacia oleracea", "🍃",
            "Sol suave", "Riego frecuente", 2,
            "Media", "5-22 °C", "Fácil",
            "Prefiere el fresco: en Santiago rinde mejor en otoño e invierno. Cosecha rápida y continua.",
            listOf("Sembrala en otoño o fines de invierno.", "No la dejes secar: se pone amarga.", "Cosechá antes de que florezca."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(3, 4, 5, 6, 8, 9),
            harvestDays = 50,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "El calor fuerte la hace subir a flor; evitá sembrarla en verano."
        ),
        Species(
            "Rabanito", "Raphanus sativus", "🌱",
            "Sol o semisombra", "Riego frecuente", 2,
            "Media", "10-25 °C", "Muy fácil",
            "El cultivo más rápido de la huerta: en un mes ya se cosecha. Perfecto para sembrar entre otras plantas.",
            listOf("Mantené la tierra húmeda y pareja.", "Raleá para que la raíz engorde.", "No lo dejes mucho: se pone picante y duro."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(1, 2, 3, 4, 5, 8, 9, 10, 11, 12),
            harvestDays = 30,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Ideal para iniciar a los chicos en la huerta."
        ),
        Species(
            "Betarraga", "Beta vulgaris", "🌱",
            "Sol directo", "Riego moderado", 3,
            "Media", "10-25 °C", "Fácil",
            "Raíz dulce y hojas comestibles. Necesita tierra suelta y riego parejo para no endurecerse.",
            listOf("Remojá las semillas antes de sembrar.", "Raleá a 10 cm cuando tenga 5 cm.", "Aprovechá las hojas tiernas en ensalada."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(3, 4, 5, 6, 8, 9, 10, 11),
            harvestDays = 70,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Cada 'semilla' es un racimo: raleá para dejar una planta."
        ),
        Species(
            "Cebolla", "Allium cepa", "🧅",
            "Sol directo", "Riego moderado", 4,
            "Baja", "10-25 °C", "Fácil",
            "Se siembra en almácigo y se trasplanta en invierno. Necesita sol para formar el bulbo.",
            listOf("Trasplantá cuando tenga el grosor de un lápiz.", "Dejá de regar cuando el tallo se dobla.", "No la siembres muy profunda."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(3, 4, 5),
            harvestDays = 150,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Cosecha a fines de primavera, cuando las hojas se caen."
        ),
        Species(
            "Ajo", "Allium sativum", "🧄",
            "Sol directo", "Riego escaso", 7,
            "Baja", "10-25 °C", "Muy fácil",
            "Se planta con dientes de ajo, no con semilla. Ocupa la huerta en invierno y se cosecha a fin de año.",
            listOf("Enterrá cada diente con la punta hacia arriba.", "No lo riegues en exceso en invierno.", "Dejá de regar 2 semanas antes de cosechar."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(4, 5),
            harvestDays = 180,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Cosecha cuando las hojas se ponen amarillas, en noviembre-diciembre."
        ),
        Species(
            "Zapallo", "Cucurbita maxima", "🎃",
            "Sol directo", "Riego moderado", 4,
            "Media", "15-30 °C", "Fácil",
            "Ocupa mucho espacio, pero rinde muchísimo. Necesita sol y un rincón con lugar para que se extienda.",
            listOf("Dale al menos 1 m² por planta.", "Regá la base, no las hojas.", "Cortá el brote principal para que ramifique."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(9, 10),
            harvestDays = 120,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Siembra después de las heladas; cosecha en otoño con el tallo seco."
        ),
        Species(
            "Zapallito", "Cucurbita pepo", "🌱",
            "Sol directo", "Riego frecuente", 2,
            "Media", "15-30 °C", "Fácil",
            "Produce en dos meses y no para. Ideal para huertas chicas con buen sol.",
            listOf("Cosechá chico: mientras más cortás, más produce.", "Riego constante para frutos tiernos.", "Evitá mojar las hojas."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(9, 10, 11, 12),
            harvestDays = 60,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Sembralo en primavera, después de las heladas."
        ),
        Species(
            "Pepino", "Cucumis sativus", "🥒",
            "Sol directo", "Riego frecuente", 2,
            "Media", "18-30 °C", "Fácil",
            "Trepador y productivo. Con tutor ahorra espacio y mantiene los frutos limpios.",
            listOf("Tutoreá para que trepe.", "Regá a diario en verano.", "Cosechá seguido para que siga dando."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(9, 10, 11),
            harvestDays = 65,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Necesita calor: esperá a que pase el frío."
        ),
        Species(
            "Arvejas", "Pisum sativum", "🌱",
            "Sol o semisombra", "Riego moderado", 4,
            "Media", "5-22 °C", "Fácil",
            "Fijan nitrógeno en la tierra y mejoran el suelo. Se dan bien en otoño e invierno suave.",
            listOf("Sembralas directo, no les gusta el trasplante.", "Poné ramas o malla para que trepen.", "Cosechá las vainas tiernas seguido."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(4, 5, 6, 7, 8),
            harvestDays = 90,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "En Santiago, la mejor siembra es de otoño y fines de invierno."
        ),
        Species(
            "Habas", "Vicia faba", "🌱",
            "Sol directo", "Riego moderado", 5,
            "Media", "5-22 °C", "Muy fácil",
            "Cultivo de invierno, resistente al frío. Mejora la tierra y da vainas en primavera.",
            listOf("Sembrá en otoño, a 20 cm entre plantas.", "Aporcá cuando el tallo crezca.", "Cortá la punta para que engorden las vainas."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(4, 5, 6),
            harvestDays = 150,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Tolera heladas suaves; cosecha en primavera."
        ),
        Species(
            "Maíz", "Zea mays", "🌽",
            "Sol directo", "Riego frecuente", 3,
            "Media", "15-32 °C", "Media",
            "Necesita espacio y sol pleno. Se siembra en grupos para que la polinización funcione.",
            listOf("Sembrá en bloque, no en una sola fila.", "Riego abundante en floración.", "Aporcá para que no se caiga con el viento."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(9, 10),
            harvestDays = 100,
            sowingMethod = SowingMethod.Directa,
            sowingNote = "Requiere un rincón con mucho sol y buen espacio."
        ),
        Species(
            "Brócoli", "Brassica oleracea var. italica", "🥦",
            "Sol directo", "Riego moderado", 3,
            "Media", "10-24 °C", "Media",
            "Cultivo de otoño y primavera. Necesita tierra abonada y riego parejo.",
            listOf("Trasplantá cuando tenga 4 hojas.", "Aboná con compost antes de trasplantar.", "Cosechá el brote central antes de que se abran las flores."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(3, 4, 5, 6, 8, 9),
            harvestDays = 90,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Evitá el calor fuerte: se da mejor en otoño y primavera."
        ),
        Species(
            "Repollo", "Brassica oleracea var. capitata", "🥬",
            "Sol directo", "Riego moderado", 3,
            "Media", "10-24 °C", "Fácil",
            "Hortaliza de otoño-invierno, rústica y rendidora. Ocupa lugar pero dura mucho en la heladera.",
            listOf("Trasplantá con tierra firme.", "Riego parejo para que forme cabeza.", "Revisá el envés por orugas."),
            category = SpeciesCategory.Huerta,
            sowMonths = listOf(3, 4, 5, 6, 8, 9),
            harvestDays = 100,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Se da bien en la zona central de otoño a primavera."
        ),

        // ---------- Frutales ----------
        Species(
            "Frutilla", "Fragaria × ananassa", "🍓",
            "Sol directo", "Riego frecuente", 3,
            "Media", "12-26 °C", "Fácil",
            "Se planta en otoño y produce en primavera. Se multiplica sola con estolones.",
            listOf("Plantá con el cuello a nivel del suelo.", "Poné paja o pasto seco bajo los frutos.", "Cortá los estolones si querés más fruta."),
            category = SpeciesCategory.Frutal,
            sowMonths = listOf(3, 4, 5),
            harvestDays = 120,
            sowingMethod = SowingMethod.Almacigo,
            sowingNote = "Mejor comprar plantines en otoño; cosecha desde octubre."
        )
    )

    fun find(name: String): Species? =
        all.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }

    fun emojiFor(speciesName: String): String =
        find(speciesName)?.emoji ?: "🌿"

    fun byCategory(category: SpeciesCategory): List<Species> =
        all.filter { it.category == category }.sortedBy { it.name }

    /** Especies que se siembran en el mes indicado (1 = enero) en la zona central. */
    fun sowableIn(month: Int): List<Species> =
        all.filter { month in it.sowMonths }.sortedBy { it.name }

    fun search(query: String): List<Species> {
        val q = query.trim()
        if (q.isBlank()) return all
        return all.filter {
            it.name.contains(q, ignoreCase = true) ||
                it.scientific.contains(q, ignoreCase = true)
        }
    }
}
