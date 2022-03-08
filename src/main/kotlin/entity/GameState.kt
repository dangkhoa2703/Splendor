package entity

class GameState(
    val currentPlayer: Player)
{
    var next: GameState = this
    var previous: GameState = this

    fun hasNext(): Boolean{
        return (next != this)
    }

    fun hasPrevious(): Boolean{
        return (previous != this)
    }
}