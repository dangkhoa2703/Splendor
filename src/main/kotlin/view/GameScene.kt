package view

import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import entity.*
import javafx.stage.DirectoryChooser
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.event.DragEvent
import tools.aqua.bgw.components.uicomponents.Orientation
import tools.aqua.bgw.util.BidirectionalMap
import tools.aqua.bgw.components.uicomponents.Label
import java.awt.Color
import tools.aqua.bgw.event.MouseButtonType
import tools.aqua.bgw.event.MouseEvent
import tools.aqua.bgw.components.container.GameComponentContainer
import kotlin.math.max
import kotlin.math.min
import java.io.File

/**
 * [GameScene] : This is the Scene where most of the action happens in Splendor. The scene shows the complete table at
 * once.
 * A player/AI "sits" on the bottom half of the screen, with a display of his development cards, gems (bottom left) and
 * visited noble tiles
 * if there are any.
 * Displayed at the top part of the screen is the name of current Player . Next to this is a function to see what the
 * other players have in hand, to be
 * able to determine a next move.
 * In the middle of the screen are the open devCards, Noble Tiles.To the far right are the Gems available for taking.
 *  These are available for all players depending on the condition
 * (ability to buy / ability to take x amount of Gems / getting a visit from a noble tile).
 * Aside that , there are various player action buttons distributed over the board.
 *  [imageLoader]: used to load various images from the SplendorImageLoader() class, needed for configScene
 *  [buttonImage] : provides facility for buttons needed for the configScene
 *  [cardBack] : back side of the cards
 *  [carbonImage] : Image for cardholders
 *  [allGems] : List of all the Gems used in GameScene
 */
class GameScene(private val rootService: RootService): BoardGameScene(1920,1080), Refreshable  {
    
    private val imageLoader = SplendorImageLoader()
    private val cardBack: ImageVisual = imageLoader.cardBack()
    private val buttonImage: ImageVisual = imageLoader.button()
    private val carbonImage: ImageVisual = imageLoader.carbon()

    private val allGems: List<GemType> = listOf(
	GemType.WHITE, GemType.BLUE, GemType.GREEN, GemType.RED, GemType.BLACK, GemType.YELLOW
    )

    private val devCardMap: BidirectionalMap<Int, CardView> = BidirectionalMap()
    private val devCardsMap: BidirectionalMap<DevCard, CardView> = BidirectionalMap()

    private var saved: List<DevCard> = listOf()

    val hint=Label(
	width/2+300,
	height/2-200,
	400,
	500,
	"", font = Font(size = 26, color = Color.WHITE)
    )

    val errorLabel=Label(
	width/2 - 500,
	1000,
	1000,
	100,
	"", font = Font(size = 20, color = Color.WHITE)
    )

    private val drawStacks: MutableList<LabeledStackView> = mutableListOf()

    //BUTTONS
    /**[nextPlayersButton] : Button to display items the other players have in hand*/
    val nextPlayersButton = Button(
	width = 50, height = 50,
        posX = 1250, posY = 25,
        text = "",
        font = Font(size = 28),
        visual = imageLoader.nextPlayersImage(),
    )

    /**[undoButton] : Button to end undo an action, if there is one to be undone */
    private val undoButton = Button(
        width = 50, height = 50,
        posX = 50, posY = 25,
        text = "",
        font = Font(size = 28),
        visual = imageLoader.undoButton()
    ).apply{
	onMouseClicked = {
	    val playerActionService = rootService.playerActionService
	    try{
		playerActionService.undo()
	    }
	    catch(e: Exception) {
		val errText: String? = e.message
		checkNotNull(errText)
		errorLabel.text = errText
		println(e)
	    }
	}
    }

    /**[redoButton] : Button to end redo an action, if there is one to be redone */
    private val redoButton = Button(
        width = 50, height = 50,
        posX = 200, posY = 25,
        text = "",
        font = Font(size = 28),
        visual = imageLoader.redoButton()
    ).apply {
	onMouseClicked = {
	    val playerActionService = rootService.playerActionService
	    try{
		playerActionService.redo()
	    }
	    catch(e: Exception) {
		val errText: String? = e.message
		checkNotNull(errText)
		errorLabel.text = errText
		println(e)
	    }
	}
    }

