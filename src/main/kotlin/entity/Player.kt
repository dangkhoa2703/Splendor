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
    var score : Int,
    var bonus : Map<GemType, Int>,
    var playerType: PlayerType,
    var gems : List<Gem>,
    var nobleTiles : List<NobleTile>,
    var reservedCards : List<DevCard>,
    var devCards : List<DevCard>
)