package view

import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import entity.*
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

    //BUTTONs

    val nextPlayersButton = Button(
	width = 50, height = 50,
        posX = 1250, posY = 25,
        text = "",
        font = Font(size = 28),
        visual = imageLoader.nextPlayersImage(),
    )

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
		println(e)
	    }
	}
    }

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
		println(e)
	    }
	}
    }

    private val reservedCardsLabel = Label(
	posX = 280, posY = 200,
	width = 140, height = 100,
	text = "Saved", font = Font(size = 20, fontStyle=Font.FontStyle.ITALIC )
    )

    private val devCardsLabel = Label(
	posX = 480, posY = 200,
	width = 140, height = 100,
	text = "Dev Cards", font = Font(size = 20, fontStyle=Font.FontStyle.ITALIC )
    )

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
//	funktioniert erst, wenn calculateBestTurn nicht mehr null zurückgibt
//		val turn = rootService.aiService.calculateBestTurn(gameState.currentPlayer,gameState)
//	    val output = playerActionService.showHint(turn!!)
//	    println(output)
	}
    }

    private val saveGameButton = Button(
        width = 50, height = 50,
        posX = 200, posY = 125,
        text = "Save Game",
        font = Font(size = 17),
        visual = imageLoader.saveGameImage()
    )
    
    val quitButton = Button(
        width = 25, height = 25,
        posX = 0, posY = 0,
        text = "X",
        font = Font(size = 15),
        visual = ColorVisual(255, 0, 0)
    )

    private val currentPlayerLabel = Label(
        posX = width / 2 - 150,
        posY = 25,
        width = 300,
        height = 50,
        text = "TestPlayer",
        font = Font(size = 30),
        visual = buttonImage
    )

    private val nextPlayerButton = Button(
        width = 200, height = 100,
        posX = 1680, posY = 930,
        text = "Next Player",
        font = Font(size = 28),
        visual = buttonImage
    ).apply{
	onMouseClicked = {
	    refreshAfterEndTurn()
	}
    }

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
		for(gem in gameGemSelection.entries) {
		    gameGemSelection.put(gem.key,0)
		}
		renderGameGems()
            }
            catch(e: Exception) {
                println(e)
            }
        }
    }

    private val scoreLabel = Label(
	width= 100, height = 50,
	posX = 650, posY=25,
	text = "",
	font = Font(size = 40, fontWeight = Font.FontWeight.BOLD)
    )

    private val discardGems = Button(
	width = 100, height = 50,
        posX = 50, posY = 450,
        text = "Discard Gems",
        font = Font(size = 12),
        visual = buttonImage
    )

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
    

    private val nobleTiles: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 330, posY = 100, width = 660, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(136, 221, 221)
    )
    
    private val levelOneCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 650, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(221, 136, 136),
    )
    private val levelTwoCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 470, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(221, 136, 136)
    )
    private val levelThreeCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 290, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER,
	//visual = ColorVisual(221, 136, 136)	
    )

    var playerDevCards: MutableList<LinearLayout<CardView>> = mutableListOf()
    var playerSaveCards: MutableList<LinearLayout<CardView>> = mutableListOf()
    var playerNobleTiles: MutableList<LinearLayout<CardView>> = mutableListOf()

    private fun toCardView(id: Int, draggable: Boolean = false): CardView {
	val cardView: CardView = CardView(
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
    
    private fun getCard(cardView: CardView): DevCard?{
	val number=devCardMap.backward(cardView)
	var returnCard : DevCard? = null
	if(1<=number&&number<=40){
	    for(card in rootService.currentGame!!.currentGameState.board.levelOneOpen){
		if(card.id==number){
		    returnCard=card
		}
	    }
	}
	if(41<=number&&number<=70){
	    for(card in rootService.currentGame!!.currentGameState.board.levelTwoOpen){
		if(card.id==number){
		    returnCard=card
		}
	    }
	}

	if(71<=number&&number<=90){
	    for(card in rootService.currentGame!!.currentGameState.board.levelThreeOpen){
		if(card.id==number){
		    returnCard=card
		}
	    }
	}

	return returnCard
    }

    private fun tryToSelect(dragEvent: DragEvent): Boolean {
	var cardView = dragEvent.draggedComponent as CardView

	var id = devCardMap.backward(cardView)
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	var nobleTile: NobleTile? = null
	for(tile in game.currentGameState.board.nobleTiles) {
	    if(tile.id == id) nobleTile = tile
	}

	if(nobleTile==null) return false
	
	checkNotNull(currentPlayer) { "No Player found. "}

	val playerActionService = rootService.playerActionService
	val player = currentPlayer as Player
	
	playerActionService.selectNobleTile(nobleTile, player)
	return true
    }

    private fun tryToBuy(dragEvent: DragEvent): Boolean {
	val playerActionService = rootService.playerActionService
	val cardView: CardView = dragEvent.draggedComponent as CardView
	val devCard: DevCard? = devCardsMap.backwardOrNull(cardView)

	if(devCard==null) return false

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	val isSaved: Boolean = saved.contains(devCard)
	
	try{
	    playerActionService.buyCard(devCard, !isSaved, playerGemSelection, 0, player)
	    if(isSaved) saved-=devCard
	    return true
	}
	catch(e: Exception) {
	    println(e)
	}
	

	return false
    }
    
    private fun tryToSave(dragEvent: DragEvent): Boolean {
	val playerActionService = rootService.playerActionService
	val cardView: CardView = dragEvent.draggedComponent as CardView
	val devCard: DevCard? = devCardsMap.backwardOrNull(cardView)

	if(devCard==null) return false

	if(saved.contains(devCard)) return false

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	try {
	    playerActionService.reserveCard(devCard, 0, player)
	    return true
	}
	catch(e: Exception) {
	    println(e)
	}
	return false
    }

    private fun moveCardView(cardView: CardView, targetContainer: GameComponentContainer<CardView>, flip: Boolean = false) {
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

    private fun fillNobleTilesLayout(
	layout: LinearLayout<CardView>,
	source: MutableList<NobleTile>,
	draggable: Boolean = true,
    ) {
	val sourceIndexed = source.map{ it -> it.id }
	val temp: MutableList<Int> = mutableListOf()

	layout.filter{ !sourceIndexed.contains(devCardMap.backward(it))}.forEachIndexed{
	    index, element -> run {
		moveCardView(element, stack)
		temp.add(index)
	    }
	}

	for(card in source) {
	    if(temp.contains(card.id)) continue
	    val cardView: CardView? = devCardMap.forward(card.id)
	    checkNotNull(cardView) { "No cardView found. "}
	    cardView.isDraggable = draggable
	    moveCardView(cardView, layout)
	}
    }

    private fun fillDevCardLayout(
	layout: LinearLayout<CardView>,
	source: MutableList<DevCard>,
	draggable: Boolean = true
    ) {
	//val sourceIndexed = toIdListDevCard(source)
	val sourceIndexed = source.map{ it -> it.id }
	val temp: MutableList<Int> = mutableListOf()

	layout.filter{ !sourceIndexed.contains(devCardMap.backward(it))}.forEachIndexed{
	    index, element -> run {
		moveCardView(element, stack)
		temp.add(index)
	    }
	}

	for(card in source) {
	    if(temp.contains(card.id)) continue
	    val cardView: CardView? = devCardMap.forward(card.id)
	    checkNotNull(cardView) { "No cardView found. "}
	    cardView.isDraggable = draggable
	    devCardsMap.add(card to cardView)
	    moveCardView(cardView, layout)
	}
    }
    
    private fun fillLayouts() {
	devCardsMap.clear()

	val temp: MutableList<Int> = mutableListOf()
	
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
	for(i in 0..playerList.size-1) {
	    val player = playerList[i]
	    fillDevCardLayout(playerDevCards[i], player.devCards)
	    fillDevCardLayout(playerSaveCards[i], player.reservedCards)
	    fillNobleTilesLayout(playerNobleTiles[i], player.nobleTiles, false)
	}

	for(gem in allGems) {
	    playerGemSelection.put(gem, 0)
	    gameGemSelection.put(gem, 0)
	}
    }

    private fun initializePlayerGems() {
	playerGems.clear()
	playerGemsInfo.clear()
	playerGemsSelected.clear()
	
	for(gem in allGems) {
	    var icon = Label(
		posX = 100.0 - 12.5, width=25.0, height=25.0,
                visual = imageLoader.tokenImage(gem),
	    ).apply{
		onMouseClicked = { event ->
				       selectPlayerGem(gem, event)
				   renderPlayerGems()
		}
	    }

	    var infoLabel = Label(
		posX = 100.0 + 12.5, width=25.0, height=25.0,
                text = "", font = Font(size = 20, color = Color.BLACK)
	    )

	    var selectLabel = Label(
		posX = 100.0 - 50.0, width=25.0, height=25.0,
                text = "", font = Font(size = 20, color = Color.BLACK)
	    )

	    playerGems.add(icon)
	    playerGemsInfo.add(infoLabel)
	    playerGemsSelected.add(selectLabel)
	}

	playerGems.forEach{ addComponents(it) }
	playerGemsInfo.forEach{ addComponents(it) }
	playerGemsSelected.forEach{ addComponents(it) }
    }

    private fun selectPlayerGem(gem: GemType, event: MouseEvent) {
	
	val current: Int? = playerGemSelection.get(gem)
	val max: Int? = playerGemMax.get(gem)
	checkNotNull(max) { "No max value found for gem."}
	checkNotNull(current) { "No value found for gem."}
	if(event.button==MouseButtonType.RIGHT_BUTTON) {
	    playerGemSelection.put(
		gem,
		Math.max(0, current-1)		       
	    )
	}
	else {
	    playerGemSelection.put(
		gem,
		Math.min(max, current+1)		       
	    )
	}
    }

    private fun selectGameGem(gem: GemType, event: MouseEvent) {
	
	val current: Int? = gameGemSelection.get(gem)
	val max: Int? = gameGemMax.get(gem)
	checkNotNull(max) { "No max value found for gem."}
	checkNotNull(current) { "No value found for gem."}
	if(event.button==MouseButtonType.RIGHT_BUTTON) {
	    gameGemSelection.put(
		gem,
		Math.max(0, current-1)		       
	    )
	}
	else {
	    gameGemSelection.put(
		gem,
		Math.min(max, current+1)		       
	    )
	}
    }

    private fun initializeGameGems() {
	gameGems.clear()
	gameGemsInfo.clear()
	gameGemsSelected.clear()
	
	for(gem in allGems) {
	    var icon = Label(
		posX = 1800 - 25.0, width=50.0, height=50.0,
                visual = imageLoader.tokenImage(gem)
	    ).apply{
		onMouseClicked = { event ->
				       selectGameGem(gem, event)
				   renderGameGems()
		}
	    }

	    var infoLabel = Label(
		posX = 1800 + 25.0, width=50.0, height=50.0,
                text = "", font = Font(size = 40, color = Color.BLACK)
	    )

	    var selectLabel = Label(
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

    private fun renderGameGems() {
	var j = 0
	for(gem in gameGemSelection.entries) {
	    val i = gem.key.toInt()-1
	    if(gameGemMax.get(gem.key)==0) {
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
		gameGemsInfo[i].text = gameGemMax.get(gem.key).toString()
		j++
	    }
	}
    }

    private fun renderPlayerGems() {
	var j = 0
	for(gem in playerGemSelection.entries) {
	    val max: Int? = playerGemMax.get(gem.key)
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

    override fun refreshAfterTakeGems() {
        val game = rootService.currentGame
        checkNotNull(game) { "No game found." }

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	for(gem in game.currentGameState.board.gems.entries) {
	    gameGemMax.put(gem.key, gem.value)
	}


	for(gem in player.gems.entries) {
	    playerGemMax.put(gem.key, gem.value)
	}

	renderGameGems()
	renderPlayerGems()
    }
    
    override fun refreshAfterSelectNobleTile(nobleTile: NobleTile) {
	val cardView: CardView? = devCardMap.forward(nobleTile.id)
	checkNotNull(cardView) { "No cardView found."}
	cardView.isDraggable = false
	
	fillLayouts()

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	scoreLabel.text = (player.score+nobleTile.prestigePoints).toString()
    }

    override fun refreshAfterEndTurn() {
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	currentPlayer = game.currentGameState.currentPlayer
	currentPlayerIndex = game.currentGameState.playerList.indexOf(currentPlayer)
	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	currentPlayerLabel.text = player.name
	scoreLabel.text = player.score.toString()

	fillLayouts()

	for(i in 0..playerDevCards.size-1) {
	    playerDevCards[i].isVisible = false
	    playerSaveCards[i].isVisible = false
	    playerNobleTiles[i].isVisible = false
	}
	
	playerDevCards[currentPlayerIndex].isVisible = true
	playerSaveCards[currentPlayerIndex].isVisible = true
	playerNobleTiles[currentPlayerIndex].isVisible = true
	

	for(gem in game.currentGameState.board.gems.entries) {
	    gameGemMax.put(gem.key, gem.value)
	}

	for(gem in player.gems.entries) {
	    playerGemMax.put(gem.key, gem.value)
	}

	renderGameGems()
	renderPlayerGems()
    }

    fun loadAllComponents() {
	clearComponents()
	
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

    override fun refreshAfterReserveCard(devCard: DevCard) {
	val cardView: CardView? = devCardsMap.forward(devCard)
	val game = rootService.currentGame
	checkNotNull(game) { "No game  found." }
	checkNotNull(cardView) { "No card found."}
	
	saved+=devCard
	//moveCardView(cardView, playerSaveCards[currentPlayerIndex])
	
	fillLayouts()

	refreshAfterTakeGems()
    }

    override fun refreshAfterBuyCard(devCard: DevCard) {
	val cardView: CardView? = devCardsMap.forward(devCard)
	checkNotNull(cardView) { "No card found."}
	
	checkNotNull(currentPlayer) { "No player found. "}

	val player = currentPlayer as Player
	cardView.isDraggable = false
	//moveCardView(cardView, playerDevCards[currentPlayerIndex])

	scoreLabel.text = (player.score+devCard.prestigePoints).toString()
	
	fillLayouts()

	refreshAfterTakeGems()
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
	)
    }
}