	private val KIButton = Button(
		width = 150, height = 50,
		posX = 50, posY = 350,
		text = "KI-TURN",
		font = Font(size = 20),
		visual = buttonImage
	).apply {
		onMouseClicked={
		if(!rootService.currentGame!!.currentGameState.currentPlayer.playerType.equals(PlayerType.HUMAN)){
			val gamestate= rootService.currentGame!!.currentGameState
			val turn = rootService.aiService.calculateBestTurn(gamestate.currentPlayer,gamestate)
			if(turn.turnType.equals(TurnType.BUY_CARD)){
				val playerActionService = rootService.playerActionService
				val devCard: DevCard = turn.card[0]
				val cardView: CardView = devCardMap.forwardOrNull(devCard.id) as CardView

				checkNotNull(currentPlayer) { "No player found."}
				val player = currentPlayer as Player

				val isSaved: Boolean = saved.contains(devCard)


			}
			if(turn.turnType.equals(TurnType.TAKE_GEMS)){
				val playerActionService = rootService.playerActionService

				checkNotNull(currentPlayer) { "No player found. "}
				val player = currentPlayer as Player

				val gemList: MutableList<GemType> = mutableListOf()

				for(gem in turn.gems) {
					var amount = gem.value
					while(amount>0) {
						gemList.add(gem.key)
						amount--
					}
				}

				try {
					playerActionService.takeGems(gemList, player)
					for (gem in turn.gems) {
						gameGemSelection[gem.key] = 0
					}
				}
				catch(e: Exception) {
					val errText: String? = e.message
					checkNotNull(errText)
					errorLabel.text = errText
					println(e)
				}

				for(gem in allGems) {
					gameGemSelection[gem] = 0
				}
				renderGameGems()

			}

			if(turn.turnType.equals(TurnType.RESERVE_CARD)){

			}
			if(turn.turnType.equals(TurnType.TAKE_GEMS_AND_DISCARD)){

			}
		}
		}
	}

    /**[reservedCardsLabel] : Label for reserved cards placeholder */
    private val reservedCardsLabel = Label(
	posX = 280, posY = 200,
	width = 140, height = 100,
	text = "Saved", font = Font(size = 20, fontStyle=Font.FontStyle.ITALIC )
    )

    /**[devCardsLabel] : Label for development cards placeholder */
    private val devCardsLabel = Label(
	posX = 480, posY = 200,
	width = 140, height = 100,
	text = "Dev Cards", font = Font(size = 20, fontStyle=Font.FontStyle.ITALIC )
    )

    /**[hintButton] : Button, when triggered gives a player a hint on what move to make */
    private val hintButton = Button(
        width = 50, height = 50,
        posX = 50, posY = 125,
        text = "",
        font = Font(size = 28),
        visual = imageLoader.hintButton()
    ).apply {
	onMouseClicked = {
	    val playerActionService = rootService.playerActionService
	    val gameState = rootService.currentGame!!.currentGameState
	    val turn = rootService.aiService.calculateBestTurn(gameState.currentPlayer,gameState)
	    var output:String = ""
	    try {
		output = playerActionService.showHint(turn)
	    }
	    catch(e: Exception) {
		val errText: String? = e.message
		checkNotNull(errText)
		errorLabel.text = errText
		println(e)
	    }
	    hint.text = ""
	    val lines = output.chunked(20)
	    var i = lines.size
	    for(line in lines) {
		hint.text+=line
		if(i!=1) hint.text+="\n"
		i--
	    }
	}
    }

    /**[saveGameButton] : Button, when triggered saves current game state */
    private val saveGameButton = Button(
        width = 50, height = 50,
        posX = 200, posY = 125,
        text = "Save Game",
        font = Font(size = 17),
        visual = imageLoader.saveGameImage()
    ).apply{
	onMouseClicked = {
	    val directoryChooser: DirectoryChooser = DirectoryChooser()
	    val file: File? = directoryChooser.showDialog(null)
	    if(file!=null) {
		val ioService = rootService.ioService
		ioService.saveGame(file.absolutePath)
	    }
	}
    }

    /**[quitButton] : Button, when triggered ends current game  */
    val quitButton = Button(
        width = 25, height = 25,
        posX = 0, posY = 0,
        text = "X",
        font = Font(size = 15),
        visual = ColorVisual(255, 0, 0)
    )

