package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button

class LoadGameScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

    private val headLineLabel = Label(
	width = 300, height = 200,
	posX = width/2 - 150, posY = 50,
	text = "Load Game",
	font = Font(size = 44)
    )

    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = ColorVisual(136, 136, 221)
    )
    
    init{
	background = ColorVisual(108, 168, 59)

	addComponents(
	    headLineLabel,
	    backButton,
	)
    }
}
