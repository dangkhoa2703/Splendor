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
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.util.BidirectionalMap
import entity.DevCard
import entity.NobleTile
import tools.aqua.bgw.core.Alignment

class GameScene(private val rootService: RootService): BoardGameScene(1920, 1080), Refreshable {

    private val imageLoader = SplendorImageLoader()
    private val buttonImage = imageLoader.button()
    private val undoImage = imageLoader.undoButton()
    private val redoImage = imageLoader.redoButton()
    private val hintImage = imageLoader.hintButton()
    private val tableImage = imageLoader.table()
    private val cardBack = imageLoader.cardBack()

    val quitButton = Button(
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
	visual = undoImage
    )

    private val redoButton = Button(
	width = 50, height = 50,
	posX = 200, posY = 150,
	text = "",
	font = Font(size = 28),
	visual = redoImage
    )

    private val hintButton = Button(
	width = 100, height = 50,
	posX = 50, posY = 500,
	text = "",
	font = Font(size = 28),
	visual = hintImage
    )

    private val saveGameButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 950,
	text = "Save Game",
	font = Font(size = 17),
	visual = buttonImage
    )

    private val loadHighscoreButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 800,
	text = "Highscores",
	font = Font(size = 17),
	visual = buttonImage
    )

    private val returnGemsButton= Button(
	width = 200, height = 100,
	posX = 50, posY =650 ,
	text = "Return Gems",
	font = Font(size = 17),
	visual = buttonImage
    )

    private val nextPlayerButton= Button(
	width = 200, height = 100,
	posX = 1650, posY =540 ,
	text = "Next Player",
	font = Font(size = 28),
	visual = buttonImage
    )

    //TODO add name of current Player into the currentPlayer label in refreshable Method
    private val currentPlayer = Label(
	posX = width/2 - 150, posY= 50, width = 300 , height= 50, text = "TestPlayer", font = Font(size = 30), visual = buttonImage
    )

    private val devCardMap: BidirectionalMap<DevCard, CardView> = BidirectionalMap()
    private val nobleTileMap: BidirectionalMap<NobleTile, CardView> = BidirectionalMap()

    private val gameLists: MutableList<LinearLayout<CardView>> = mutableListOf()
    private val gameStack: MutableList<LabeledStackView> = mutableListOf()

    override fun refreshAfterStartNewGame() {
	val game = rootService.currentGame
	checkNotNull(game) { "No game found." }
	
	devCardMap.clear()
	nobleTileMap.clear()
	gameLists.forEach{ list -> list.clear() }
	gameLists.clear()

	initializeNobleCardsView(game.currentGameState.board.nobleTiles)

	initializeDevCardStack(game.currentGameState.board.levelOneCards, 1)
	initializeDevCardList(game.currentGameState.board.levelOneOpen, 1)
	initializeDevCardList(game.currentGameState.board.levelTwoOpen, 2)
	initializeDevCardList(game.currentGameState.board.levelThreeOpen, 3)

	gameLists.forEach{ addComponents(it) }
    }

    private fun initializeNobleCardsView(nobleTiles: MutableList<NobleTile>) {
	val layout: LinearLayout<CardView> = LinearLayout(
	    posX = width/2 - 550, posY = 100, width = 1100, height = 180, spacing=30,
	    alignment = Alignment.CENTER
	)

	for(i in 0..nobleTiles.size-1) {
	    println(nobleTiles[i].id)
	    val cardView = CardView(
		height = 150, width = 95,
		front = imageLoader.frontImageFor(nobleTiles[i].id),
		back = cardBack,
	    )
	    cardView.showFront()
	    nobleTileMap.add(nobleTiles[i] to cardView)
	    layout.add(cardView)
	}
	
	gameLists.add(layout)
    }

    private fun initializeDevCardStack(devCards: MutableList<DevCard>, level: Int) {
	
    }

    private fun initializeDevCardList(devCards: MutableList<DevCard>, level: Int) {
	val layout: LinearLayout<CardView> = LinearLayout(
	    posX = width/2 - 440, posY = 650-(level-1)*180, width = 880, height = 180, spacing=30,
	    alignment = Alignment.CENTER
	)

	for(i in 0..devCards.size-1) {
	    val cardView = CardView(
		height = 150, width = 95,
		front = imageLoader.frontImageFor(devCards[i].id),
		back = cardBack,
	    )
	    cardView.isDraggable = true
	    cardView.onDragGestureEnded =
		{ _, succes ->
		      if(succes) {
			  cardView.isDraggable = false
		      }
		}
	    cardView.showFront()
	    devCardMap.add(devCards[i] to cardView)
	    layout.add(cardView)
	}
	
	gameLists.add(layout)
    }
    
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
	)
    }


}
