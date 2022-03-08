package service

import entity.DevCard
import entity.GemType
import entity.NobleTile
import entity.PlayerType

class GameService(private val rootService: RootService): AbstractRefreshingService() {
    fun endGame(){}

    fun startNewGame(players: List<Pair<String,PlayerType>>, randomizedTurns: Boolean, simulationSpeed: Int){
        // Sicherheitsabfragen
//        require( players.size < 2 || players.size > 4 )
//        {
//            "invalid players' number"
//        }
//        if ein Spielername doppelt oder leer
//        return // s.o.
//
//        // Spieler erstellen
//        playerList = Liste mit aus players generierten Player-Entitäten // score, bonus entsprechend initialisieren
//        // Prüfe, ob die Reihenfolge der Spieler zufällig sein soll
//        if randomizedTurns == true
//        playerList.shuffle()
//
//        gameState = neuer GameState inkl. playerList
//        gameState.currentPlayer = playerList[0]
//
//        // Karten und Chips erstellen
//        devCardsLevelOne = this(GameService).createCardStack(1)
//        devCardsLevelTwo = this.createCardStack(2)
//        devCardsLevelThree = this.createCardStack(3)
//        nobleTilesOpen = this.createNobleTiles(players.length + 1)
//        gems =  this.createGems(playerList.length)
//
//        // Board erstellen
//        board = Board erstellen mit
//        1) Karten devCardsLevelOne, devCardsLevelTwo, devCardsLevelThree
//        2) NobleTiles aus nobleTilesOpen
//        3) Gems aus gems
//
//        gameState.board = board
//
//        splendor = neue Splendor-Entität mit leerer Highscore-Entität
//        splendor.currentGameState = gameState
//        splendor.simulationSpeed = simulationSpeed
//
//        rootService.currentGame = splendor

//        onAllRefreshable { refreshAfterStartGame() }
    }

//    fun checkNobleTiles(): List<NobleTile>{}

    fun endTurn(){}

    fun refill(level: Int){}

    fun dealCards(){}

//    fun createCardStack(val level: Int): List<DevCard>{}

    fun nextPlayer(){}

//    fun isCardAcquirable(card: DevCard, payment: Map<GemType,Int>): Boolean{}

    fun createGems(PlayerCount: Int){}

//    fun createNobleTiles(PlayerCount: Int): List<NobleTile>{}

}
