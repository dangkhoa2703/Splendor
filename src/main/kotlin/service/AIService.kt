package service

import entity.*

/**
 *  Class for artificial intelligence
 * */
class AIService(private val rootService: RootService): AbstractRefreshingService()
{
    /** calculates the best possible Turn for the current player and returns an object of type Turn which includes
     *  a map of gems and a list of at most one card whose contents depend on the TurnType */
    fun calculateBestTurn(player : Player, gameState: GameState) : Turn?
    {
        return null
    }
}