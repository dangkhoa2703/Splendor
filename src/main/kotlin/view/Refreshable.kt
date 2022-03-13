package view

import entity.DevCard

interface Refreshable {
    fun refreshAfterStartNewGame() : Unit {}

    fun refreshAfterEndGame() : Unit {}

    fun refreshAfterShowHighscores() : Unit {}

    fun refreshAfterEndTurn() : Unit {}

    fun refreshAfterTakeGems() : Unit {}

    fun refreshAfterBuyCard(devCard: DevCard) : Unit {}

    fun refreshAfterReserveCard(devCard: DevCard) : Unit {}

    fun refreshAfterLoadGame() : Unit {}

    fun refreshAfterUndoRedo() : Unit {}

    fun refreshScore() : Unit {}

    fun refreshAfterSelectNobleTile() : Unit {}

    fun refreshAfterShowHint() : Unit {}

    fun refreshAfterReturnGems(): Unit {}
}
