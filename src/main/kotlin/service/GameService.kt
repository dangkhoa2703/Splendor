package service

import entity.*
import java.io.File
import java.math.RoundingMode

/**
 *  class for basic game functionalities
 * */
class GameService(private val rootService: RootService): AbstractRefreshingService() {


    /** initializes a new game and connects it to the rootService */
    fun startNewGame(
        players: List<Pair<String,PlayerType>>,
        randomizedTurns: Boolean,
        simulationSpeed: Int){
        require(players.size in 2..4) { "invalid players' number" }

        // create players
        val playerList = mutableListOf<Player>()
        for(player in players) { playerList.add(Player(player.first,player.second)) }

        // check if order should be randomized
        if (randomizedTurns) { playerList.shuffle() }

        for((index, player) in playerList.withIndex()) { player.id = index }

        val levelOneStack = createCardStack(1)
        levelOneStack.shuffle()
        val levelTwoStack = createCardStack(2)
        levelTwoStack.shuffle()
        val levelThreeStack = createCardStack(3)
        levelThreeStack.shuffle()

        var tempList: MutableList<DevCard> = levelOneStack.slice(0..3).toMutableList()
	    val levelOneOpen = tempList
	    for(card in tempList) levelOneStack.remove(card)

	    tempList = levelTwoStack.slice(0..3).toMutableList()
	    val levelTwoOpen = tempList
	    for(card in tempList) levelTwoStack.remove(card)

	    tempList = levelThreeStack.slice(0..3).toMutableList()
	    val levelThreeOpen = tempList
	    for(card in tempList) levelThreeStack.remove(card)

        // create Board
        val board = Board(createNobleTiles(players.size), levelOneStack, levelOneOpen, levelTwoStack, levelTwoOpen,
            levelThreeStack, levelThreeOpen)
        if (playerList.size < 4) {
            if (playerList.size == 2) {
                board.gems = mutableMapOf(GemType.RED to 4, GemType.GREEN to 4, GemType.WHITE to 4, GemType.BLACK to 4,
                    GemType.BLUE to 4, GemType.YELLOW to 5) }
            else { board.gems = mutableMapOf(GemType.RED to 5, GemType.GREEN to 5, GemType.WHITE to 5,
                GemType.BLACK to 5, GemType.BLUE to 5, GemType.YELLOW to 5) }
        }
        //create GameState
        val gameState = GameState(playerList[0], playerList, board)
        gameState.isInitialState = true
        //create Splendor(current game)
        val splendor = Splendor(simulationSpeed, gameState, mutableListOf())

        rootService.currentGame = splendor
        //createNewGameState(false)
        rootService.currentGame!!.currentGameState.consecutiveNoAction = 0
        rootService.currentGame!!.currentGameState.currentPlayerIndex = 0

        onAllRefreshables { refreshAfterStartNewGame() }
        onAllRefreshables { refreshAfterEndTurn() }
    }

    /**
     * create a new game state, link it to the chain and set the pointer to this game state
     */
    fun createNewGameState(notFirstGameState:Boolean):GameState{
        val game = rootService.currentGame
        checkNotNull(game)
        val currentGameState = game.currentGameState
        val board = game.currentGameState.board

        //create new state
        val newPlayerList = mutableListOf<Player>()
        val tempBoard = Board(
            board.nobleTiles.toMutableList(),
            board.levelOneCards.toMutableList(),
            board.levelOneOpen.toMutableList(),
            board.levelTwoCards.toMutableList(),
            board.levelTwoOpen.toMutableList(),
            board.levelThreeCards.toMutableList(),
            board.levelThreeOpen.toMutableList(),
            board.gems.toMutableMap()
        )

        // create a new players list with the properties of the current list
        currentGameState.playerList.forEach { player ->
            newPlayerList.add(
                Player(player.name, player.playerType, player.gems.toMutableMap(), player.bonus.toMutableMap()
                    , player.reservedCards.toMutableList(), player.nobleTiles.toMutableList(), player.score
                    , player.devCards.toMutableList(), player.id)
            )
        }

        var nextPlayerIndex = 0
        if(notFirstGameState){
            nextPlayerIndex =
                (rootService.currentGame!!.currentGameState.currentPlayerIndex + 1) % newPlayerList.size
        }
        val newGameState = GameState(
            newPlayerList[nextPlayerIndex],
            newPlayerList,
            tempBoard)
        if(notFirstGameState) {
            newGameState.currentPlayerIndex = nextPlayerIndex
            //bind new gameState to chain and set pointer to the newGameState
            newGameState.previous = currentGameState
            currentGameState.next = newGameState
            game.currentGameState = newGameState
        }
        else{
            //for undo, put new gamestate at beginning, is used to create first game state
            currentGameState.previous = newGameState
            newGameState.next = currentGameState
            currentGameState.isInitialState = false
            newGameState.isInitialState = true
        }
        return newGameState
    }

