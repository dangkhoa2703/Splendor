package view

import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import service.RootService
import entity.SplendorImageLoader

class StartScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

	val imageLoader = SplendorImageLoader()
	val image = imageLoader.button()

    val startNewGameButton = Button(
		width = 400, height = 100,
		posX = width/2 - 200, posY = 500,
		text = "Start New Game",
		font = Font(size = 30),
		visual = image
    )

    val loadGameButton = Button(
		width = 400, height = 100,
		posX = width/2 - 200, posY = 650,
		text = "Load Game",
		font = Font(size = 30),
		visual = image
	)

    val loadHighscoreButton = Button(
		width = 400, height = 100,
		posX = width/2 - 200, posY = 800,
		text = "Load Highscore",
		font = Font(size = 30),
		visual = image
    )

    val quitButton = Button(
		width = 200, height = 100,
		posX = 50, posY = 930,
		text = "Exit",
		font = Font(size = 28),
		visual = image
    )

    
    init {
	val imageLoader = SplendorImageLoader()
	val image = imageLoader.startBackground()

	background = image
	//background = ColorVisual(108, 168, 59)
	
	addComponents(
	    startNewGameButton,
	    loadGameButton,
	    loadHighscoreButton,
	    quitButton,
	)
    }
}
