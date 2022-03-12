package service

import entity.*
import java.io.File

/**
 *  class for basic game functionalities
 * */
class GameService(private val rootService: RootService): AbstractRefreshingService() {

    var consecutiveNoAction = 0
    var currentPlayerIndex = 0

    /** initializes a new game and connects it to the rootService */
    fun startNewGame(
        players: List<Pair<String,PlayerType>>,
        randomizedTurns: Boolean,
        simulationSpeed: Int) {
        require(players.size in 2..4) { "invalid players' number" }

        // create players
        val playerList = mutableListOf<Player>()
        for(player in players) {
            playerList.add(Player(player.first,player.second)) }
        // check if order should be randomized
        if (randomizedTurns) {
            playerList.shuffle() }

        val levelOneStack = createCardStack(1)
        levelOneStack.shuffle()
        val levelOneOpen = levelOneStack.slice(0..3).toMutableList()

        val levelTwoStack = createCardStack(2)
        levelTwoStack.shuffle()
        val levelTwoOpen = levelTwoStack.slice(0..3).toMutableList()

        val levelThreeStack = createCardStack(3)
        levelThreeStack.shuffle()
        val levelThreeOpen = levelThreeStack.slice(0..3).toMutableList()

        for(i in 0..3) {
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
            levelThreeOpen)

        //create GameState
        val gameState = GameState(
            playerList[0],
            playerList,
            board)

        //create Splendor(current game)
        val splendor = Splendor(
            simulationSpeed,
            gameState,
            mutableListOf())

        consecutiveNoAction = 0
        currentPlayerIndex = 0

        rootService.currentGame = splendor
        onAllRefreshables { refreshAfterStartNewGame() }
        onAllRefreshables { refreshAfterEndTurn() }
    }

    /**
     * create a new game state, link it to the chain and set the pointer to this game state
     * if nobody can make a move or the current Player hat 15 point -> endGame()
     */
    fun nextPlayer(){
        val game = rootService.currentGame
        checkNotNull(game)
        val currentGameState = game.currentGameState
        val board = game.currentGameState.board

        currentPlayerIndex = (currentPlayerIndex + 1) % currentGameState.playerList.size

        // if current player reach 15 or above -> end game
        if(currentGameState.currentPlayer.score >= 15){
            currentGameState.playerList = currentGameState.playerList.sortedByDescending { player -> player.score }
            println(currentGameState.playerList.toString())
           onAllRefreshables { refreshAfterEndGame() }
            return
        }

        //create new state
        val newPlayerList = mutableListOf<Player>()
        println(board.gems.toString())
        val tempBoard = Board(
            board.nobleTiles.toMutableList(),
            board.levelOneCards.toMutableList(),
            board.levelOneOpen.toMutableList(),
            board.levelTwoCards.toMutableList(),
            board.levelTwoCards.toMutableList(),
            board.levelThreeCards.toMutableList(),
            board.levelThreeOpen.toMutableList(),
            board.gems.toMutableMap()
        )

        // create a new players list with the properties of the current list
        currentGameState.playerList.forEach { player ->
            newPlayerList.add(
                Player(
                    player.name,
                    player.playerType,
                    player.gems,
                    player.bonus,
                    player.reservedCards,
                    player.nobleTiles,
                    player.score,
                    player.devCards
                )
            )
        }
        val newGameState = GameState(
            newPlayerList[currentPlayerIndex],
            newPlayerList,
            tempBoard)

        //bind new gameState to chain and set pointer to the newGameState
        newGameState.previous = currentGameState
        game.currentGameState = newGameState
        val newCurrentGameState = game.currentGameState
        val newBoard = game.currentGameState.board

        //update currentPlayerIndex and check if the next player can make a valid move
//        currentPlayerIndex = (currentPlayerIndex + 1) % newCurrentGameState.playerList.size
        val totalGemsOnBoard = newBoard.gems.values.sum() - newBoard.gems.getValue(GemType.YELLOW)
        val affordableCards = acquirableCards().size
        val reservedCards = newCurrentGameState.currentPlayer.reservedCards.size
        println(totalGemsOnBoard)
        println(affordableCards)
        println(reservedCards)
        if((totalGemsOnBoard == 0) && (affordableCards == 0) && (reservedCards == 3)){
//            onAllRefreshables { refreshIfNoValidAction() }
            consecutiveNoAction++
            println("plus one")
        }

        // if no player can make any move, end game with tie result
        if(consecutiveNoAction == newCurrentGameState.playerList.size){
            newCurrentGameState.playerList = newCurrentGameState.playerList.sortedByDescending { player -> player.score }
//            onAllRefreshables { refreshAfterEndGame(true) }
            return
        }
//        onAllRefreshables { refreshAfterNextPlayer }
    }

//    /**
//     * End game
//     *
//     * @return a list sort by players' score
//     */
//    fun endGame(){
//        val game = rootService.currentGame
//        checkNotNull(game)
//        val currentGameState = game.currentGameState
//
//        currentGameState.playerList = currentGameState.playerList.sortedByDescending { player -> player.score }
//    }

