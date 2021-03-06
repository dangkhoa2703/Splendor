package entity

/**
 *  class for the game Splendor
 *  @param validGame : indication whether game is eligible for saving a highscore
 *  @param simulationSpeed : specification of the simulation speed
 *  @param currentGameState : see class GameState
 *  @param highscores : see class Highscore
 *  @property turnCount: counts the amount of turns of all players
 * */
data class Splendor (
    var simulationSpeed : Int,
    var currentGameState : GameState,
    val highscores: MutableList<Highscore> = mutableListOf(),
    var validGame : Boolean = true,
    var turnCount: Int = 0
)