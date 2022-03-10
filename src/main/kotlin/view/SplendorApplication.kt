package view

import tools.aqua.bgw.core.BoardGameApplication
import service.RootService
import tools.aqua.bgw.core.MenuScene

class SplendorApplication: BoardGameApplication("Splendor") {

    private val rootService= RootService()


    private val loadGameScene: MenuScene = LoadGameScene(rootService).apply {
	backButton.onMouseClicked = {
	    this@SplendorApplication.hideMenuScene()
	    this@SplendorApplication.showMenuScene(startScene)
	}
    }

    private val configScene: MenuScene = ConfigScene(rootService).apply {
	backButton.onMouseClicked = {
	    this@SplendorApplication.hideMenuScene()
	    this@SplendorApplication.showMenuScene(startScene)
	}
		startButton.onMouseClicked = {
			this@SplendorApplication.hideMenuScene()
			this@SplendorApplication.showGameScene(GameScene(rootService))
		}
    }

    private val highscoreScene: MenuScene = HighscoreScene(rootService).apply {
	backButton.onMouseClicked = {
	    this@SplendorApplication.hideMenuScene()
	    this@SplendorApplication.showMenuScene(startScene)
	}
    }
    
    private val startScene: MenuScene = StartScene(rootService).apply {
	startNewGameButton.onMouseClicked = {
	    this@SplendorApplication.showMenuScene(configScene)
	}
	loadGameButton.onMouseClicked = {
	    this@SplendorApplication.showMenuScene(loadGameScene)
	}
	loadHighscoreButton.onMouseClicked = {
	    this@SplendorApplication.showMenuScene(highscoreScene)
	}
	quitButton.onMouseClicked = {
	    exit()
	}
    }

    init{
	this.showMenuScene(startScene)
    }
}
