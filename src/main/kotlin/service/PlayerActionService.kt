package service

import entity.DevCard
import entity.GemType
import entity.NobleTile
import entity.Player

/**
 * class to provide the logic for possible actions a player can take
 */
class PlayerActionService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * undoes the last performed player-game-action
     * @throws IllegalStateException if a previous state does not exist
     */
    fun undo(){
        val game = rootService.currentGame!!
        if(game.currentGameState.hasPrevious()) {
            game.currentGameState = game.currentGameState.previous
            game.validGame = false }
        else throw IllegalStateException("a previous state does not exist")
    }

    /**
     * redoes the last undone player-game-action
     * @throws IllegalStateException if a following state does not exist
     */
    fun redo(){
        val game = rootService.currentGame!!
        if(game.currentGameState.hasNext()) {
            game.currentGameState = game.currentGameState.next
            game.validGame = false }
        else throw IllegalStateException("a following state does not exist")
    }

    /**
     * @return a hint for the best next move for the current player and current situation
     */
    fun showHint(): String{return ""}

    //player-game-action
    /**
     * Move gems from to board to player's hand
     * @param types mutable list of gem types, which player chosen
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     */
    fun takeGems(types : MutableList<GemType>, user : Player){
        val game = rootService.currentGame!!
        val currentGameState = game.currentGameState
        val board = currentGameState.board
        if(currentGameState.currentPlayer == user)
        {
            val numDiffTypes =
                currentGameState.board.gems.filter{it.key != GemType.YELLOW}.filterValues{ it > 0 }.size
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
            rootService.gameService.consecutiveNoAction = 0
            // update GUI
            onAllRefreshables{ refreshAfterTakeGems()}
            // visit by nobleTiles, check gems
            // rootService.gameService.endTurn()
        } else { throw IllegalArgumentException("not your turn") }
        rootService.gameService.nextPlayer()
    }

    /**
     * player buys a card
     * @param card the card the player wants to buy
     * @param boardGameCard true if the card was not reserved first
     * @param payment represents the gems the player chooses to pay with
     * @param index the position of the card in her line on the bord (index is 1 to 4)
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     * @throws IllegalArgumentException if the card can't be bought with given payment (and boni)
     */
    fun buyCard(card: DevCard, boardGameCard: Boolean, payment: Map<GemType, Int>, index: Int, user : Player){
        val game = rootService.currentGame!!
        val board = game.currentGameState.board
        if(user == game.currentGameState.currentPlayer)
        {
            if (rootService.gameService.isCardAcquirable(card, payment)) {
                if (boardGameCard) {
                    //move card from board to player.devCards
                    when (card.level) {
                        1 -> { board.levelOneOpen.remove(card) }
                        2 -> { board.levelTwoOpen.remove(card) }
                        else -> { board.levelThreeOpen.remove(card) }
                    }
                    rootService.gameService.refill(card.level, index)
                } else {
                    //move card from player.reservedCards to player.devCards
                    user.reservedCards.remove(card)
                }
                //move the gems in payment from player's hand back to board
                card.price.forEach { (gemType, value) ->
                    user.gems[gemType] = user.gems.getValue(gemType) - value
                    board.gems[gemType] = board.gems.getValue(gemType) + value }
                user.score += card.prestigePoints
                user.bonus[card.bonus] = user.bonus.getValue(card.bonus) + 1
                user.devCards.add(card)
            } else throw IllegalArgumentException("card is not acquirable")
            rootService.gameService.consecutiveNoAction = 0
            // update GUI
            //refreshAfterBuyCard()
            // visit by nobleTiles, check gems
            // rootService.gameService.endTurn()
        } else { throw IllegalArgumentException("not your turn") }
        rootService.gameService.nextPlayer()
        onAllRefreshables {refreshAfterBuyCard(card)}
    }

    /**
     * player reserves a card
     * @param card the player wants to reserve
     * @param index the position of the card in her line on the bord (index is 1 to 4)
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     * @throws IllegalArgumentException if the player already has three reserved cards
     */
    fun reserveCard(card: DevCard, index:Int, user : Player){
        val board = rootService.currentGame!!.currentGameState.board
        if(user == rootService.currentGame!!.currentGameState.currentPlayer){
            if(user.reservedCards.size < 3)
            {
                //move card from board to player.reservedCards
                val level = card.level
                if (level == 1) {
                    if(board.levelOneOpen.contains(card)) {
                        board.levelOneOpen.remove(card)
                        rootService.gameService.refill(card.level, index) }
                    else{ board.levelOneCards.remove(card) }
                } else if (level == 2) {
                    if(board.levelTwoOpen.contains(card)) {
                        board.levelTwoOpen.remove(card)
                        rootService.gameService.refill(card.level, index)
                    }
                    else{ board.levelTwoCards.remove(card) }
                } else {
                    if(board.levelThreeOpen.contains(card)) {
                        board.levelThreeOpen.remove(card)
                        rootService.gameService.refill(card.level, index) }
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
            rootService.gameService.consecutiveNoAction = 0
            // update GUI
            // refreshAfterReserveCard()
            // visit by nobleTiles, check gems
            // rootService.gameService.endTurn()
        } else { throw IllegalArgumentException("not your turn") }
        onAllRefreshables{ refreshAfterReserveCard(card)}
        rootService.gameService.nextPlayer()
    }

    /**
     * gives the player a selected nobleTile
     * @param card the selected nobleTile
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     */
    fun selectNobleTile(card: NobleTile, user : Player){
        val game = rootService.currentGame!!
        val board = game.currentGameState.board
        // if(user == game.currentGameState.currentPlayer)
        // {
        board.nobleTiles.remove(card)
        user.nobleTiles.add(card)
        user.score += card.prestigePoints
        // } else { return }
        rootService.gameService.nextPlayer()
    }

    /**
     * return selected gems from the player to the board
     * @param gems the selected gems
     * @param user is the CurrentPlayer to check if the right player is doing the turn
     */
    fun returnGems(gems: List<GemType>, user : Player){
        val game = rootService.currentGame!!
        val board = game.currentGameState.board
        // if(user == game.currentGameState.currentPlayer)
        // {
        gems.forEach { gemType ->
            user.gems[gemType] = user.gems.getValue(gemType) - 1
            board.gems[gemType] = board.gems.getValue(gemType) + 1 }
        // } else { throw IllegalArgumentException("not your turn") }
    }
}
