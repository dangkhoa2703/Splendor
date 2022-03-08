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
        // rootService.gameService.endTurn()
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
        //rootService.gameService.endTurn()
    }
    fun buyCard(card: DevCard, boardGameCard: Boolean, payment: Map<GemType, Int>){}
    fun reserveCard(card: DevCard){}

    fun selectNobleTile(card: NobleTile){}
    fun returnGems(gems: List<GemType>){}
}