    /**
     * deal a new card to the same place, where a card was removed
     *
     * @param level level of the card
     * @param index  index of the removed card
     */
    fun refill(level: Int,index: Int){
        val game = rootService.currentGame!!
        val board = game.currentGameState.board

        when(level){
            1 -> {
                board.levelOneOpen.add(index,board.levelOneCards[0])
                board.levelOneCards.removeAt(0)
            }
            2 -> {
                board.levelTwoOpen.add(index,board.levelTwoCards[0])
                board.levelTwoCards.removeAt(0)
            }
            3 -> {
                board.levelThreeOpen.add(index,board.levelThreeCards[0])
                board.levelThreeCards.removeAt(0)
            }
        }
    }


    /**
     * Check which noble tiles can visit the current player
     * automatic add prestige point to player score if there is only on affordable card
     * @return a mutable list of indexes of the affordable noble tiles on board
     */
    fun checkNobleTiles(): MutableList<Int>{

        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val player = game.currentGameState.currentPlayer
        val affordableNobleTile = mutableListOf<Int>()

        board.nobleTiles.forEach { nobleTile ->
            // extract each card out of noble tiles
            val tempGemMap = nobleTile.condition.toMutableMap()
            tempGemMap.forEach { (gemType, _) ->
                // check if the noble can visit the player
                tempGemMap[gemType] = tempGemMap.getValue(gemType) - player.bonus.getValue(gemType)
                if (tempGemMap.filterValues { it >= 0 }.values.sum() == 0) {
                    affordableNobleTile.add(board.nobleTiles.indexOf(nobleTile))
                }
            }
        }

        if(affordableNobleTile.size == 1){
            player.score += board.nobleTiles[affordableNobleTile[0]].prestigePoints
            board.nobleTiles.removeAt(affordableNobleTile[0])
        }

        return affordableNobleTile
    }


    /** checks if the current player has more than ten gems */
    fun checkGems(): Boolean {
        return rootService.currentGame!!.currentGameState.currentPlayer.gems.values.sum() > 10
    }


    /*-----------------------------HELP FUNCTION-----------------------------*/

    /** creates nobleTiles */
    private fun createNobleTiles(playerCount: Int): MutableList<NobleTile> {

        val nobleTileFile = "src/main/resources/splendor-adligenkarten.csv"
        val cardProps: MutableList<String> = File(nobleTileFile).readLines().toMutableList()
        cardProps.removeAt(0)
        val nobleCards = mutableListOf<NobleTile>()

        cardProps.forEach { cardProp ->
            val cardConfig = cardProp.split(", ")
            val tempMap = mapOf(
                GemType.WHITE to cardConfig[1].trim().toInt(),
                GemType.BLUE to cardConfig[2].trim().toInt(),
                GemType.GREEN to cardConfig[3].trim().toInt(),
                GemType.RED to cardConfig[4].trim().toInt(),
                GemType.BLACK to cardConfig[5].trim().toInt()
            )
            nobleCards.add(
                NobleTile(
                    cardConfig[0].trim().toInt(),
                    tempMap,
                    cardConfig[6].trim().toInt()
                )
            )
        }
        nobleCards.shuffle()
        return  nobleCards.slice(0..playerCount).toMutableList()
    }


