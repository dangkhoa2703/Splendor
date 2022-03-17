package service

import entity.DevCard
import entity.GemType
import entity.NobleTile
import entity.Player
import view.Refreshable

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {

    var refreshAfterTakeGemsCalled: Boolean = false
        private set

    var refreshAfterStarNewGameCalled: Boolean = false
        private set

    var refreshAfterEndGameCalled: Boolean = false
        private set

    var refreshAfterPopUpCalled: Boolean = false
        private set

    var refreshAfterShowHighScoresCalled:Boolean = false
        private set

    var refreshAfterEndTurnCalled: Boolean = false
        private set

    var refreshAfterTakeGemsWithParamCalled: Boolean = false
        private set

    var refreshAfterBuyCardCalled: Boolean = false
        private set

    var refreshAfterReserveCardCalled: Boolean = false
        private set

    var refreshAfterSelectNobleTileCall: Boolean = false
        private set

    fun reset(){
        refreshAfterTakeGemsCalled = false
        refreshAfterStarNewGameCalled = false
        refreshAfterEndGameCalled = false
        refreshAfterPopUpCalled = false
        refreshAfterShowHighScoresCalled = false
        refreshAfterEndTurnCalled = false
        refreshAfterTakeGemsWithParamCalled = false
        refreshAfterBuyCardCalled = false
        refreshAfterReserveCardCalled = false
        refreshAfterSelectNobleTileCall = false
    }


    override fun refreshAfterTakeGems(gems: Map<GemType, Int>){
        refreshAfterTakeGemsWithParamCalled = false
    }

    override fun refreshAfterStartNewGame() {
        refreshAfterStarNewGameCalled = true
    }

    override fun refreshAfterEndGame() {
        refreshAfterEndGameCalled = true
    }

    override fun refreshAfterPopup(currentPlayer: Player){
        println("popup")
        refreshAfterPopUpCalled = true
    }

    override fun refreshAfterShowHighscores() {
        refreshAfterShowHighScoresCalled = true
    }

    override fun refreshAfterEndTurn()  {
        refreshAfterEndTurnCalled = true
    }

    override fun refreshAfterTakeGems()  {
        refreshAfterTakeGemsCalled = true
    }

    override fun refreshAfterBuyCard(devCard: DevCard){
        refreshAfterBuyCardCalled = true
    }

    override fun refreshAfterReserveCard(devCard: DevCard) {
        refreshAfterReserveCardCalled = true
    }

    override fun refreshAfterSelectNobleTile(nobleTile: NobleTile)  {
        refreshAfterSelectNobleTileCall = true
    }

}