package entity

/**
 *  Klasse das Spiel Splendor
 *  @param validGame : Boolean, welcher angibt, ob das Spiel gültig für die Highscore-Liste ist.
 *  @param simulationSpeed : Integer als Angabe für die Simulationsgeschwindigkeit
 *  @param currentGameState : siehe Klasse GameState
 *  @param highscores : siehe Klasse Highscore
 * */
data class Splendor (
    var validGame : Boolean,
    var simulationSpeed : Int,
    val currentGameState : GameState,
    val highscores: List<Highscore>
)