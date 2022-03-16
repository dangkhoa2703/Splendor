package view

import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import service.RootService
import entity.SplendorImageLoader

/**
 * [MenuScene] that is used for launching the Game. It is displayed directly at program start or reached
 * when "new game" is clicked in [GameFinishScene]. There are the options to start a new game, load an
 * existing game or view previous highscores.
 */

class StartScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

	private val imageLoader = SplendorImageLoader()
	private val image = imageLoader.button()

	/**[startNewGameButton] : Button to start a new game.*/
    val startNewGameButton = Button(
		width = 400, height = 100,
		posX = width/2 - 200, posY = 500,
		text = "Start New Game",
		font = Font(size = 30),
		visual = image
    )

	/**[loadGameButton] : Button to load an already existing game.*/
    val loadGameButton = Button(
		width = 400, height = 100,
		posX = width/2 - 200, posY = 650,
		text = "Load Game",
		font = Font(size = 30),
		visual = image
	)

	/**[loadHighscoreButton] : Button to load highscores of previous game winners.*/
    val loadHighscoreButton = Button(
		width = 400, height = 100,
		posX = width/2 - 200, posY = 800,
		text = "Load Highscore",
		font = Font(size = 30),
		visual = image
    )

	/**[quitButton] : Button to quit the game.*/
    val quitButton = Button(
		width = 200, height = 100,
		posX = 50, posY = 930,
		text = "Exit",
		font = Font(size = 28),
		visual = image
    )

	/**Block to initialize our view components first.*/
	init {
	val imageLoader = SplendorImageLoader()
	val image = imageLoader.startBackground()

	background = image
	//background = ColorVisual(108, 168, 59)

	opacity = 1.0
	
	addComponents(
	    startNewGameButton,
	    loadGameButton,
	    loadHighscoreButton,
	    quitButton,
	)
    }
}
