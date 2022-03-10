package view

import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual

class GameScene(private val rootService: RootService): BoardGameScene(1920, 1080), Refreshable {

    private val quitButton = Button(
		width = 200, height = 100,
		posX = 100, posY = 100,
		text = "Exit",
		font = Font(size = 28),
    )

	private val undoButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 100,
		text = "Undo",
		font = Font(size = 28)
	)

	private val redoButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 250,
		text = "Redo",
		font = Font(size = 28)
	)

    private val hintButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 400,
		text = "Show Hint",
		font = Font(size = 28)
	)

	private val loadGameButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 550,
		text = "Load Game",
		font = Font(size = 28)
	)

	private val saveGameButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 700,
		text = "Save Game",
		font = Font(size = 28)
	)

	private val loadHighscoreButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 850,
		text = "Highscores",
		font = Font(size = 28)
	)

	private val startNewGameButton = Button(
		width = 200, height = 100,
		posX = 1650, posY = 1000,
		text = "New Game",
		font = Font(size = 28)
	)

	private val take2GemsButton= Button(
		width = 200, height = 100,
		posX = 1350, posY =100 ,
		text = "Take 2 Gems",
		font = Font(size = 28)
	)

	private val take3GemsButton= Button(
		width = 200, height = 100,
		posX = 1350, posY =250 ,
		text = "Take 3 Gems",
		font = Font(size = 28)
	)

	private val buyCardButton= Button(
		width = 200, height = 100,
		posX = 1350, posY =400 ,
		text = "Buy Card",
		font = Font(size = 28)
	)

	private val reserveCardButton= Button(
		width = 200, height = 100,
		posX = 1350, posY =550 ,
		text = "Reserve Card",
		font = Font(size = 28)
	)

	private val returnGemsButton= Button(
		width = 200, height = 100,
		posX = 1350, posY =700 ,
		text = "Return Gems",
		font = Font(size = 28)
	)

	private val nextPlayerButton= Button(
		width = 200, height = 100,
		posX = 1350, posY =850 ,
		text = "Next Player",
		font = Font(size = 28)
	)

    init {
	background = ColorVisual(108, 168, 59)

	addComponents(
	    quitButton,
	)
    }
}
