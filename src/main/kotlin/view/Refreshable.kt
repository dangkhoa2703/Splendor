package view

import entity.DevCard
import entity.GemType
import entity.NobleTile
import entity.Player

interface Refreshable {

    fun refreshAfterTakeGems(gems: Map<GemType, Int>){}

    fun refreshAfterStartNewGame() {}

    fun refreshAfterEndGame() {}

    fun refreshAfterPopup(currentPlayer: Player){}

    fun refreshAfterShowHighscores() {}

    fun refreshAfterEndTurn()  {}

    fun refreshAfterTakeGems()  {}

    fun refreshAfterBuyCard(devCard: DevCard){}

    fun refreshAfterReserveCard(devCard: DevCard) {}

    fun refreshAfterLoadGame()  {}

    fun refreshAfterUndoRedo()  {}

    fun refreshScore() {}

    fun refreshAfterSelectNobleTile(nobleTile: NobleTile)  {}

    fun refreshAfterShowHint()  {}

    fun refreshAfterReturnGems(){}
}
