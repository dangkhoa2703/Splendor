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

class GameScene(private val rootService: RootService): BoardGameScene(1920,1080), Refreshable  {

    private val imageLoader = SplendorImageLoader()
    private val cardBack: ImageVisual = imageLoader.cardBack()
    private val buttonImage: ImageVisual = imageLoader.button()

    private val allGems: List<GemType> = listOf(
	GemType.WHITE, GemType.BLUE, GemType.GREEN, GemType.RED, GemType.BLACK, GemType.YELLOW
    )

    private val devCardMap: BidirectionalMap<DevCard, CardView> = BidirectionalMap()

    //BUTTONs
   
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
        alignment = Alignment.CENTER, visual = ColorVisual(136, 221, 221)
    )
    
    private val levelOneCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 650, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER, visual = ColorVisual(221, 136, 136),
    )
    private val levelTwoCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 470, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER, visual = ColorVisual(221, 136, 136)
    )
    private val levelThreeCards: LinearLayout<CardView> = LinearLayout(
	posX = width / 2 - 220, posY = 290, width = 440, height = 180, spacing = 30,
        alignment = Alignment.CENTER, visual = ColorVisual(221, 136, 136)	
    )

    private var playerDevCards: MutableList<LinearLayout<CardView>> = mutableListOf()
    private var playerSaveCards: MutableList<LinearLayout<CardView>> = mutableListOf()

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

    private fun tryToBuy(dragEvent: DragEvent): Boolean {
	/*
	val playerActionService = rootService.playerActionService
	val cardView: CardView = dragEvent.draggedComponent as CardView
	val devCard: DevCard? = devCardMap.backward(cardView)
	checkNotNull(devCard) { "No dec Card found."}

	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player
	
	try{
	    playerActionService.buyCard(devCard, true, playerGemSelection, 0, player)
	    return false
	}
	catch(e: Exception) {
	    return false
	}
	 */
	return false
    }
    
    private fun tryToSave(dragEvent: DragEvent): Boolean {
	return false
    }

    private var currentPlayer: Player? = null
    private var currentPlayerIndex: Int = -1
    
    private fun fillLayouts() {
	nobleTiles.clear()
	nobleTiles.removeAll{card -> true}
	
	levelOneCards.clear()
	levelOneCards.removeAll{card -> true}
	
	levelTwoCards.clear()
	levelTwoCards.removeAll{card -> true}
	
	levelThreeCards.clear()
	levelThreeCards.removeAll{card -> true}

	playerDevCards.forEach{
	    it.forEach{ card -> if(card.parent!=null) card.removeFromParent()}
	    if(it.parent!=null) it.removeFromParent()
	    it.clear()
	}
	playerSaveCards.clear()
	
	playerSaveCards.forEach{
	    it.forEach{ card -> if(card.parent!=null) card.removeFromParent()}
	    if(it.parent!=null) it.removeFromParent()
	    it.clear()
	}
	playerSaveCards.clear()

	val game = rootService.currentGame
	checkNotNull(game) { "No game found."}

	//Player

	val playerList = game.currentGameState.playerList
	for(player in playerList) {
	    println("PLAYER: "+player.name)
	    val devCards: LinearLayout<CardView> = LinearLayout(
		posX = 1380, posY = 100, width = 200, height = 800,
                orientation = Orientation.VERTICAL, alignment = Alignment.BOTTOM_CENTER,
                visual = ColorVisual(221, 136, 136), spacing = -110
	    )
	    val devCardsOrigin = player.devCards
	    print("\t:")
	    println(devCardsOrigin)
	    devCardsOrigin.forEach{
		card -> run {
		    val cardView = toCardView(card.id)
		    devCards.add(cardView)
		}
	    }
	    print("LINEARLAYOUT: ")
	    for(cardView in devCards) {
		print(cardView)
		print(", ")
	    }
	    println()
	    
	    devCards.isVisible = false
	    devCards.dropAcceptor = {
		dragEvent -> tryToBuy(dragEvent)
	    }
	    
	    val saveCards: LinearLayout<CardView> = LinearLayout(
		posX = 280, posY = 100, width = 200, height = 800,
                orientation = Orientation.VERTICAL, alignment = Alignment.BOTTOM_CENTER,
                visual = ColorVisual(221, 136, 221), spacing = 10
	    )
	    val saveCardsOrigin = player.reservedCards
	    saveCardsOrigin.forEach{
		card -> run {
		    val cardView = toCardView(card.id)
		    saveCards.add(cardView)
		}
	    }
	    saveCards.isVisible = false
	    saveCards.dropAcceptor= {
		dragEvent -> tryToSave(dragEvent)
	    }

	    playerDevCards.add(devCards)
	    playerSaveCards.add(saveCards)
	}

	playerDevCards.forEach{ addComponents(it)}
	playerSaveCards.forEach{ addComponents(it)}

	for(gem in allGems) {
	    playerGemSelection.put(gem, 0)
	    gameGemSelection.put(gem, 0)
	}

	//Board ?

	val nobleTilesOrigin = game.currentGameState.board.nobleTiles
	print("nobleTiles: ")
	println(nobleTilesOrigin)
	nobleTilesOrigin.forEach{
	    card -> run {
		val cardView = toCardView(card.id)
		nobleTiles.add(cardView)
	    }
	}

	val levelOneCardsOrigin = game.currentGameState.board.levelOneOpen
	print("levelOneCardsOrign: ")
	println(levelOneCardsOrigin)
	levelOneCardsOrigin.forEach{
	    card -> run {
		val cardView = toCardView(card.id)
		levelOneCards.add(cardView)
	    }
	}

	val levelTwoCardsOrigin = game.currentGameState.board.levelTwoOpen
	print("levelTwoCards: ")
	println(levelTwoCardsOrigin)
	levelTwoCardsOrigin.forEach{
	    card -> run {
		val cardView = toCardView(card.id)
		levelTwoCards.add(cardView)
	    }
	}

	val levelThreeCardsOrigin = game.currentGameState.board.levelThreeOpen
	print("levelThreeCards: ")
	println(levelThreeCardsOrigin)
	levelThreeCardsOrigin.forEach{
	    card -> run {
		val cardView = toCardView(card.id)
		levelThreeCards.add(cardView)
	    }
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
                text = "", font = Font(size = 20, color = Color.WHITE)
	    )

	    var selectLabel = Label(
		posX = 100.0 - 50.0, width=25.0, height=25.0,
                text = "", font = Font(size = 20, color = Color.WHITE)
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
                text = "", font = Font(size = 40, color = Color.WHITE)
	    )

	    var selectLabel = Label(
		posX = 1800 - 75.0, width=50.0, height=50.0,
                text = "", font = Font(size = 40, color = Color.WHITE)
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
    
    override fun refreshAfterStartNewGame() {
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	initializePlayerGems()
	initializeGameGems()

    }

    override fun refreshAfterEndTurn() {
	fillLayouts()
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	currentPlayer = game.currentGameState.currentPlayer
	currentPlayerIndex = game.currentGameState.playerList.indexOf(currentPlayer)
	checkNotNull(currentPlayer) { "No player found."}
	val player = currentPlayer as Player

	currentPlayerLabel.text = player.name

	playerDevCards[currentPlayerIndex].isVisible = true
	playerSaveCards[currentPlayerIndex].isVisible = true

	for(gem in game.currentGameState.board.gems.entries) {
	    gameGemMax.put(gem.key, gem.value)
	}

	for(gem in player.gems.entries) {
	    playerGemMax.put(gem.key, gem.value)
	}

	renderGameGems()
	renderPlayerGems()
    }

    override fun refreshAfterBuyCard(devCard: DevCard) {
	/*
	val cardView: CardView? = devCardMap.forward(devCard)
	checkNotNull(cardView) { "No card found."}
	cardView.isDraggable = false
	
	fillLayouts()

	refreshAfterTakeGems()
	 */
    }


    init {
	imageLoader.preload()
	
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
	)
    }
}