    /**[currentPlayerLabel] : Label for the current Player playing  */
    private val currentPlayerLabel = Label(
        posX = width / 2 - 150,
        posY = 25,
        width = 300,
        height = 50,
        text = "TestPlayer",
        font = Font(size = 30),
        visual = buttonImage
    )

    /**[nextPlayersButton] : Button to end one's turn and move to the next player*/
    private val nextPlayerButton = Button(
        width = 200, height = 100,
        posX = 1680, posY = 930,
        text = "Next Player",
        font = Font(size = 28),
        visual = buttonImage
    ).apply{
	onMouseClicked = {
		try {
			rootService.gameService.nextPlayer()
			refreshAfterEndTurn()
		}
		catch(e: Exception) {
			val errText: String? = e.message
			checkNotNull(errText)
			errorLabel.text = errText
			println(e)
		}


	}
    }

    /**[takeGemsButton] : Button ,when triggered enables a player to take selected gems*/
    private val takeGemsButton = Button(
        width = 200, height = 100,
        posX = 1680, posY = 650,
        text = "Take Gems",
        font = Font(size = 17),
        visual = buttonImage
    ).apply {
        onMouseClicked = {
	    
            val playerActionService = rootService.playerActionService

	    checkNotNull(currentPlayer) { "No player found. "}
	    val player = currentPlayer as Player

	    val gemList: MutableList<GemType> = mutableListOf()

	    for(gem in gameGemSelection.entries) {
		var amount = gem.value
		while(amount>0) {
		    gemList.add(gem.key)
		    amount--
		}
	    }

	    try {
		playerActionService.takeGems(gemList, player)
		for (gem in gameGemSelection.entries) {
		    gameGemSelection[gem.key] = 0
		}
	    }
            catch(e: Exception) {
		val errText: String? = e.message
		checkNotNull(errText)
		errorLabel.text = errText
               	println(e)
            }

	    for(gem in allGems) {
		gameGemSelection[gem] = 0
	    }
	    renderGameGems()
        }
    }

    /**[scoreLabel] : Label displaying player score */
    private val scoreLabel = Label(
	width= 100, height = 50,
	posX = 650, posY=25,
	text = "",
	font = Font(size = 40, fontWeight = Font.FontWeight.BOLD)
    )

    /**[discardGems] : Button, when triggered enables discarding of selected Gems */
    private val discardGems = Button(
	width = 100, height = 50,
        posX = 50, posY = 450,
        text = "Discard Gems",
        font = Font(size = 12),
        visual = buttonImage
    ).apply {
	onMouseClicked = {
	    val playerActionService = rootService.playerActionService

	    val gemList: MutableList<GemType> = mutableListOf()

	    for(gem in playerGemSelection.entries) {
		var amount = gem.value
		while(amount>0) {
		    gemList.add(gem.key)
		    amount--
		}
	    }

	    checkNotNull(currentPlayer) { "No Player found. "}
	    try {
		playerActionService.returnGems(gemList, currentPlayer as Player)
	    }
	    catch(e: Exception) {
		val errText: String? = e.message
		checkNotNull(errText)
		errorLabel.text = errText
		println(e)
	    }

	    renderPlayerGems()
	}
    }

    /**[stack] : devCard stack */
    private val stack: LabeledStackView = LabeledStackView(
	posX = 0, posY = 0,  "stack"
    ).apply{
	isVisible = false
    }


    //game Gems
    private val gameGems: MutableList<Label> = mutableListOf()
    private val gameGemsInfo: MutableList<Label> = mutableListOf()
    private val gameGemsSelected: MutableList<Label> = mutableListOf()
    
    private val gameGemSelection: MutableMap<GemType, Int> = mutableMapOf()
    private val gameGemMax: MutableMap<GemType, Int> = mutableMapOf()

    //player Gems
    private val playerGems: MutableList<Label> = mutableListOf()
    private val playerGemsInfo: MutableList<Label> = mutableListOf()
    private val playerGemsSelected: MutableList<Label> = mutableListOf()
    
    private val playerGemSelection: MutableMap<GemType, Int> = mutableMapOf()
    private val playerGemMax: MutableMap<GemType, Int> = mutableMapOf()


