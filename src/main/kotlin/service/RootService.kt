package service

import view.Refreshable

class RootService {
    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)

    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }

    fun addRefreshable(newRefreshable: Refreshable) {
	gameService.addRefreshable(newRefreshable)
	playerActionService.addRefreshable(newRefreshable)
    }
}
