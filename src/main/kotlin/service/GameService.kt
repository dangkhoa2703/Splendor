package service

import entity.*
import java.io.File

class GameService(private val rootService: RootService): AbstractRefreshingService() {

    private var consecutiveNoAction = 0
    private var currentPlayerIndex = 0

    fun startNewGame(
        players: List<Pair<String,PlayerType>>,
        randomizedTurns: Boolean,
        simulationSpeed: Int)
    { require(players.size in 2..4) { "invalid players' number" }

        // create players
        val playerList = mutableListOf<Player>()
        for(player in players){
            playerList.add(Player(player.first,player.second))
        }
        // check if order should be randomized
        if (randomizedTurns) {
            playerList.shuffle()
        }

        val levelOneStack = createCardStack(1)
        val levelOneOpen = levelOneStack.slice(0..3).toMutableList()

        val levelTwoStack = createCardStack(2)
        val levelTwoOpen = levelTwoStack.slice(0..3).toMutableList()

        val levelThreeStack = createCardStack(3)
        val levelThreeOpen = levelThreeStack.slice(0..3).toMutableList()

        for(i in 0..3){
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
            mutableListOf()
        )

        rootService.currentGame = splendor

//        onAllRefreshable { refreshAfterStartGame() }
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

        //check if there are any valid move, if nobody can make a move -> endGame()
        if(!checkValidAction()){
//            onAllRefreshables { refreshIfNoValidAction() }
            consecutiveNoAction++
        }
        if(currentGameState.currentPlayer.score >= 15
            || consecutiveNoAction == currentGameState.playerList.size){
            endGame()
            return
        }

        val newPlayerList = mutableListOf<Player>()
        val newBoard = Board(
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
                    player.bonus
                )
            )
        }

        //create new GameState
        val newGameState = GameState(
            newPlayerList[currentPlayerIndex],
            newPlayerList,
            newBoard
        )

        //bind new gameState to chain and set pointer to the newGameState
        newGameState.previous = currentGameState
        game.currentGameState = newGameState

//        onAllRefreshables { refreshAfterNextPlayer }
    }

    /**
     * End game
     *
     * @return a list sort by players' score
     */
    fun endGame(): List<Player>{
        val game = rootService.currentGame
        checkNotNull(game)
        val currentGameState = game.currentGameState
        val board = game.currentGameState.board

        return currentGameState.playerList.sortedByDescending { player -> player.score }
    }

    fun endTurn(){
//
//        val game = rootService.currentGame
//        checkNotNull(game)
//        val board = game.currentGameState.board
//
//        checkNobleTiles()
////        checkGems()
    }

    /**
     * deal a new card to the same place, where a card was removed
     *
     * @param level level of the card
     * @param index  index of the removed card
     */
    fun refill(level: Int,index: Int){
        val game = rootService.currentGame
        checkNotNull(game)
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
            var affordable = true
            nobleTile.condition.forEach { (type, num) ->
                affordable = affordable && (player.bonus.getValue(type) >= num )
            }
            if(affordable){ affordableNobleTile.add(board.nobleTiles.indexOf(nobleTile)) }
        }

        if(affordableNobleTile.size == 1){
            player.score += board.nobleTiles[affordableNobleTile[0]].prestigePoints
        }

        return affordableNobleTile
    }

    fun checkGems(): Boolean{

        val game = rootService.currentGame
        checkNotNull(game)
        val player = game.currentGameState.currentPlayer
        var totalGems = 0

        player.gems.forEach{ (_,v) ->
            totalGems += v
        }

        return totalGems >= 10
    }


    /*-----------------------------HELP FUNCTION-----------------------------*/

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


    // create and ass a development card to board with input of list of card's properties
    private fun createCard(cardProp: List<String>): DevCard {

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
            color
        )
//        // add card to level one stack
//        if(cardProp[7].toInt() == 1) { board.levelOneCards.add(devCard) }
//        //add card to level two stack
//        if(cardProp[7].toInt() == 2) { board.levelTwoCards.add(devCard) }
//        //add card to level three stack
//        if(cardProp[7].toInt() == 3) { board.levelThreeCards.add(devCard) }
    }

//    fun checkIfAvailable(neededGem: Map<GemType,Int>, availableGem: Map<GemType,Int>): Boolean{
//
//        var boolean = true
//        neededGem.forEach { (gemType, gemNum) ->
//            boolean = boolean && (availableGem[gemType]!! >= gemNum)
//        }
//        return boolean
//    }

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
    private fun acquirableCards(): MutableList<Pair<Int,Int>>{

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
            GemType.YELLOW to   playerGems.getValue(GemType.YELLOW)
        )

        for(i in 0..3){
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

    /**
     * Check if the current player can perform any valid move in this turn
     *
     * @return true if there are at least one valid action
     * @return false if there is no valid action
     */
    fun checkValidAction(): Boolean{

        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val currentPlayer = game.currentGameState.currentPlayer
        var totalGemsOnBoard = 0
        val affordableCard = acquirableCards().size
        val reservedCards = currentPlayer.gems.size

        //calculate total gems on board
        board.gems.forEach{ (_,v) ->
            totalGemsOnBoard += v
        }

        return (totalGemsOnBoard != 0) || (affordableCard != 0) || (reservedCards < 3)
    }
}
