package entity

/**
 *  class for the game Splendor
 *  @param validGame : indication whether game is eligible for saving a highscore
 *  @param simulationSpeed : specification of the simulation speed
 *  @param currentGameState : see class GameState
 *  @param highscores : see class Highscore
 * */
data class Splendor (
    var simulationSpeed : Int,
    val currentGameState : GameState,
    val highscores: List<Highscore>,
    var validGame : Boolean = true
)