    /** creates cardStacks for all devCard levels */
    private fun createCardStack(level: Int): MutableList<DevCard>{

        val cardConfigFile = "src/main/resources/splendor-entwicklungskarten.csv"
        val cardConfigs: MutableList<String> = File(cardConfigFile.trim()).readLines().toMutableList()
        cardConfigs.removeAt(0)
        val cardList = mutableListOf<DevCard>()

        when (level) {
            1 -> {
                for(i in 0..39){
                    val cardConfig = cardConfigs[i].split(", ")
                    cardList.add(createCard(cardConfig))
                }
            }
            2 -> {
                for(i in 40..69){
                    val cardConfig = cardConfigs[i].split(", ")
                    cardList.add(createCard(cardConfig))
                }
            }
            else -> {
                for(i in 70..89){
                    val cardConfig = cardConfigs[i].split(", ")
                    cardList.add(createCard(cardConfig))
                }
            }
        }
        return cardList
    }


    /**
     * Create a development card
     *
     * @param cardProp card properties as String
     * @return a development card object
     */
    fun createCard(cardProp: List<String>): DevCard {

        val tempMap = mapOf(
            GemType.WHITE to cardProp[1].trim().toInt(),
            GemType.BLUE to cardProp[2].trim().toInt(),
            GemType.GREEN to cardProp[3].trim().toInt(),
            GemType.RED to cardProp[4].trim().toInt(),
            GemType.BLACK to cardProp[5].trim().toInt()
        )
        val color = when (cardProp[8].trim()) {
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
            cardProp[0].trim().toInt(),
            tempMap,
            cardProp[6].trim().toInt(),
            cardProp[7].trim().toInt(),
            color)
    }

    /**
     * check if the card is for the current player affordable
     *
     * @param card the card which the player chose
     * @param payment map of gems from player
     * @return true if player can this card afford, else return false
     * */
    fun isCardAcquirable(card: DevCard, payment: Map<GemType,Int>): Boolean {

        val tempGemMap = card.price.toMutableMap()

        card.price.forEach { (gemType) ->
            tempGemMap[gemType] = tempGemMap.getValue(gemType) - payment.getValue(gemType)
        }

        val gemsNeeded = tempGemMap.filterValues { it >= 0 }.values.sum()
        return (gemsNeeded == 0) || (gemsNeeded <= payment.getValue(GemType.YELLOW))
    }

    /**
     * check if there are any open card, which player can afford
     *
     * @return a list of Pair( level, cardIndex ) of acquirable card
     */
     fun acquirableCards(): MutableList<Pair<Int,Int>>{

        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val playerBonus = game.currentGameState.currentPlayer.bonus
        val playerGems = game.currentGameState.currentPlayer.gems
        val listOfAcquirableCards = mutableListOf<Pair<Int,Int>>()
        val totalOwn : Map<GemType,Int> = mapOf(
            GemType.RED    to (playerBonus.getValue(GemType.RED)   + playerGems.getValue(GemType.RED)),
            GemType.GREEN  to (playerBonus.getValue(GemType.GREEN) + playerGems.getValue(GemType.GREEN)),
            GemType.WHITE  to (playerBonus.getValue(GemType.WHITE) + playerGems.getValue(GemType.WHITE)),
            GemType.BLACK  to (playerBonus.getValue(GemType.BLACK) + playerGems.getValue(GemType.BLACK)),
            GemType.BLUE   to (playerBonus.getValue(GemType.BLUE)  + playerGems.getValue(GemType.BLUE)),
            GemType.YELLOW to   playerGems.getValue(GemType.YELLOW))

        for(i in 0..3) {
            if(isCardAcquirable(board.levelOneOpen[i], totalOwn)){
                listOfAcquirableCards.add(Pair(1,i))
            }
            if(isCardAcquirable(board.levelTwoOpen[i], totalOwn)){
                listOfAcquirableCards.add(Pair(2,i))
            }
            if(isCardAcquirable(board.levelThreeOpen[i], totalOwn)){
                listOfAcquirableCards.add(Pair(3,i))
            }
        }
        return listOfAcquirableCards
    }

//    /**
//     * Check if the current player can perform any valid move in this turn
//     *
//     * @return true if there are at least one valid action. False if there is no valid action.
//     *
//     */
//    fun checkValidAction(): Boolean{
//
//        val game = rootService.currentGame
//        checkNotNull(game)
//        val board = game.currentGameState.board
//        val currentPlayer = game.currentGameState.currentPlayer
//        var totalGemsOnBoard = board.gems.values.sum() - board.gems.getValue(GemType.YELLOW)
//        val affordableCards = acquirableCards().size
//        val reservedCards = currentPlayer.gems.size
//
//        return (totalGemsOnBoard != 0) || (affordableCards != 0) || (reservedCards < 3)
//    }
}
