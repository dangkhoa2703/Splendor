package view

import entity.DevCard
import entity.GemType
import entity.NobleTile
import entity.Player

/**[Refreshable] : Interface containing various refreshable methods for Splendor. */
interface Refreshable {

    /**[refreshAfterTakeGems] : Refreshable method, refreshes the game / scene after Gems are taken.*/
    fun refreshAfterTakeGems(gems: Map<GemType, Int>){}

    /**[refreshAfterStartNewGame] : Refreshable method, refreshes the game / scene after new Game is started.*/
    fun refreshAfterStartNewGame() {}

    /**[refreshAfterEndGame] : Refreshable method, refreshes the game / scene after a Game is ended.*/
    fun refreshAfterEndGame() {}

    /**[refreshAfterPopup] : Refreshable method, refreshes the game / scene after showPlayers popup is triggered.*/
    fun refreshAfterPopup(currentPlayer: Player){}

    /**[refreshAfterShowHighscores] : Refreshable method, refreshes the game / scene after viewing highscores.*/
    fun refreshAfterShowHighscores() {}

    /**[refreshAfterEndTurn] : Refreshable method, refreshes the game / scene after a turn is ended.*/
    fun refreshAfterEndTurn()  {}

    /**[refreshAfterTakeGems] : Refreshable method, refreshes the game / scene after a current player takes Gems.*/
    fun refreshAfterTakeGems()  {}

    /**[refreshAfterBuyCard] : Refreshable method, refreshes the game / scene after a dev card is purchased from a
     * player.*/
    fun refreshAfterBuyCard(devCard: DevCard){}

    /**[refreshAfterReserveCard] : Refreshable method, refreshes the game / scene after a card is reserved
     * by a player.*/
    fun refreshAfterReserveCard(devCard: DevCard) {}

    //fun refreshAfterLoadGame()  {}

    // fun refreshAfterUndoRedo()  {}

    //fun refreshScore() {}
    /**[refreshAfterSelectNobleTile] : Refreshable method, refreshes the game / scene after a player selects desired
     * Gems in his turn.*/
    fun refreshAfterSelectNobleTile(nobleTile: NobleTile)  {}

    // fun refreshAfterShowHint()  {}

   // fun refreshAfterReturnGems(){}
}
