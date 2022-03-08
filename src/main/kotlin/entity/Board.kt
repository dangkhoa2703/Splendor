package entity

class Board {

    val nobleTiles: List<NobleTile> = listOf()
    val gems: Map<GemType, Int> = mapOf(
        GemType.RED to 0,
        GemType.GREEN to 0,
        GemType.WHITE to 0,
        GemType.BLACK to 0,
        GemType.BLUE to 0,
        GemType.YELLOW to 0)

    val levelOneCards: List<DevCard> = listOf()
    val levelOneOpen: List<DevCard> = listOf()

    val levelTwoCards: List<DevCard> = listOf()
    val levelTwoOpen: List<DevCard> = listOf()

    val levelThreeCards: List<DevCard> = listOf()
    val levelThreeOpen: List<DevCard> = listOf()

}