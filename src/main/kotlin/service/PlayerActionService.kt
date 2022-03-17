package service

import entity.*

/**
 * class to provide the logic for possible actions a player can take
 */
class PlayerActionService(private val rootService: RootService): AbstractRefreshingService() {

    /** [showPlayers] :Popup displaying concurring players and items they have in hand.*/
    fun showPlayers(currentPlayer: Player) {
	    onAllRefreshables{ refreshAfterPopup(currentPlayer) }
    }

    /**
     * undoes the last performed player-game-action
     * @throws IllegalStateException if a previous state does not exist
     */
    fun undo(){
        val game = rootService.currentGame!!
        if(game.currentGameState.hasPrevious()) {
            game.currentGameState = game.currentGameState.previous
            game.validGame = false
            game.turnCount--
        }
        else throw IllegalStateException("a previous state does not exist")
	    onAllRefreshables { refreshAfterEndTurn() }
    }

    /**
     * redoes the last undone player-game-action
     * @throws IllegalStateException if a following state does not exist
     */
    fun redo(){
        val game = rootService.currentGame!!
        if(game.currentGameState.hasNext()) {
            game.currentGameState = game.currentGameState.next
            game.validGame = false
            game.turnCount++
        }
        else throw IllegalStateException("a following state does not exist")
	    onAllRefreshables { refreshAfterEndTurn() }
    }

    /**
     * @return a hint for the best next move for the current player and current situation
     */
    fun showHint(turn:Turn): String{
        if(!rootService.currentGame!!.currentGameState.currentPlayer.hasDoneTurn){
        val hint :String
        val board = rootService.currentGame!!.currentGameState.board
        val player = rootService.currentGame!!.currentGameState.currentPlayer
        if(turn.turnType == TurnType.RESERVE_CARD){
            val card = turn.card[0]
            val placement = when(card.level){
                1 -> board.levelOneOpen.indexOf(card)+1
                2 -> board.levelTwoOpen.indexOf(card)+1
                3 -> board.levelThreeOpen.indexOf(card)+1
                else -> {throw IllegalStateException("this should not happen")}
        }
        hint = "You should reserve the level-${card.level}-card at position $placement."
        }
        else if(turn.turnType == TurnType.BUY_CARD){
            val card = turn.card[0]
            val placement :Int
            if(player.reservedCards.contains(card)){
                placement = player.reservedCards.indexOf(card)+1
                hint = "You should buy your reserved card at position $placement."
            }
            else {
                placement = when (card.level) {
                    1 -> board.levelOneOpen.indexOf(card) + 1
                    2 -> board.levelTwoOpen.indexOf(card) + 1
                    3 -> board.levelThreeOpen.indexOf(card) + 1
                    else -> {
                        throw IllegalStateException("this should not happen")
                    }
                }
                hint = "You should buy the level-${card.level}-card at position $placement."
            }
        }
        else if (turn.turnType == TurnType.TAKE_GEMS){
            val gemTypes = turn.gems.filter { it.value > 0 }.keys.toMutableList()
            hint = when (gemTypes.size) {
                1 -> {
                    "You should take two ${gemTypes[0]} gems."
                }
                3 -> {
                    "You should take three gems of the colours ${gemTypes[0]}, ${gemTypes[1]} and ${gemTypes[2]}."
                }
                else -> {
                    throw IllegalStateException("tip is wrong")
                }
            }
        }
        else{ hint = "there is no help for you" }
        return hint
        } else { throw IllegalArgumentException ("NOT UR TURN") }
    }

    //player-game-action
    /**
     * Move gems from to board to player's hand
     * @param types mutable list of gem types, which player chosen
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     */
    fun takeGems(types : MutableList<GemType>, user : Player){
        val game = rootService.currentGame!!
        if(!game.currentGameState.currentPlayer.hasDoneTurn){
            val currentGameState = game.currentGameState
            val board = currentGameState.board
            if(currentGameState.isInitialState){ rootService.gameService.createNewGameState(false) }
            val numDiffTypes = currentGameState.board.gems.filter{it.key != GemType.YELLOW}.filterValues{ it > 0 }.size
            val numDiffGemTypesInTypes = types.map { it.name }.toSet().size

            // list of gem types has invalid size or content
            if( types.size > 3 || (types.size == 3 && numDiffGemTypesInTypes != 3) || types.contains(GemType.YELLOW)) {
                throw IllegalArgumentException("no valid gem/type number") }
            else if (types.size < 3 && numDiffGemTypesInTypes == types.size && types.size != numDiffTypes) {
                throw IllegalArgumentException("no valid gem/type number") }
            else if ( types.size == 2 && numDiffGemTypesInTypes == 1 && board.gems.getValue(types[0]) < 4) {
                throw IllegalArgumentException("two same gems can only be chosen if four gems of their type are left") }
            // take gems
            else{ types.forEach{ gemType ->
                user.gems[gemType] = user.gems.getValue(gemType) + 1
                board.gems[gemType] = board.gems.getValue(gemType) - 1 } }
            rootService.currentGame!!.currentGameState.consecutiveNoAction = 0
            // update GUI
            onAllRefreshables{ refreshAfterTakeGems()}
            rootService.currentGame!!.currentGameState.currentPlayer.hasDoneTurn = true
        } else { throw IllegalArgumentException ("NOT UR TURN") }
    }

