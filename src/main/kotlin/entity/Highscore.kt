package entity

/**
 *  Klasse für ein Highscore-Objekt
 *  @param score : Endgültiger Score des Gewinners des aktuellen Spiels
 *  @param playerName : Name des Gewinners
 */
class Highscore (
    val playerName : String,
    val score : Int
)