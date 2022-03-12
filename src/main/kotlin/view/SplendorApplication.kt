package view

import service.GameService
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.BoardGameScene
import service.RootService
import tools.aqua.bgw.core.MenuScene

class SplendorApplication: BoardGameApplication("Splendor"), Refreshable{

    private val rootService = RootService()

    private val gameScene :GameScene = GameScene(rootService).apply{
	quitButton.onMouseClicked = {
	}
    }


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
    }

	private val gameFinishScene: MenuScene = GameFinishScene(rootService).apply {
		backButton.onMouseClicked = {
			this@SplendorApplication.hideMenuScene()
			this@SplendorApplication.showMenuScene(startScene)
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

	override fun refreshAfterEndGame() {
		this@SplendorApplication.showMenuScene(gameFinishScene)
	}

    override fun refreshAfterStartNewGame() {
	this@SplendorApplication.hideMenuScene()
	this@SplendorApplication.showGameScene(gameScene)
    }
    
    init{
	rootService.addRefreshables(
	    this as Refreshable,
	    gameScene as Refreshable,
	    loadGameScene as Refreshable , startScene as Refreshable,
	    highscoreScene as Refreshable, configScene as Refreshable,
		gameFinishScene as Refreshable
	)
	
	this.showMenuScene(startScene)
    }
}