    /**
     * if nobody can make a move or the current Player hat 15 point -> endGame()
     */
    fun nextPlayer(){
        var newGameState = rootService.currentGame!!.currentGameState
        val newBoard = newGameState.board
        val newPlayer = newGameState.currentPlayer
        if (checkGems()){
            throw IllegalArgumentException("DISCARD GEMS")
        }
        if(!newPlayer.choseNobleTile && checkNobleTiles().size>0){
            throw IllegalStateException("Chose a noble tile")
        }

        // if current player reach 15 or above -> end game
        if(newGameState.currentPlayer.score >= 15){
            newGameState.playerList = newGameState.playerList.sortedByDescending { player -> player.score }
            saveHighscoresAfterEndGame()
            onAllRefreshables { refreshAfterEndGame() }
            return
        }

        rootService.currentGame!!.turnCount++
        newGameState = createNewGameState(true)
        rootService.currentGame!!.currentGameState.currentPlayer.choseNobleTile = false

        //check if the next player can make a valid move
        val totalGemsOnBoard = newBoard.gems.values.sum() - newBoard.gems.getValue(GemType.YELLOW)
        val affordableCards = acquirableCards().size
        val reservedCards = newGameState.currentPlayer.reservedCards.size
        if((totalGemsOnBoard == 0) && (affordableCards == 0) && (reservedCards == 3)){
            newGameState.consecutiveNoAction++
        }

        // if no player can make any move, end game with tie result
        if(rootService.currentGame!!.currentGameState.consecutiveNoAction == newGameState.playerList.size){
            newGameState.playerList = newGameState.playerList.sortedByDescending { player -> player.score }
            saveHighscoresAfterEndGame()
            onAllRefreshables { refreshAfterEndGame() }
            return
        }
    }

    /**
     * method to save highscores after end game
     * don't use in combination with save game or quit game
     */
    private fun saveHighscoresAfterEndGame(){
        val game = rootService.currentGame
        checkNotNull(game)
        if(game.validGame){
            for (player in game.currentGameState.playerList){
                var score: Double =
                    ((player.score).toDouble() + (1 / rootService.currentGame!!.turnCount)).
                    toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
                score *= 100
                rootService.ioService.saveHighscore(Highscore(player.name, score))
            }
        }
    }

    /**
     * deal a new card to the same place, where a card was removed
     *
     * @param level level of the card
     */
    fun refill(level: Int){
        val game = rootService.currentGame!!
        val board = game.currentGameState.board

        when(level){
            1 -> {
                board.levelOneOpen.add(board.levelOneCards[0])
                board.levelOneCards.removeAt(0)
            }
            2 -> {
                board.levelTwoOpen.add(board.levelTwoCards[0])
                board.levelTwoCards.removeAt(0)
            }
            3 -> {
                board.levelThreeOpen.add(board.levelThreeCards[0])
                board.levelThreeCards.removeAt(0)
            }
        }
    }


