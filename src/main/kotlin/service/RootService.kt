package service

import entity.Splendor
import view.Refreshable

class RootService {
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)
    var currentGame : Splendor? = null

    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }

    fun addRefreshable(newRefreshable: Refreshable) {
	gameService.addRefreshable(newRefreshable)
	playerActionService.addRefreshable(newRefreshable)
    }
}
