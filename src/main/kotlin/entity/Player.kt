package entity

/**
 *  Klasse für ein Player-Objekt
 *  @param name : Name des Spielers
 *  @param score : aktueller Score (PrestigePunkte) des Spielers
 *  @param bonus : zur Verfügung stehende Boni des Spielers
 *  @param playerType : siehe Enum PlayerType
 *  @param gems : gems, die der Spieler aktuell auf der Hand hat.
 *  @param nobleTiles : Adlige, die den Spieler besucht haben
 *  @param reservedCards : aktuell reservierte Entwicklungskarten
 *  @param devCards : gekaufte Entwicklungskarten
 * */
class Player (
    val name : String,
    val playerType: PlayerType
){
    val reservedCards : MutableList<DevCard> = mutableListOf()
    val nobleTiles : MutableList<NobleTile> = mutableListOf()
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