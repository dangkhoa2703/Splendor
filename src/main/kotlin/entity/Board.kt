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

    val levelOneCards: MutableList<DevCard> = mutableListOf()
    val levelOneOpen: MutableList<DevCard> = mutableListOf()

    val levelTwoCards: MutableList<DevCard> = mutableListOf()
    val levelTwoOpen: MutableList<DevCard> = mutableListOf()

    val levelThreeCards: MutableList<DevCard> = mutableListOf()
    val levelThreeOpen: MutableList<DevCard> = mutableListOf()

}