package view

interface Refreshable {
    fun refreshAfterStartNewGame() : Unit {}

    fun refreshAfterEndGame() : Unit {}

    fun refreshAfterShowHighscores() : Unit {}

    fun refreshAfterEndTurn() : Unit {}

    fun refreshAfterTakeGems() : Unit {}

    fun refreshAfterBuyCard() : Unit {}

    fun refreshAfterReserveCard() : Unit {}

    fun refreshAfterLoadGame() : Unit {}

    fun refreshAfterUndo() : Unit {}

    fun refreshAfterRedo() : Unit {}

    fun refreshScore() : Unit {}

    fun refreshAfterSelectNobleTile() : Unit {}

    fun refreshAfterShowHint() : Unit {}

    fun refreshAfterReturnGems(): Unit {}
}
