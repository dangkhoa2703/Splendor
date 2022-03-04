package view

import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

class GameScene(private val rootService: RootService): BoardGameScene(1920, 1080), Refreshable {

    private val quitButton = Button(
	width = 200, height = 100,
	posX = 100, posY = 100,
	text = "Exit",
	font = Font(size = 28),
    )
    
    init {
	background = ColorVisual(108, 168, 59)

	addComponents(
	    quitButton,
	)
    }
}
