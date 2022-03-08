package entity

/**
 *  class for a player object
 *  @param name: name of the player
 *  @param score: current score (prestigePoints) of the player
 *  @param bonus: available bonuses of the player
 *  @param playerType: see enum PlayerType
 *  @param gems: gems the player currently has
 *  @param nobleTiles: nobleTiles who visited the player
 *  @param reservedCards: currently reserved devCards
 *  @param devCards: purchased devCards
 * */
class Player (
    val name : String,
    val playerType: PlayerType
    )
{
    var reservedCards : MutableList<DevCard> = mutableListOf()
    var nobleTiles : MutableList<NobleTile> = mutableListOf()
    var score : Int = 0
    var bonus : Map<GemType, Int> = mapOf(
        GemType.RED to 0,
        GemType.GREEN to 0,
        GemType.WHITE to 0,
        GemType.BLACK to 0,
        GemType.BLUE to 0,
        GemType.YELLOW to 0
    )
    var gems : Map<GemType,Int> = mapOf(
        GemType.RED to 0,
        GemType.GREEN to 0,
        GemType.WHITE to 0,
        GemType.BLACK to 0,
        GemType.BLUE to 0,
        GemType.YELLOW to 0)

    var devCards : MutableList<DevCard> = mutableListOf()
}