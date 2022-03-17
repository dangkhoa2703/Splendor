package view

import entity.Player
import service.RootService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.MenuScene
//import java.io.File

/**
 * Implementation of the Splendor [BoardGameApplication] for the game Splendor.
 * With the private values in SplendorApplication, there is the ability to switch between the
 * different scenes in the Splendor game
 */
class SplendorApplication : BoardGameApplication("Splendor"), Refreshable {

    private val rootService = RootService()

    /**[gameScene] : Of type GameScene; by clicking on the quit button the current game is
    assigned to null and we jump back to the startScene. */
    private val gameScene: GameScene = GameScene(rootService).apply {
        quitButton.onMouseClicked = {
            rootService.currentGame = null
            this@SplendorApplication.showMenuScene(startScene)
        }

    //pop up to show
    nextPlayersButton.onMouseClicked = {
            val currentPlayer: Player = recievePlayer()
            rootService.playerActionService.showPlayers(currentPlayer)
            this@SplendorApplication.showMenuScene(popUpScene)
        }
    }

    /**[popUpScene] : Of type MenuScene; with popUpScene the current scene is hidden when the
    quitButton is clicked. */
    private val popUpScene: MenuScene = PopupScene(rootService, gameScene).apply {
        quitButton.onMouseClicked = {
            this@SplendorApplication.hideMenuScene()
        }
    }

    /**[loadGameScene] : Of type MenuScene; applied to the LoadGameScene. By clicking the
    back Button the LoadGameScene is hidden to show the startScene / move back to the start
    scene. */
    private val loadGameScene: MenuScene = LoadGameScene(rootService).apply {
        backButton.onMouseClicked = {
            this@SplendorApplication.hideMenuScene()
            this@SplendorApplication.showMenuScene(startScene)
        }
        startButton.onMouseClicked = {
            this@SplendorApplication.hideMenuScene()
            loadGame()
            this@SplendorApplication.showGameScene(gameScene)
            gameScene.refreshAfterEndTurn()
        }
    }

	/**[configScene] : Of type MenuScene; applied to the ConfigScene. By clicking the
	back Button the ConfigScene is hidden to show the startScene / move back to the start
	scene. */
    private val configScene: MenuScene = ConfigScene(rootService).apply {
        backButton.onMouseClicked = {
            this@SplendorApplication.hideMenuScene()
            this@SplendorApplication.showMenuScene(startScene)
        }
    }

	/**[gameFinishScene] : Of type MenuScene; applied to the GameFinishScene. By clicking the
	back Button the GameFinishScene is hidden to show the startScene / move back to the start
	scene. */
    private val gameFinishScene: MenuScene = GameFinishScene(rootService).apply {
        backButton.onMouseClicked = {
            this@SplendorApplication.hideMenuScene()
            this@SplendorApplication.showMenuScene(startScene)
        }
    }

	/**[highscoreScene] : Of type MenuScene; applied to the HighScoreScene. By clicking the
	back Button the GameFinishScene is hidden to show the startScene / move back to the start
	scene. */
    private val highscoreScene: MenuScene = HighscoreScene(rootService).apply {
        backButton.onMouseClicked = {
            rootService.playerActionService.onAllRefreshables{ refreshAfterShowHighscores() }
            this@SplendorApplication.hideMenuScene()
            this@SplendorApplication.showMenuScene(startScene)
        }
    }

	/**[startScene] : Of type MenuScene , applied to the StartScene. Various buttons to navigate
	  through the different scenes. */
    private val startScene: MenuScene = StartScene(rootService).apply {
        //navigate to the configScene
		startNewGameButton.onMouseClicked = {
            this@SplendorApplication.showMenuScene(configScene)
        }

		//navigate to the loadGameScene
        loadGameButton.onMouseClicked = {
            this@SplendorApplication.showMenuScene(loadGameScene)
        }

		//navigate to the highscoreScene
        loadHighscoreButton.onMouseClicked = {
            this@SplendorApplication.showMenuScene(highscoreScene)
        }

		//exit the game
        quitButton.onMouseClicked = {
            exit()
        }
    }

	/** [refreshAfterEndGame] : override function of the refreshAfterEndGame
	from Refreshable Interface. Shows gameFinishScene after the game ends. */
    override fun refreshAfterEndGame() {
        this@SplendorApplication.showMenuScene(gameFinishScene)
    }

	/** [refreshAfterStartNewGame] : override function of the refreshAfterStartNewGame
	from Refreshable Interface. Shows gameScene after starting a new game.
	 */
    override fun refreshAfterStartNewGame() {
        this@SplendorApplication.hideMenuScene()
        this@SplendorApplication.showGameScene(gameScene)
    }

	/**Block to initialize our view components first.*/
    init {
        rootService.addRefreshables(
            this as Refreshable,
            gameScene as Refreshable,
            loadGameScene as Refreshable, startScene as Refreshable,
            highscoreScene as Refreshable, configScene as Refreshable,
            gameFinishScene as Refreshable,
            popUpScene as Refreshable,
        )

        this.showMenuScene(startScene)
    }

	/**[recievePlayer] : Method to check if we have an existing player
	  @return player if a player does exist.*/
    private fun recievePlayer(): Player {
        val player: Player? = gameScene.currentPlayer
        checkNotNull(player) { "No player found. " }
        return player
    }
}
