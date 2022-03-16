package entity

/**
 *  class for a gameState object
 *  @param currentPlayer: player whose turn it is
 *  @param playerList: all participating players
 *  @param board: current board in this gameState
 *  @property next: following gameState
 *  @property previous: previous gameState
 * */
class GameState(
    val currentPlayer: Player,
    var playerList: List<Player>,
    val board: Board )
{
    var next: GameState = this
    var previous: GameState = this
    var isInitialState: Boolean = false

    /** gets indication whether a gameState follows */
    fun hasNext(): Boolean{
        return (next != this)
    }
    /** gets indication whether a gameState precedes */
    fun hasPrevious(): Boolean{
        return (previous != this)
    }
}