    /**
     * player buys a card
     * @param card the card the player wants to buy
     * @param boardGameCard true if the card was not reserved first
     * @param payment represents the gems the player chooses to pay with
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     * @throws IllegalArgumentException if the card can't be bought with given payment (and boni)
     */
    fun buyCard(card: DevCard, boardGameCard: Boolean, payment: Map<GemType, Int>, user : Player){
        val game = rootService.currentGame!!
        if(!game.currentGameState.currentPlayer.hasDoneTurn) {
            val board = game.currentGameState.board
            if(game.currentGameState.isInitialState) { rootService.gameService.createNewGameState(false) }
                if (rootService.gameService.isCardAcquirable(card, payment)) {
                    if (boardGameCard) {
                        //move card from board to player.devCards
                        when (card.level) {
                            1 -> {
                                board.levelOneOpen.remove(card)
                            }
                            2 -> {
                                board.levelTwoOpen.remove(card)
                            }
                            3 -> {
                                board.levelThreeOpen.remove(card)
                            }
                            else -> {
                                throw IllegalArgumentException("illegal card.level " + card.level)
                            }
                        }
                        rootService.gameService.refill(card.level)
                    } else {
                        //move card from player.reservedCards to player.devCards
                        user.reservedCards.remove(card)
                    }
                    //move the gems in payment from player's hand back to board
                    payment.forEach { (gemType, value) ->
                        user.gems[gemType] = user.gems.getValue(gemType) - value
                        board.gems[gemType] = board.gems.getValue(gemType) + value
                    }
                    user.score += card.prestigePoints
                    user.bonus[card.bonus] = user.bonus.getValue(card.bonus) + 1
                    user.devCards.add(card)
                } else throw IllegalArgumentException("card is not acquirable")
            rootService.currentGame!!.currentGameState.consecutiveNoAction = 0
            onAllRefreshables { refreshAfterBuyCard(card) }
            rootService.currentGame!!.currentGameState.currentPlayer.hasDoneTurn = true
        } else { throw IllegalArgumentException("NOT UR TURN") }
    }

    /**
     * player reserves a card
     * @param card the player wants to reserve
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     * @throws IllegalArgumentException if the player already has three reserved cards
     */
    fun reserveCard(card: DevCard, user : Player){
        if(!rootService.currentGame!!.currentGameState.currentPlayer.hasDoneTurn){
            if(rootService.currentGame!!.currentGameState.isInitialState){
                rootService.gameService.createNewGameState(false)
            }
        val board = rootService.currentGame!!.currentGameState.board
            if(user.reservedCards.size < 3){
                //move card from board to player.reservedCards
                val level = card.level
                if (level == 1) {
                    if(board.levelOneOpen.contains(card)) {
                        board.levelOneOpen.remove(card)
                        rootService.gameService.refill(card.level) }
                    else{ board.levelOneCards.remove(card) }
                } else if (level == 2) {
                    if(board.levelTwoOpen.contains(card)) {
                        board.levelTwoOpen.remove(card)
                        rootService.gameService.refill(card.level)
                    }
                    else{ board.levelTwoCards.remove(card) }
                } else {
                    if(board.levelThreeOpen.contains(card)) {
                        board.levelThreeOpen.remove(card)
                        rootService.gameService.refill(card.level) }
                    else{ board.levelThreeCards.remove(card) }
                }
                user.reservedCards.add(card)

                val numberGold = board.gems[GemType.YELLOW]
                if(numberGold != 0) {
                    //move gold gem from game.board to player
                    user.gems[GemType.YELLOW] = user.gems.getValue(GemType.YELLOW) + 1
                    board.gems[GemType.YELLOW] = board.gems.getValue(GemType.YELLOW) - 1 }
            }
            else throw IllegalArgumentException("a player can only reserve up to three cards")
            rootService.currentGame!!.currentGameState.consecutiveNoAction = 0
            onAllRefreshables{ refreshAfterReserveCard(card)}
            rootService.currentGame!!.currentGameState.currentPlayer.hasDoneTurn = true
        } else { throw IllegalArgumentException("NOT UR TURN") }
    }

    /**
     * gives the player a selected nobleTile
     * @param card the selected nobleTile
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     */
    fun selectNobleTile(card: NobleTile, user : Player){
        val game = rootService.currentGame!!
        val board = game.currentGameState.board
        val availableCards = rootService.gameService.checkNobleTiles()
        if (availableCards.contains(card)) {
                board.nobleTiles.remove(card)
                user.nobleTiles.add(card)
                user.score += card.prestigePoints
            } else { throw IllegalArgumentException("the chosen card is not available for the current player") }
        onAllRefreshables { refreshAfterSelectNobleTile(card) }
    }

    /**
     * return selected gems from the player to the board
     * @param gems the selected gems
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     */
    fun returnGems(gems: List<GemType>, user : Player) {
        val game = rootService.currentGame!!
        val board = game.currentGameState.board
        if (rootService.gameService.checkGems()) {
            gems.forEach { gemType ->
                user.gems[gemType] = user.gems.getValue(gemType) - 1
                board.gems[gemType] = board.gems.getValue(gemType) + 1
                onAllRefreshables { refreshAfterTakeGems() }
            }
        }
        else { throw IllegalArgumentException("you don't have more than ten gems") }
    }
}