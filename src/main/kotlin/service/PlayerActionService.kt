package service

import entity.DevCard
import entity.GemType
import entity.NobleTile

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
        if(game.currentGameState.hasPrevious()){
            game.currentGameState = game.currentGameState.previous
            game.validGame = false
        }
        else throw IllegalStateException("a previous state does not exist")
    }

    /**
     * redoes the last undone player-game-action
     * @throws IllegalStateException if a following state does not exist
     */
    fun redo(){
        val game = rootService.currentGame!!
        if(game.currentGameState.hasNext()){
            game.currentGameState = game.currentGameState.next
            game.validGame = false
        }
        else throw IllegalStateException("a following state does not exist")
    }

    /**
     * @return a hint for the best next move for the current player and current situation
     */
    fun showHint():String{return ""}

    //player-game-action
    /**
     * Move gems from to board to player's hand
     * @param types mutable list of gem types, which player chosen
     */
    fun takeGems(types : MutableList<GemType>){
        val game = rootService.currentGame
        checkNotNull(game)
        val currentGameState = game.currentGameState
        val player = currentGameState.currentPlayer
        val board = currentGameState.board

        val numDiffTypes =
            currentGameState.board.gems.filter{it.key != GemType.YELLOW}.filterValues{ it > 0 }.size
        val numDiffGemTypesInTypes = types.map { it.name }.toSet().size

        // list of gem types has invalid size
        if( types.size > 3 || (types.size == 3 && numDiffGemTypesInTypes != 3)) {
            throw IllegalArgumentException("no valid gem/type number")
        }
        else if (types.size < 3 && numDiffGemTypesInTypes == types.size && types.size != numDiffTypes) {
            throw IllegalArgumentException("no valid gem/type number")
        }
        else if ( types.size == 2 && numDiffGemTypesInTypes == 1 && board.gems.getValue(types[0]) < 4) {
            throw IllegalArgumentException("two same gems can only be chosen if four gems of their GemType are left")
        }
        // take two same gems
        else if((types.size == 2) && (types[0] == types[1])) {
            player.gems[types[0]] = player.gems.getValue(types[0]) + 2
            board.gems[types[0]] = board.gems.getValue(types[0]) - 2
        }
        // take one to three different gems
        else{
            types.forEach{ gemType ->
                player.gems[gemType] = player.gems.getValue(gemType) + 1
                board.gems[gemType] = board.gems.getValue(gemType) - 1
            }
        }
        rootService.gameService.consecutiveNoAction = 0
        // update GUI
        //refreshAfterTakeThreeGems()
        // visit by nobleTiles, check gems
        rootService.gameService.endTurn()
    }

    /**
     * player buys a card
     * @param card the card the player wants to buy
     * @param boardGameCard true if the card was not reserved first
     * @param payment represents the gems the player chooses to pay with
     * @param index the position of the card in her line on the bord (index is 1 to 4)
     * @throws IllegalArgumentException if the card can't be bought with given payment (and boni)
     */
    fun buyCard(card: DevCard, boardGameCard: Boolean, payment: Map<GemType, Int>, index: Int){
        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val player = game.currentGameState.currentPlayer

        if( rootService.gameService.isCardAcquirable(card, payment)) {
            if (boardGameCard) {
                //move card from board to player.devCards
                when (card.level) {
                    1 -> {
                        board.levelOneOpen.remove(card)
                    }
                    2 -> {
                        board.levelTwoOpen.remove(card)
                    }
                    else -> {
                        board.levelThreeOpen.remove(card)
                    }
                }
                rootService.gameService.refill(card.level,index)
            } else {
                //move card from player.reservedCards to player.devCards
                player.reservedCards.remove(card)
            }
            //move the gems in payment from player's hand back to board
            card.price.forEach{ (gemType, value) ->
                player.gems[gemType] = player.gems.getValue(gemType) - value
                board.gems[gemType] = board.gems.getValue(gemType) + value
            }

            player.score += card.prestigePoints
            player.bonus[card.bonus] = player.bonus.getValue(card.bonus) + 1
            player.devCards.add(card)
        } else
            throw IllegalArgumentException("card is not acquirable")

        rootService.gameService.consecutiveNoAction = 0
        // update GUI
        //refreshAfterBuyCard()
        // visit by nobleTiles, check gems
        rootService.gameService.endTurn()
    }

    /**
     * player reserves a card
     * @param card the player wants to reserve
     * @param index the position of the card in her line on the bord (index is 1 to 4)
     * @throws IllegalArgumentException if the player already has three reserved cards
     */
    fun reserveCard(card: DevCard, index:Int){
        val board = rootService.currentGame!!.currentGameState.board
        val player = rootService.currentGame!!.currentGameState.currentPlayer

        if(player.reservedCards.size < 3) {
            //move card from board to player.reservedCards
            val level = card.level
            if (level == 1) {
                if(board.levelOneOpen.contains(card)){
                    board.levelOneOpen.remove(card)
                    rootService.gameService.refill(card.level,index)
                }
                else{board.levelOneCards.remove(card)}
            }else if (level == 2) {
                if(board.levelTwoOpen.contains(card)){
                    board.levelTwoOpen.remove(card)
                    rootService.gameService.refill(card.level,index)
                }
                else{board.levelTwoCards.remove(card)}
            }else {
                if(board.levelThreeOpen.contains(card)){
                    board.levelThreeOpen.remove(card)
                    rootService.gameService.refill(card.level,index)
                }
                else{board.levelThreeCards.remove(card)}
            }
            player.reservedCards.add(card)

            val numberGold = board.gems[GemType.YELLOW]
            checkNotNull(numberGold)
            if(numberGold != 0) {
                //move gold gem from game.board to player
                player.gems[GemType.YELLOW] = player.gems.getValue(GemType.YELLOW) + 1
                board.gems[GemType.YELLOW] = board.gems.getValue(GemType.YELLOW) - 1
            }
        }
        else
            throw IllegalArgumentException("a player can only reserve up to three cards")

        rootService.gameService.consecutiveNoAction = 0
        // update GUI
        //refreshAfterReserveCard()
        // visit by nobleTiles, check gems
        rootService.gameService.endTurn()
    }

    /**
     * gives the player a selected nobleTile
     * @param card the selected nobleTile
     */
    fun selectNobleTile(card: NobleTile){
        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val player = game.currentGameState.currentPlayer

        board.nobleTiles.remove(card)
        player.nobleTiles.add(card)
        player.score += card.prestigePoints
    }

    /**
     * return selected gems from the player to the board
     * @param gems the selected gems
     */
    fun returnGems(gems: List<GemType>){
        val game = rootService.currentGame
        checkNotNull(game)
        val board = game.currentGameState.board
        val player = game.currentGameState.currentPlayer

        gems.forEach{ gemType ->
            player.gems[gemType] = player.gems.getValue(gemType) - 1
            board.gems[gemType] = board.gems.getValue(gemType) + 1
        }
    }
}
