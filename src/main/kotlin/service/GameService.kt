package service

import entity.*
import java.io.File
import javax.swing.RowFilter.Entry

class GameService(private val rootService: RootService): AbstractRefreshingService() {
    fun endGame(){}

    fun startNewGame(
        players: List<Pair<String,PlayerType>>,
        randomizedTurns: Boolean,
        simulationSpeed: Int) {
        // Sicherheitsabfragen
        require( players.size < 2 || players.size > 4 ) { "invalid players' number" }

        // Spieler erstellen
        val playerList = mutableListOf<Player>()
        for(player in players){
            playerList.add(Player(player.first,player.second))
        }
        // Prüfe, ob die Reihenfolge der Spieler zufällig sein soll
        if (randomizedTurns) {
            playerList.shuffle()
        }

        val levelOneStack = createCardStack(1)
        val levelOneOpen = levelOneStack.slice(0..2).toMutableList()

        val levelTwoStack = createCardStack(2)
        val levelTwoOpen = levelTwoStack.slice(0..2).toMutableList()

        val levelThreeStack = createCardStack(3)
        val levelThreeOpen = levelThreeStack.slice(0..2).toMutableList()

        for(i in 0..2){
            levelOneStack.removeAt(i)
            levelTwoStack.removeAt(i)
            levelThreeStack.removeAt(i)
        }

        // create Board
        val board = Board(
            createNobleTiles(players.size),
            levelOneStack,
            levelOneOpen,
            levelTwoStack,
            levelTwoOpen,
            levelThreeStack,
            levelThreeOpen
        )
        //create GameState
        val gameState = GameState(
            playerList[0],
            playerList,
            board
        )
        //create Splendor(current game)
        val splendor = Splendor(
            simulationSpeed,
            gameState,
            listOf<Highscore>()
        )

        rootService.currentGame = splendor

//        onAllRefreshable { refreshAfterStartGame() }
    }

//    fun checkNobleTiles(): List<NobleTile>{}

    fun endTurn(){}

    fun refill(level: Int){}

//        fun nextPlayer(){}

    fun isCardAcquirable(card: DevCard, payment: Map<GemType,Int>): Boolean{return true}

//    fun createGems(PlayerCount: Int){
//
//        for()
//    }


    /*-----------------------------HELP FUNCTION-----------------------------*/

    private fun createNobleTiles(playerCount: Int): MutableList<NobleTile> {

        val game = rootService.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState
        val board = game.currentGameState.board

        val nobleTileFile = "/splendor-adligenkarten.csv"
        val cardProp: MutableList<String> = File(nobleTileFile).readLines().toMutableList()
        cardProp.removeAt(0)
        val nobleCards = mutableListOf<NobleTile>()

        for (nobleTileConfig in cardProp) {
            val tempMap = mapOf(
                GemType.WHITE to cardProp[1].toInt(),
                GemType.BLUE to cardProp[2].toInt(),
                GemType.GREEN to cardProp[3].toInt(),
                GemType.RED to cardProp[4].toInt(),
                GemType.BLACK to cardProp[5].toInt()
            )
            nobleCards.add(
                NobleTile(
                    cardProp[0].toInt(),
                    tempMap,
                    cardProp[6].toInt()
                )
            )
        }
        nobleCards.shuffle()
        return  nobleCards.slice(0..playerCount).toMutableList()
    }


    private fun createCardStack(level: Int): MutableList<DevCard>{

        val cardConfigFile = "/splendor-entwicklungskarten.csv"
        val cardConfigs: MutableList<String> = File(cardConfigFile).readLines().toMutableList()
        cardConfigs.removeAt(0)
        val cardList = mutableListOf<DevCard>()

        if(level == 1){
            for(i in 1..40){
                val cardConfig = cardConfigs[i].split(", ")
                cardList.add(createCard(cardConfigs))
            }
        }else if(level == 2){
            for(i in 41..69){
                val cardConfig = cardConfigs[i].split(", ")
                cardList.add(createCard(cardConfigs))
            }
        }else{
            for(i in 70..90){
                val cardConfig = cardConfigs[i].split(", ")
                cardList.add(createCard(cardConfigs))
            }
        }
        return cardList

    }


    // create and ass a development card to board with input of list of card's properties
    private fun createCard(cardProp: List<String>): DevCard {

        val game = rootService.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState
        val board = game.currentGameState.board

        val tempMap = mapOf(
            GemType.WHITE to cardProp[1].toInt(),
            GemType.BLUE to cardProp[2].toInt(),
            GemType.GREEN to cardProp[3].toInt(),
            GemType.RED to cardProp[4].toInt(),
            GemType.BLACK to cardProp[5].toInt()
        )
        val color = when (cardProp[8]) {
            "diamant" -> GemType.WHITE
            "saphir" -> GemType.BLUE
            "smaragd" -> GemType.GREEN
            "rubin" -> GemType.RED
            "onyx" -> GemType.BLACK
            else -> {
                throw java.lang.IllegalArgumentException(
                    "invalid gem type"
                )
            }
        }
        //create card
        return DevCard(
            cardProp[0].toInt(),
            tempMap,
            cardProp[6].toInt(),
            cardProp[7].toInt(),
            color
        )
//        // add card to level one stack
//        if(cardProp[7].toInt() == 1) { board.levelOneCards.add(devCard) }
//        //add card to level two stack
//        if(cardProp[7].toInt() == 2) { board.levelTwoCards.add(devCard) }
//        //add card to level three stack
//        if(cardProp[7].toInt() == 3) { board.levelThreeCards.add(devCard) }
    }

    fun checkIfAvailable(neededGem: Map<GemType,Int>, availableGem: Map<GemType,Int>): Boolean{

        var boolean = true
        neededGem.forEach { (gemType, gemNum) ->
            boolean = boolean && (availableGem[gemType]!! >= gemNum)
        }
        return boolean
    }
}