    /**
     * Check which noble tiles can visit the current player
     * automatic add prestige point to player score if there is only on affordable card
     * @return a mutable list of affordable noble tiles on board
     */
    fun checkNobleTiles(): MutableList<NobleTile>{

        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val player = game.currentGameState.currentPlayer
        val affordableNobleTile = mutableListOf<NobleTile>()

        board.nobleTiles.forEach { nobleTile ->
            // extract each card out of noble tiles
            val tempGemMap = nobleTile.condition.toMutableMap()
            tempGemMap.forEach { (gemType, _) ->
                // check if the noble can visit the player
                tempGemMap[gemType] = tempGemMap.getValue(gemType) - player.bonus.getValue(gemType)
                if (tempGemMap.filterValues { it >= 0 }.values.sum() == 0) {
                    affordableNobleTile.add(nobleTile)
                }
            }
        }

//        if(affordableNobleTile.size == 1){
//            player.score += board.nobleTiles[affordableNobleTile[0]].prestigePoints
//            board.nobleTiles.removeAt(affordableNobleTile[0])
//        }

        return affordableNobleTile
    }


    /** checks if the current player has more than ten gems */
    fun checkGems(): Boolean {
        return rootService.currentGame!!.currentGameState.currentPlayer.gems.values.sum() > 10
    }


    /*-----------------------------HELP FUNCTION-----------------------------*/

    /** creates nobleTiles */
    private fun createNobleTiles(playerCount: Int): MutableList<NobleTile> {

        val nobleTileFile = GameService::class.java.getResource("/splendor-adligenkarten.csv").getPath()
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

	val cardConfigFile = GameService::class.java.getResource("/splendor-entwicklungskarten.csv").getPath()
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
            cardProp[7].trim().toInt(),
            cardProp[6].trim().toInt(),
            color)
    }

    /**
     * check if the card is for a player affordable
     *
     * @param card the card which the player chose
     * @param payment map of gems from player
     * @return true if player can this card afford, else return false
     * */
    fun isCardAcquirable(card: DevCard, payment: Map<GemType,Int>): Boolean {

        val tempGemMap = card.price.toMutableMap()

        card.price.forEach { (gemType) ->
            tempGemMap[gemType] =(tempGemMap.getValue(gemType) - payment.getValue(gemType)) -
                    rootService.currentGame!!.currentGameState.currentPlayer.bonus.getValue(gemType)
        }

        val gemsNeeded = tempGemMap.filterValues { it >= 0 }.values.sum()
        return (gemsNeeded == 0) || (gemsNeeded <= payment.getValue(GemType.YELLOW))
    }

    /**
     * check if there are any open card, which player can afford
     *
     * @return a list of Pair( level, cardIndex ) of acquirable card
     */
    fun acquirableCards(): MutableList<DevCard>{

        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val playerBonus = game.currentGameState.currentPlayer.bonus
        val playerGems = game.currentGameState.currentPlayer.gems
        val listOfAcquirableCards = mutableListOf<DevCard>()
        val totalOwn : Map<GemType,Int> = mapOf(
            GemType.RED    to (playerBonus.getValue(GemType.RED)   + playerGems.getValue(GemType.RED)),
            GemType.GREEN  to (playerBonus.getValue(GemType.GREEN) + playerGems.getValue(GemType.GREEN)),
            GemType.WHITE  to (playerBonus.getValue(GemType.WHITE) + playerGems.getValue(GemType.WHITE)),
            GemType.BLACK  to (playerBonus.getValue(GemType.BLACK) + playerGems.getValue(GemType.BLACK)),
            GemType.BLUE   to (playerBonus.getValue(GemType.BLUE)  + playerGems.getValue(GemType.BLUE)),
            GemType.YELLOW to   playerGems.getValue(GemType.YELLOW))

        for(i in 0..3) {
            if(isCardAcquirable(board.levelOneOpen[i], totalOwn)){
                listOfAcquirableCards.add(board.levelOneOpen[i])
            }
            if(isCardAcquirable(board.levelTwoOpen[i], totalOwn)){
                listOfAcquirableCards.add(board.levelTwoOpen[i])
            }
            if(isCardAcquirable(board.levelThreeOpen[i], totalOwn)){
                listOfAcquirableCards.add(board.levelThreeOpen[i])
            }
        }
        return listOfAcquirableCards
    }

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
