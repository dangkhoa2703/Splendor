package entity

/**
 *  class for the board of the game Splendor
 *  @param nobleTiles: List of noble cards
 *  @param gems: List of gems that can be taken by players
 *  @property levelOneCards: stack of level one development cards (forty cards)
 *  @property levelOneOpen: maximum four revealed level one development cards
 *  @property levelTwoCards: stack of level one development cards (thirty cards)
 *  @property levelTwoOpen: maximum four revealed level one development cards
 *  @property levelThreeCards: stack of level one development cards (twenty cards)
 *  @property levelThreeOpen: maximum four revealed level one development cards
 *  @property gems: list of gems that can be taken by players
 * */
class Board (
    /** List of noble cards */
    val nobleTiles: MutableList<NobleTile> = mutableListOf(),

    val levelOneCards: MutableList<DevCard> = mutableListOf(),
    val levelOneOpen: MutableList<DevCard> = mutableListOf(),

    val levelTwoCards: MutableList<DevCard> = mutableListOf(),
    val levelTwoOpen: MutableList<DevCard> = mutableListOf(),

    val levelThreeCards: MutableList<DevCard> = mutableListOf(),
    val levelThreeOpen: MutableList<DevCard> = mutableListOf(),

    var gems: MutableMap<GemType,Int> = mutableMapOf(
        GemType.RED to 7,
        GemType.GREEN to 7,
        GemType.WHITE to 7,
        GemType.BLACK to 7,
        GemType.BLUE to 7,
        GemType.YELLOW to 5)
) {

    /** Clones the Board for simulating turns */
    fun cloneForSimulation(): Board = Board(this.nobleTiles.toMutableList(),
        levelOneOpen = levelOneOpen.toMutableList(),
        levelTwoOpen = levelTwoOpen.toMutableList(),
        levelThreeOpen = levelThreeOpen.toMutableList(),
        gems = gems.toMutableMap())

}
