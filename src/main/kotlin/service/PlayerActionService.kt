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
     */
    fun undo(){}

    /**
     * redoes the last undone player-game-action
     */
    fun redo(){}

    /**
     * @return a hint for the best next move for the current player and current gamesituation
     */
    fun showHint():String{return ""}

    //player-game-action
    /**
     * player takes two gems from the board
     * @param type is the GemType of the two chosen gems
     * @throws IllegalArgumentException if there aren't at least four gems of the given type left
     */
    fun takeTwoSameGems(type : GemType){
        val game = rootService.currentGame!!.currentGameState
        val player = game.currentPlayer
        val  numberGemsOfType = game.board.gems[type]
        checkNotNull(numberGemsOfType)
        // if the board has at least four gems of type, move two gems from game.board to player
        if( numberGemsOfType >= 4 ){
            game.board.gems[type] to numberGemsOfType-2
            val numberPlayerGemsOfType = player.gems[type]
            checkNotNull(numberPlayerGemsOfType)
            player.gems[type] to numberPlayerGemsOfType+2
        }
        else
            throw IllegalArgumentException("there aren't at least four gems of the given type left")
        // update GUI
        //refreshAfterTakeTwoGems()
        // visit by nobleTiles, check gems
        rootService.gameService.endTurn()
    }

    /**
     * player takes (up to) three different gems from the board
     * @param types are the different GemTypes of the chosen gems
     * @throws IllegalArgumentException if the board has at least three different gemTypes left but
     * types contains two or less gemTypes
     */
    fun takeThreeDifferentGems(types : MutableList<GemType>){
        val game = rootService.currentGame!!.currentGameState
        val player = game.currentPlayer
        var numberOfDifferentGemTypes = 0
        var numberGemsOfType : Int?
        for (gemType in GemType.values()){
            numberGemsOfType = game.board.gems[gemType]
            checkNotNull(numberGemsOfType)
            if(numberGemsOfType !=0){
                numberOfDifferentGemTypes++
            }
        }
        //move the gems from game.board to player
        var actionPerformed = false
        if( types.size == 3 && numberOfDifferentGemTypes >= 3 ){
            numberGemsOfType = game.board.gems[types[0]]
            checkNotNull(numberGemsOfType)
            game.board.gems[types[0]] to numberGemsOfType-1
            player.gems[types[0]] to player.gems[types[0]]!!+1
            types.removeAt(0)
            numberOfDifferentGemTypes = 2
        }
        if( types.size == 2 && numberOfDifferentGemTypes == 2 ){
            numberGemsOfType = game.board.gems[types[0]]
            checkNotNull(numberGemsOfType)
            game.board.gems[types[0]] to numberGemsOfType-1
            player.gems[types[0]] to player.gems[types[0]]!!+1
            types.removeAt(0)
            numberOfDifferentGemTypes = 1
        }
        if(types.size == 1 && numberOfDifferentGemTypes == 1 ){
            numberGemsOfType = game.board.gems[types[0]]
            checkNotNull(numberGemsOfType)
            game.board.gems[types[0]] to numberGemsOfType-1
            player.gems[types[0]] to player.gems[types[0]]!!+1
            actionPerformed = true
        }
        if(!actionPerformed)
            throw IllegalArgumentException("not enough gems were chosen")

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
     * @throws IllegalArgumentException if the card can't be bought with given payment (and boni)
     */
    fun buyCard(card: DevCard, boardGameCard: Boolean, payment: Map<GemType, Int>){
        val game = rootService.currentGame!!.currentGameState
        val player = game.currentPlayer

        if( rootService.gameService.isCardAcquirable(card, payment)) {
            if (boardGameCard) {
                //move card from board to player.devCards
                val level = card.level
                if (level == 1) {
                    game.board.levelOneCards.remove(card)
                } else if (level == 1) {
                    game.board.levelTwoCards.remove(card)
                } else {
                    game.board.levelThreeCards.remove(card)
                }
                rootService.gameService.refill(card.level)
            } else {
                //move card from player.reservedCards to player.devCards
                player.reservedCards.remove(card)
            }
            //move the gems in payment from player's hand back to board
            for (type in GemType.values()){
                if(payment[type] != null){
                    player.gems[type] to player.gems[type]!!-payment[type]!!
                    game.board.gems[type] to game.board.gems[type]!! + payment[type]!!
                }
            }
            player.score += card.PrestigePoints
            val bonusType = card.bonus
            player.bonus[bonusType] to player.bonus[bonusType]!!+1
            player.devCards.add(card)
        }
        else
            throw IllegalArgumentException("card is not acquirable")

        // update GUI
        //refreshAfterBuyCard()
        // visit by nobleTiles, check gems
        rootService.gameService.endTurn()
    }

    /**
     * player reserves a card
     * @param card the player wants to reserve
     * @throws IllegalArgumentException if the player already has three reserved cards
     */
    fun reserveCard(card: DevCard){
        val game = rootService.currentGame!!.currentGameState
        val player = game.currentPlayer

        if(player.reservedCards.size < 3) {
            //move card from board to player.reservedCards
            val level = card.level
            if (level == 1) {
                game.board.levelOneCards.remove(card)
            }
            else if (level == 1) {
                game.board.levelTwoCards.remove(card)
            }
            else {
                game.board.levelThreeCards.remove(card)
            }
            rootService.gameService.refill(card.level)
        }
        val numberGold = game.board.gems[GemType.YELLOW]
        checkNotNull(numberGold)
        if(numberGold!=0) {
            //move gold gem from game.board to player
            game.board.gems[GemType.YELLOW] to numberGold-1
            player.gems[GemType.YELLOW] to player.gems[GemType.YELLOW]!!+1
        }
        else
            throw IllegalArgumentException("a player can only reserve up to three cards")

        // update GUI
        //refreshAfterReserveCard()
        // visit by nobleTiles, check gems
        rootService.gameService.endTurn()

    }

    /**
     * gives the player a selected nobleTile
     * @param card the selected nobleTile
     */
    fun selectNobleTile(card: NobleTile){}

    /**
     * return selected gems from the player to the board
     * @param gems the selected gems
     */
    fun returnGems(gems: List<GemType>){}
}
