package view

import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import service.RootService
import entity.SplendorImageLoader

class StartScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

    private val headLineLabel = Label(
	width = 300 ,height = 200,
	posX = width/2 - 150,
	posY = 50,
	text = "Welcome to Splendor",
	font = Font(size = 30) 
    )

    val startNewGameButton = Button(
	width = 400, height = 100,
	posX = width/2 - 200, posY = 200,
	text = "Start New Game",
	font = Font(size = 30),
    )

    val loadGameButton = Button(
	width = 400, height = 100,
	posX = width/2 - 200, posY = 350,
	text = "Load Game",
	font = Font(size = 30)
    )

    val loadHighscoreButton = Button(
	width = 400, height = 100,
	posX = width/2 - 200, posY = 500,
	text = "Load Highscore",
	font = Font(size = 30)
    )

    val quitButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Exit",
	font = Font(size = 28), visual = ColorVisual(221, 136, 136)
    )

    
    init {
	val imageLoader = SplendorImageLoader()
	val image = imageLoader.startBackground()

	background = image
	//background = ColorVisual(108, 168, 59)
	
	addComponents(
	    headLineLabel,
	    startNewGameButton,
	    loadGameButton,
	    loadHighscoreButton,
	    quitButton,
	)
    }
}
