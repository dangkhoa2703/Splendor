package view

import entity.*
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
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.components.uicomponents.Orientation
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.event.MouseButtonType
import tools.aqua.bgw.event.MouseEvent
import tools.aqua.bgw.visual.CompoundVisual
import java.awt.Color

class GameScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {

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
        visual = ColorVisual(255, 0, 0)
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
    ).apply{
        onMouseClicked = {
            val game = rootService.currentGame
            checkNotNull(game) { "No game found."}

            println(game.currentGameState.currentPlayer.gems)
        }
    }

    private val takeGemsButton = Button(
        width = 200, height = 100,
        posX = 50, posY = 650,
        text = "Take Gems",
        font = Font(size = 17),
        visual = buttonImage
    ).apply {
        onMouseClicked = {
            val playerActionService = rootService.playerActionService

            val type: GemType = GemType.GREEN
            val gemList: MutableList<GemType> = mutableListOf()
            for(i in 0..gemSelection.size-1) {
                var j  = gemSelection[i]
                while(j>0) {
                    val insert: GemType? = type.gemType(i+1)
                    checkNotNull(insert) { "No gem found. "}
                    gemList.add(insert)
                    j--
                }
            }

            println(gemList)

            try {
                playerActionService.takeGems(gemList)
            }
            catch(e: Exception) {
                println(e)
            }
        }
    }

    private val nextPlayerButton = Button(
        width = 200, height = 100,
        posX = 1680, posY = 930,
        text = "Next Player",
        font = Font(size = 28),
        visual = buttonImage

    )

    //TODO add name of current Player into the currentPlayer label in refreshable Method
    private val currentPlayer = Label(
        posX = width / 2 - 150,
        posY = 50,
        width = 300,
        height = 50,
        text = "TestPlayer",
        font = Font(size = 30),
        visual = buttonImage
    )

    private val devCardMap: BidirectionalMap<DevCard, CardView> = BidirectionalMap()
    private val nobleTileMap: BidirectionalMap<NobleTile, CardView> = BidirectionalMap()

    private val gameLists: MutableList<LinearLayout<CardView>> = mutableListOf()
    private val gameStacks: MutableList<LabeledStackView> = mutableListOf()

    private val playerHands: MutableList<LinearLayout<CardView>> = mutableListOf()

    private val tokemImages: List<ImageVisual> = listOf()

    private val gameGems: MutableList<Label> = mutableListOf()
    private val gameGemsLabel: MutableList<Label> = mutableListOf()

    private val playerGems: MutableList<Label> = mutableListOf()
    private val playerGemsLabel: MutableList<Label> = mutableListOf()


    private val gemSelection: IntArray = intArrayOf(
        0, 0, 0, 0, 0, 0
    )

    override fun refreshAfterTakeGems() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }

        val gems = game.currentGameState.board.gems.entries
        var i: Int = 0
        for(gem in gems) {
            val index = gem.key.toInt()
            gameGemsLabel[index-1].text = gem.value.toString()
            i++
        }
    }

    private fun selectGem(label: Label, index: Int, event: MouseEvent) {

        val old = gemSelection[index]
        if(event.button==MouseButtonType.RIGHT_BUTTON) {
            gemSelection[index]=0
        }
        else {
            gemSelection[index] = (gemSelection[index] + 1) % 3
        }
        if(gemSelection.sum()>3) {
            gemSelection[index] = old
        }

        when(gemSelection[index]) {
            0 -> {
                label.opacity = 0.2
                label.text=""
            }
            1 -> {
                label.opacity = 1.0
                label.text=""
            }
            2 -> {
                label.opacity = 1.0
                label.text ="+"
            }
        }
    }

    override fun refreshAfterStartNewGame() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }

        devCardMap.clear()
        nobleTileMap.clear()
        gameLists.forEach { list -> list.clear() }
        gameLists.clear()

        initializeNobleCardsView(game.currentGameState.board.nobleTiles)

        /*
        initializeDevCardStack(game.currentGameState.board.levelOneCards, 1)
        initializeDevCardStack(game.currentGameState.board.levelTwoCards, 2)
        initializeDevCardStack(game.currentGameState.board.levelThreeCards, 3)

         */

        initializeDevCardList(game.currentGameState.board.levelOneOpen, 1)
        initializeDevCardList(game.currentGameState.board.levelTwoOpen, 2)
        initializeDevCardList(game.currentGameState.board.levelThreeOpen, 3)

        for(i in 0..game.currentGameState.playerList.size-1) {
            val layout: LinearLayout<CardView> = LinearLayout(
                posX = 1380, posY = 100, width = 200, height = 800,
                orientation = Orientation.VERTICAL, alignment = Alignment.BOTTOM_CENTER,
                visual = ColorVisual(221, 136, 136)
            )

            layout.dropAcceptor = { dragEvent ->
                    when(dragEvent.draggedComponent) {
                        is CardView -> {
                            val gameService = rootService.gameService
                            true
                        }
                        else -> false
                    }
            }
            layout.onDragDropped = { dragEvent ->
                val playerActionService = rootService.playerActionService
                val gameService = rootService.gameService

                val draggedDevCard: CardView = dragEvent.draggedComponent as CardView
                println("yo")
            }

            playerHands.add(layout)
        }

        val gemsEntry = game.currentGameState.board.gems.entries
        val gems: MutableList<Pair<GemType, Int>> = mutableListOf()
        for(gem in gemsEntry) gems.add(Pair(gem.key, gem.value))
        gems.sortBy{ it.first.toInt() }
        var i: Int = 0
        for(gem in gems) {
            println(gem)
            var token = Label(
                posX = 1800 - 25, posY = 100+i*75 - 25, width=50, height=50,
                visual = imageLoader.tokenImage(gem.first), font = Font(size = 44)
            ).apply {
                onMouseClicked = {
                    val index = gem.first.toInt()-1
                    selectGem(this, index, it)
                }
            }
            token.opacity = 0.2

            val textLabel = Label(
                posX = 1700 - 50, posY = 100+i*75 - 25, width=100, height=50,
                text = gem.second.toString(), font = Font(size=40, color = Color.WHITE)
            )

            gameGems.add(token)
            gameGemsLabel.add(textLabel)
            i++
        }

        gameLists.forEach { addComponents(it) }
        gameStacks.forEach { addComponents(it) }
        playerHands.forEach{ addComponents(it) }
        gameGems.forEach{ addComponents(it) }
        gameGemsLabel.forEach{ addComponents(it)}

    }

    private fun initializeNobleCardsView(nobleTiles: MutableList<NobleTile>) {
        val layout: LinearLayout<CardView> = LinearLayout(
            posX = width / 2 - 440, posY = 100, width = 880, height = 180, spacing = 30,
            alignment = Alignment.CENTER
        )

        for (i in 0..nobleTiles.size - 1) {
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
        val stackView: LabeledStackView = LabeledStackView(
            posX = width / 2 - 440 - 30, posY = 650 - (level - 1) * 180
        )

        for (i in 0..devCards.size - 1) {
            val cardView = CardView(
                height = 150, width = 95,
                front = imageLoader.frontImageFor(devCards[i].id),
                back = cardBack,
            )
            cardView.showBack()
            devCardMap.add(devCards[i] to cardView)
            stackView.add(cardView)
        }

        gameStacks.add(stackView)
    }

    private fun initializeDevCardList(devCards: MutableList<DevCard>, level: Int) {
        val layout: LinearLayout<CardView> = LinearLayout(
            posX = width / 2 - 330, posY = 650 - (level - 1) * 180, width = 660, height = 180, spacing = 30,
            alignment = Alignment.CENTER
        )

        for (i in 0..devCards.size - 1) {
            val cardView = CardView(
                height = 150, width = 95,
                front = imageLoader.frontImageFor(devCards[i].id),
                back = cardBack,
            )
            cardView.isDraggable = true
            cardView.onDragGestureEnded =
                { _, succes ->
                    if (succes) {
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
            takeGemsButton,
            loadHighscoreButton,
            hintButton,
            saveGameButton,
            currentPlayer,
        )
    }


}
