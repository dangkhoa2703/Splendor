package entity

/**
 *  class for a player object
 *  @param name: name of the player
 *  @property score: current score (prestigePoints) of the player
 *  @param bonus: available bonuses of the player
 *  @param playerType: see enum PlayerType
 *  @param gems: gems the player currently has
 *  @property nobleTiles: nobleTiles who visited the player
 *  @property reservedCards: currently reserved devCards
 *  @property devCards: purchased devCards
 * */
class Player (
    val name : String,
    val playerType: PlayerType,
    val gems : MutableMap<GemType,Int> = mutableMapOf(
        GemType.RED to 0,
        GemType.GREEN to 0,
        GemType.WHITE to 0,
        GemType.BLACK to 0,
        GemType.BLUE to 0,
        GemType.YELLOW to 0),
    val bonus : MutableMap<GemType,Int> = mutableMapOf(
        GemType.RED to 0,
        GemType.GREEN to 0,
        GemType.WHITE to 0,
        GemType.BLACK to 0,
        GemType.BLUE to 0,
        GemType.YELLOW to 0
    ),
    val reservedCards : MutableList<DevCard> = mutableListOf(),
    val nobleTiles : MutableList<NobleTile> = mutableListOf(),
    var score : Int = 0,
    var devCards : MutableList<DevCard> = mutableListOf(),
    var id: Int = 0,
    var hasDoneTurn: Boolean = false
    ) {

    /** Clones a player */
    fun clone() : Player = Player(name, playerType, gems.toMutableMap(),
        bonus.toMutableMap(), reservedCards.toMutableList(), nobleTiles.toMutableList(),
        score, devCards.toMutableList())

    override fun equals(other: Any?): Boolean {
        if (other !is Player)
            return false
        val otherPlayer: Player = other
        return otherPlayer.name.equals(name)
                && otherPlayer.playerType.equals(playerType)
                && otherPlayer.gems.equals(gems)
                && otherPlayer.bonus.equals(bonus)
                && otherPlayer.reservedCards.equals(reservedCards)
                && otherPlayer.nobleTiles.equals(nobleTiles)
                && otherPlayer.score.equals(score)
                && otherPlayer.devCards.equals(devCards)
                && otherPlayer.id.equals(id)
    }

}