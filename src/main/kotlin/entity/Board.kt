package entity

/**
 *  class for the board of the game Splendor
 *  @param nobleTiles: List of noble cards
 *  @param gems: List of gems that can be taken by players
 * */
class Board (
    /** List of noble cards */
    val nobleTiles: List<NobleTile> = listOf(),

    /**
     *  stack of level one development cards
     *  four revealed level development cards
     *  */
    val levelOneCards: MutableList<DevCard> = mutableListOf(),
    val levelOneOpen: MutableList<DevCard> = mutableListOf(),

    val levelTwoCards: MutableList<DevCard> = mutableListOf(),
    val levelTwoOpen: MutableList<DevCard> = mutableListOf(),

    val levelThreeCards: MutableList<DevCard> = mutableListOf(),
    val levelThreeOpen: MutableList<DevCard> = mutableListOf(),

    /** gems: List of gems that can be taken by players */
    var gems: Map<GemType, Int> = mapOf(
        GemType.RED to 0,
        GemType.GREEN to 0,
        GemType.WHITE to 0,
        GemType.BLACK to 0,
        GemType.BLUE to 0,
        GemType.YELLOW to 0)
)
