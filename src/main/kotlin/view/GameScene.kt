package view

import entity.SplendorImageLoader
import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.image.BufferedImage
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.DEFAULT_CARD_HEIGHT
import tools.aqua.bgw.core.DEFAULT_CARD_WIDTH
import tools.aqua.bgw.visual.Visual

class GameScene(private val rootService: RootService): BoardGameScene(1920, 1080), Refreshable {

	val imageLoader = SplendorImageLoader()
	val image = imageLoader.button()
	val undo = imageLoader.undoButton()
	val redo = imageLoader.redoButton()
	val hint = imageLoader.hintButton()
	val tableImage = imageLoader.table()
	val cardBack = imageLoader.cardBack()

    private val quitButton = Button(
		width = 25, height = 25,
		posX = 0, posY = 0,
		text = "X",
		font = Font(size = 15),
		visual = ColorVisual(255,0,0)
    )

	private val undoButton = Button(
		width = 50, height = 50,
		posX = 50, posY = 150,
		text = "",
		font = Font(size = 28),
		visual = undo
	)

	private val redoButton = Button(
		width = 50, height = 50,
		posX = 200, posY = 150,
		text = "",
		font = Font(size = 28),
		visual = redo
	)

    private val hintButton = Button(
		width = 100, height = 50,
		posX = 50, posY = 500,
		text = "",
		font = Font(size = 28),
		visual = hint
	)

	private val saveGameButton = Button(
		width = 200, height = 100,
		posX = 50, posY = 950,
		text = "Save Game",
		font = Font(size = 17),
		visual = image
	)

	private val loadHighscoreButton = Button(
		width = 200, height = 100,
		posX = 50, posY = 800,
		text = "Highscores",
		font = Font(size = 17),
		visual = image
	)

	private val returnGemsButton= Button(
		width = 200, height = 100,
		posX = 50, posY =650 ,
		text = "Return Gems",
		font = Font(size = 17),
		visual = image
	)

	private val nextPlayerButton= Button(
		width = 200, height = 100,
		posX = 1650, posY =540 ,
		text = "Next Player",
		font = Font(size = 28),
		visual = image
	)

	private val nobleTile1 = CardView(
		300,200,
		DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT,
		front= Visual.EMPTY, back = cardBack
	)
	private val nobleTile2 = CardView(
		450,200,
		DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT,
		front= Visual.EMPTY, back = cardBack
	)

	private val nobleTile3 = CardView(
		600,200,
		DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT,
		front= Visual.EMPTY, back = cardBack
	)
	private val nobleTile4 = CardView(
		750,200,
		DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT,
		front= Visual.EMPTY, back = cardBack
	)
	private val nobleTile5 = CardView(
		900,200,
		DEFAULT_CARD_WIDTH, DEFAULT_CARD_HEIGHT,
		front= Visual.EMPTY, back = cardBack
	)

//TODO add name of current Player into the currentPlayer label in refreshable Method
	private val currentPlayer = Label(
		posX = 700, posY= 50, width = 300 , height= 50, text = "TestPlayer", font = Font(size = 30), visual = image
	)

    init {

		background = tableImage


	addComponents(
	    quitButton,
		undoButton,
		redoButton,
		nextPlayerButton,
		returnGemsButton,
		loadHighscoreButton,
		hintButton,
		saveGameButton,
		currentPlayer,
		nobleTile1,
		nobleTile2,
		nobleTile3,
		nobleTile4,
		nobleTile5
	)
    }


}
