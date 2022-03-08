package entity

/**
 *  class for the GameState
 *  @param currentPlayer: player whose turn it is
 *  @param playerList: list of players participating in the current game
 *  @param board: current game board
 * */
class GameState (
    val currentPlayer: Player,
    val playerList: List<Player>,
    val board: Board
    )
{
    var next: GameState = this
    var previous: GameState = this

    /** query whether GameState has a successor */
    fun hasNext(): Boolean{
        return (next != this)
    }

    /** query whether GameState has a predecessor */
    fun hasPrevious(): Boolean{
        return (previous != this)
    }
}