    /**[nobleTiles] : Visual Layout for the noble Tiles*/
    private val nobleTiles: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 330, posY = 100, width = 660, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(136, 221, 221)
    )

    /**[levelOneCards] : Visual Layout for the level One Cards*/
    private val levelOneCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 650, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(221, 136, 136),
    )

    /**[levelTwoCards] : Visual Layout for level two cards*/
    private val levelTwoCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 470, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(221, 136, 136)
    )

    /**[levelThreeCards] : Visual Layout for level three cards*/
    private val levelThreeCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 290, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(221, 136, 136)	
    )

    /**[playerDevCards] : Mutable Linear Layout list of player DevCards.
     [playerSaveCards] : Mutable Linear Layout list of player reserved cards.
     [playerNobleTiles] : Mutable Linear Layout list of player noble tiles.* */

    var playerDevCards: MutableList<LinearLayout<CardView>> = mutableListOf()
    var playerSaveCards: MutableList<LinearLayout<CardView>> = mutableListOf()
    var playerNobleTiles: MutableList<LinearLayout<CardView>> = mutableListOf()


    /**
     * [toCardView] : Method to show front of the card
     * @param id : id of the card
     * @param draggable : Boolean to check if the card can be dragged
     */
    private fun toCardView(id: Int, draggable: Boolean = false): CardView {
	val cardView = CardView(
	    height = 150, width = 95,
	    front = imageLoader.frontImageFor(id),
	    back = cardBack,
	)
	if(draggable) {
	    cardView.isDraggable = true
	}
	cardView.showFront()
	return cardView
    }

    /**
     * [tryToSelect] : Method to enable selection of game Items like Noble tiles
     */
    private fun tryToSelect(dragEvent: DragEvent): Boolean {
	val cardView = dragEvent.draggedComponent as CardView

	val id = devCardMap.backward(cardView)
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. " }

	var nobleTile: NobleTile? = null
	for (tile in game.currentGameState.board.nobleTiles) {
	    if (tile.id == id) nobleTile = tile
	}

	if (nobleTile == null) return false

	checkNotNull(currentPlayer) { "No Player found. " }

	val playerActionService = rootService.playerActionService
	val player = currentPlayer as Player

	try {
	    playerActionService . selectNobleTile (nobleTile, player)
	}catch (e:Exception){
	    val errText: String? = e.message
	    checkNotNull(errText)
	    errorLabel.text = errText
	    println(e)
	    return false
	}
	return true
    }

    /**[tryToBuy] : Method, giving a player the chance to purchase a devCard */
    private fun tryToBuy(dragEvent: DragEvent): Boolean {
	val playerActionService = rootService.playerActionService
	val cardView: CardView = dragEvent.draggedComponent as CardView
	val devCard: DevCard = devCardsMap.backwardOrNull(cardView) ?: return false

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	val isSaved: Boolean = saved.contains(devCard)
	
	try{
	    playerActionService.buyCard(devCard, !isSaved, playerGemSelection, player)
	    if(isSaved) saved = saved - devCard
	    return true
	}
	catch(e: Exception) {
	    val errText: String? = e.message
	    checkNotNull(errText)
	    errorLabel.text = errText
	    println(e)
	}

	return false
    }

    /**[tryToBuy] : Method, giving a player the chance to reserve a devCard */
    private fun tryToSave(dragEvent: DragEvent): Boolean {
	val playerActionService = rootService.playerActionService
	val cardView: CardView = dragEvent.draggedComponent as CardView
	val devCard: DevCard = devCardsMap.backwardOrNull(cardView) ?: return false

	if(saved.contains(devCard)) return false

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	try {
	    playerActionService.reserveCard(devCard, player)
	    return true
	}
	catch(e: Exception) {
	    val errText: String? = e.message
	    checkNotNull(errText)
	    errorLabel.text = errText
	    println(e)
	}
	return false
    }

    /**[moveCardView] : Method to switch a card view */
    private fun moveCardView(cardView: CardView, targetContainer: GameComponentContainer<CardView>,
			     flip: Boolean = false) {
	if (flip) {
	    when (cardView.currentSide) {
	        CardView.CardSide.BACK -> cardView.showFront()
	       	CardView.CardSide.FRONT -> cardView.showBack()
	    }
	}
	if(cardView.parent!=null) cardView.removeFromParent()
	if(!targetContainer.contains(cardView))targetContainer.add(cardView)
    }

    var currentPlayer: Player? = null
    private var currentPlayerIndex: Int = -1

    /**[fillNobleTilesLayout] : Method, filling the nobleTiles layout on the table */
    private fun fillNobleTilesLayout(
	layout: LinearLayout<CardView>,
	source: MutableList<NobleTile>,
	draggable: Boolean = true,
    ) {
	val sourceIndexed = source.map{ it.id }
	val temp: MutableList<Int> = mutableListOf()

	layout.filter{ !sourceIndexed.contains(devCardMap.backward(it))}.forEachIndexed{
	    index, element -> run {
		moveCardView(element, stack)
		temp.add(index)
	    }
	}

	for(card in source) {
	    if(temp.contains(card.id)) continue
	    val cardView: CardView = devCardMap.forward(card.id)
	    cardView.isDraggable = draggable
	    moveCardView(cardView, layout)
	}
    }

    /**[fillDevCardLayout] : Method, filling the dev card layout on the table */
    private fun fillDevCardLayout(
	layout: LinearLayout<CardView>,
	source: MutableList<DevCard>,
	draggable: Boolean = true
    ) {
	//val sourceIndexed = toIdListDevCard(source)
	val sourceIndexed = source.map{ it.id }
	val temp: MutableList<Int> = mutableListOf()

	layout.filter{ !sourceIndexed.contains(devCardMap.backward(it))}.forEachIndexed{
	    index, element -> run {
		moveCardView(element, stack)
		temp.add(index)
	    }
	}

	for(card in source) {
	    if(temp.contains(card.id)) continue
	    val cardView: CardView = devCardMap.forward(card.id)
	    cardView.isDraggable = draggable
	    devCardsMap.add(card to cardView)
	    moveCardView(cardView, layout)
	}
    }

    /**[fillLayouts] : Method, filling all layouts on the table */
    private fun fillLayouts() {
	errorLabel.text = ""
	hint.text = ""
	devCardsMap.clear()


	var temp: List<CardView> = listOf()
	drawStacks.forEach{
	    it.forEach{
		cardView -> temp+=cardView
	    }
	}

	temp.forEach{ moveCardView(it, stack)}
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found."}

	val nobleTilesOrigin = game.currentGameState.board.nobleTiles
	
	val levelOneCardsOrigin = game.currentGameState.board.levelOneOpen
	val levelTwoCardsOrigin = game.currentGameState.board.levelTwoOpen
	val levelThreeCardsOrigin = game.currentGameState.board.levelThreeOpen
	
	fillNobleTilesLayout(nobleTiles, nobleTilesOrigin)

	fillDevCardLayout(levelOneCards, levelOneCardsOrigin)
	fillDevCardLayout(levelTwoCards, levelTwoCardsOrigin)
	fillDevCardLayout(levelThreeCards, levelThreeCardsOrigin)

	val playerList = game.currentGameState.playerList
	for(i in playerList.indices) {
	    val player = playerList[i]
	    fillDevCardLayout(playerDevCards[i], player.devCards,false)
	    fillDevCardLayout(playerSaveCards[i], player.reservedCards)
	    fillNobleTilesLayout(playerNobleTiles[i], player.nobleTiles, false)
	}

	for(gem in allGems) {
	    playerGemSelection[gem] = 0
	    gameGemSelection[gem] = 0
	}

	val levelOneStackSize = Math.min(game.currentGameState.board.levelOneCards.size, 2)
	val levelTwoStackSize = Math.min(game.currentGameState.board.levelTwoCards.size, 2)
	val levelThreeStackSize = Math.min(game.currentGameState.board.levelThreeCards.size, 2)

	for(i in 0..levelOneStackSize-1) {
	    val cardView: CardView? = devCardMap.forward(200+i)
	    checkNotNull(cardView)
	    cardView.isDraggable = true
	    moveCardView(cardView, drawStacks[2])
	}

	for(i in 0..levelTwoStackSize-1) {
	    val cardView: CardView? = devCardMap.forward(202+i)
	    checkNotNull(cardView)
	    cardView.isDraggable = true
	    moveCardView(cardView, drawStacks[1])
	}

	for(i in 0..levelThreeStackSize-1) {
	    val cardView: CardView? = devCardMap.forward(204+i)
	    checkNotNull(cardView)
	    cardView.isDraggable = true
	    moveCardView(cardView, drawStacks[0])
	}
    }

    /** [initializePlayerGems] : Method,visually initializing Gems selected from a player */
    private fun initializePlayerGems() {
	playerGems.clear()
	playerGemsInfo.clear()
	playerGemsSelected.clear()
	
	for(gem in allGems) {
	    val icon = Label(
		posX = 100.0 - 25, width=50.0, height=50.0,
                visual = imageLoader.tokenImage(gem),
	    ).apply{
		onMouseClicked = { event ->
				       selectPlayerGem(gem, event)
				   renderPlayerGems()
		}
	    }

	    val infoLabel = Label(
		posX = 100.0 + 25, width=50.0, height=50.0,
                text = "", font = Font(size = 40, color = Color.BLACK)
	    )

	    val selectLabel = Label(
		posX = 100.0 - 75.0, width=50.0, height=50.0,
                text = "", font = Font(size = 40, color = Color.BLACK)
	    )

	    playerGems.add(icon)
	    playerGemsInfo.add(infoLabel)
	    playerGemsSelected.add(selectLabel)
	}

	playerGems.forEach{ addComponents(it) }
	playerGemsInfo.forEach{ addComponents(it) }
	playerGemsSelected.forEach{ addComponents(it) }
    }

    /**[selectPlayerGem] : Method,to enable selection of a player Gem for discarding them or
     * purchasing a dev card */
    private fun selectPlayerGem(gem: GemType, event: MouseEvent) {
	
	val current: Int? = playerGemSelection[gem]
	val max: Int? = playerGemMax[gem]
	checkNotNull(max) { "No max value found for gem."}
	checkNotNull(current) { "No value found for gem."}
	if(event.button==MouseButtonType.RIGHT_BUTTON) {
	    playerGemSelection[gem] = max(0, current-1)
	}
	else {
	    playerGemSelection[gem] = min(max, current + 1)
	}
    }

    /**[selectGameGem] : Method,to enable selection of a table Gem during a player turn */
    private fun selectGameGem(gem: GemType, event: MouseEvent) {
	
	val current: Int? = gameGemSelection[gem]
	val max: Int? = gameGemMax[gem]
	checkNotNull(max) { "No max value found for gem."}
	checkNotNull(current) { "No value found for gem."}
	if(event.button==MouseButtonType.RIGHT_BUTTON) {
	    gameGemSelection[gem] = max(0, current-1)
	}
	else {
	    gameGemSelection[gem] = min(max, current+1)
	}
    }

    /**[initializeGameGems] : Method,visually initializing Game Gems */
    private fun initializeGameGems() {
	gameGems.clear()
	gameGemsInfo.clear()
	gameGemsSelected.clear()
	
	for(gem in allGems) {
	    val icon = Label(
		posX = 1800 - 25.0, width=50.0, height=50.0,
                visual = imageLoader.tokenImage(gem)
	    ).apply{
		onMouseClicked = { event ->
				       selectGameGem(gem, event)
				   renderGameGems()
		}
	    }

	    val infoLabel = Label(
		posX = 1800 + 25.0, width=50.0, height=50.0,
                text = "", font = Font(size = 40, color = Color.BLACK)
	    )

	    val selectLabel = Label(
		posX = 1800 - 75.0, width=50.0, height=50.0,
                text = "", font = Font(size = 40, color = Color.BLACK)
	    )

	    gameGems.add(icon)
	    gameGemsInfo.add(infoLabel)
	    gameGemsSelected.add(selectLabel)
	}

	gameGems.forEach{ addComponents(it) }
	gameGemsInfo.forEach{ addComponents(it) }
	gameGemsSelected.forEach{ addComponents(it) }
    }

    /**[renderGameGems] : Method,visually rendering Game Gems */
    private fun renderGameGems() {
	var j = 0
	for(gem in gameGemSelection.entries) {
	    val i = gem.key.toInt()-1
	    if(gameGemMax[gem.key] ==0) {
		gameGems[i].isVisible = false
		gameGemsInfo[i].isVisible = false
		gameGemsSelected[i].isVisible = false
	    }
	    else {
		gameGems[i].isVisible = true
		gameGemsInfo[i].isVisible = true
		gameGemsSelected[i].isVisible = true
		
		val y = 100.0+j*75.0 - 25.0
		gameGems[i].posY = y
		gameGemsInfo[i].posY = y
		gameGemsSelected[i].posY = y
		

		if(gem.value==0) {
		    gameGemsSelected[i].text = ""
		}
		else {
		    gameGemsSelected[i].text = gem.value.toString()
		}
		gameGemsInfo[i].text = gameGemMax[gem.key].toString()
		j++
	    }
	}
    }

    /**[renderPlayerGems] : Method,visually rendering Player Gems */
    private fun renderPlayerGems() {
	var j = 0
	for(gem in playerGemSelection.entries) {
	    val max: Int? = playerGemMax[gem.key]
	    checkNotNull(max) { "No max found for gem."}
	    val i = gem.key.toInt()-1
	    if(max==0) {
		playerGems[i].isVisible = false
		playerGemsInfo[i].isVisible = false
		playerGemsSelected[i].isVisible = false
	    }
	    else {
		playerGems[i].isVisible = true
		playerGemsInfo[i].isVisible = true
		playerGemsSelected[i].isVisible = true
		
		val y = 850.0-j*50.0 - 25.0
		playerGems[i].posY = y
		playerGemsInfo[i].posY = y
		playerGemsSelected[i].posY = y

		if(gem.value==0) {
		    playerGemsSelected[i].text = ""
		}
		else {
		    playerGemsSelected[i].text = gem.value.toString()
		}
		playerGemsInfo[i].text = max.toString()
		j++
	    }
	}
    }

    /**[refreshAfterTakeGems] : Override Method,refreshing the game scene after a player
     * takes gems */
    override fun refreshAfterTakeGems() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	for(gem in game.currentGameState.board.gems.entries) {
	    gameGemMax[gem.key] = gem.value
	}

	for(gem in player.gems.entries) {
	    playerGemMax[gem.key] = gem.value
	}

	renderGameGems()
	renderPlayerGems()
    }

    /**[refreshAfterSelectNobleTile] : Override Method,refreshing the game scene after
     * noble tiles selection */
    override fun refreshAfterSelectNobleTile(nobleTile: NobleTile) {
	val cardView: CardView = devCardMap.forward(nobleTile.id)
	cardView.isDraggable = false
	
	fillLayouts()

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player
	scoreLabel.text = (player.score).toString()
    }

    /**[refreshAfterEndTurn] : Override Method,refreshing after end of a turn */
    override fun refreshAfterEndTurn() {
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	//	game.turnCount++

	currentPlayer = game.currentGameState.currentPlayer
	currentPlayerIndex = game.currentGameState.playerList.indexOf(currentPlayer)
	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	currentPlayerLabel.text = player.name
	scoreLabel.text = player.score.toString()

	fillLayouts()

	for(i in 0 until playerDevCards.size) {
	    playerDevCards[i].isVisible = false
	    playerSaveCards[i].isVisible = false
	    playerNobleTiles[i].isVisible = false
	}
	
	playerDevCards[currentPlayerIndex].isVisible = true
	playerSaveCards[currentPlayerIndex].isVisible = true
	playerNobleTiles[currentPlayerIndex].isVisible = true
	

	for(gem in game.currentGameState.board.gems.entries) {
	    gameGemMax[gem.key] = gem.value
	}

	for(gem in player.gems.entries) {
	    playerGemMax[gem.key] = gem.value
	}

	renderGameGems()
	renderPlayerGems()
    }

    /**[loadAllComponents] : Method to load all view components to GameScene */
    fun loadAllComponents() {
	clearComponents()

	for(i in 0..2) {
	    val drawStack = LabeledStackView(
		posX = width/2 + 240, posY = 305+i*180,
		label = "",
	    )

	    val cardView1 = CardView(
		width = 95, height = 150,
		back = imageLoader.cardBack(),
		front = ColorVisual(0, 0, 0)
	    )

	    val cardView2 = CardView(
		width = 95, height = 150,
		back = imageLoader.cardBack(),
		front = ColorVisual(0, 0, 0)
	    )

	    devCardMap.add((200+i*2+0) to cardView1)
	    devCardMap.add((200+i*2+1) to cardView2)
	    
	    drawStacks.add(drawStack)
	}

	drawStacks.forEach{ addComponents(it)}
	
	for(i in 0..3) {
	    val devCards: LinearLayout<CardView> = LinearLayout(
		posX = 480, posY = 300, width = 140, height = 600,
                orientation = Orientation.VERTICAL, alignment = Alignment.BOTTOM_CENTER,
		visual = carbonImage,
                //visual = ColorVisual(221, 136, 136),
		spacing = -110
	    )

	    val saveCards: LinearLayout<CardView> = LinearLayout(
		posX = 280, posY = 300, width = 140, height = 600,
                orientation = Orientation.VERTICAL, alignment = Alignment.BOTTOM_CENTER,
		visual = carbonImage,
                //visual = ColorVisual(221, 136, 221),
		spacing = 10
	    )

	    val tiles: LinearLayout<CardView> = LinearLayout(
		posX = width/2 - 300, posY = 870,
		width = 600, height = 140,
		visual = carbonImage,
		spacing = 30,
		orientation = Orientation.HORIZONTAL, alignment = Alignment.CENTER
	    )

	    

	    devCards.isVisible = false
	    devCards.dropAcceptor = {
		dragEvent -> tryToBuy(dragEvent)
	    }
	    
	    saveCards.isVisible = false
	    saveCards.dropAcceptor= {
		dragEvent -> tryToSave(dragEvent)
	    }

	    tiles.isVisible = false
	    tiles.dropAcceptor = {
		dragEvent -> tryToSelect(dragEvent)
	    }

	    playerDevCards.add(devCards)
	    playerSaveCards.add(saveCards)
	    playerNobleTiles.add(tiles)
	}

	playerDevCards.forEach{ addComponents(it) }
	playerSaveCards.forEach{ addComponents(it) }
	playerNobleTiles.forEach{ addComponents(it)}
	
	imageLoader.preload()
	
	for(i in 0..99) {
	    val cardView = toCardView(i)
	    stack.add(cardView)
	    devCardMap.add(i to cardView)
	}

	

	initializePlayerGems()
	initializeGameGems()
    }

    /**[refreshAfterReserveCard] : Override Method,refreshing after reserving a card */
    override fun refreshAfterReserveCard(devCard: DevCard) {
	val game = rootService.currentGame
	checkNotNull(game) { "No game  found." }

	saved = saved + devCard
	//moveCardView(cardView, playerSaveCards[currentPlayerIndex])
	
	fillLayouts()

	refreshAfterTakeGems()
    }

    /**[refreshAfterBuyCard] : Override Method,refreshing after buying a card */
    override fun refreshAfterBuyCard(devCard: DevCard) {
	val cardView: CardView = devCardsMap.forward(devCard)
	
	checkNotNull(currentPlayer) { "No player found. "}

	val player = currentPlayer as Player
	cardView.isDraggable = false
	//moveCardView(cardView, playerDevCards[currentPlayerIndex])

	scoreLabel.text = (player.score).toString()
	
	fillLayouts()

	refreshAfterTakeGems()
    }

    override fun refreshAfterTakeGems(gems: Map<GemType, Int>) {

	for(gem in allGems) {
	    playerGemSelection[gem]=0
	    gameGemSelection[gem] = 0
	}

	for(gem in gems) {
	    playerGemMax[gem.key]=gem.value
	}

	renderPlayerGems()

    }
	/**
	 * Checks if current Player is AI(if yes it returns true)
	 */
	fun AIcheck(): Boolean{
		return !rootService.currentGame!!.currentGameState.currentPlayer.playerType.equals(PlayerType.HUMAN)
	}

	fun AITurn(){

	}


	init {
	loadAllComponents()


	background = imageLoader.table()

	addComponents(
	    quitButton,
	    nobleTiles,
	    levelOneCards,
	    levelTwoCards,
	    levelThreeCards,
	    currentPlayerLabel,
	    takeGemsButton,
	    nextPlayerButton,
	    stack,
	    discardGems,
	    saveGameButton,
	    hintButton,
	    redoButton,
	    undoButton,
	    nextPlayersButton,
	    scoreLabel,
	    reservedCardsLabel,
	    devCardsLabel,
	    hint,
	    errorLabel,
		KIButton
	)
    }
}
