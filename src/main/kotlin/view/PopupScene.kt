package view

import entity.Player
import entity.DevCard
import entity.SplendorImageLoader
import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.Font
import entity.NobleTile

class PopupScene(
    private val rootService: RootService,
    private val gameScene: GameScene
): MenuScene(1400, 1080), Refreshable {

    private val imageLoader: SplendorImageLoader = SplendorImageLoader()

    val quitButton = Button(
	width = 50, height = 50,
	posX = 1250 - (1920-width)/2, posY = 25,
	visual = imageLoader.nextPlayersImage()
    )

    private fun standardComponents() {
	addComponents(
	    quitButton,
	)
    }

    private fun drawPlayer(
	index: Int,
	player: Player,
	devCardsLayout: List<CardView>,
	saveCardsLayout: List<CardView>,
	nobleTilesLayout: List<CardView>,
    ){
	val nameLabel = Label(
	    posX =index*500+25+75, posY = 100,
	    height = 100, width = 200,
	    text = player.name, font = Font(size = 24)
	)

	val typeLabel = Label(
	    posX =index*500+25+75, posY = 150,
	    height = 100, width = 200,
	    text = player.playerType.toString(), font = Font(size = 24)
	)

	val pointsLabel = Label(
	    posX = index*500, posY = 125,
	    text = player.score.toString(),
	    font = Font(size = 40, fontWeight = Font.FontWeight.BOLD)
	)

	val devCards: MutableList<DevCard> = player.devCards
	
	for(i in 0 until devCards.size) {
	    val devCardLabel = Label(
		width = 95, height = 150,
		visual = devCardsLayout[i].frontVisual,
		posX = index*500+25, posY = 225+i*40
	    )

	    addComponents(devCardLabel)
	}

	val saveCards: MutableList<DevCard> = player.reservedCards

	for(i in 0 until saveCards.size) {
	    val saveCardLabel = Label(
		width = 95, height = 150,
		visual = saveCardsLayout[i].frontVisual,
		posX = index*500+25+120, posY = 225+i*150
	    )

	    addComponents(saveCardLabel)
	}

	val nobleTiles: MutableList<NobleTile> = player.nobleTiles

	for(i in 0 until nobleTiles.size) {
	    val tileLabel = Label(
		width = 95, height = 150,
		visual = nobleTilesLayout[i].frontVisual,
		posX = index*500+25+240, posY = 225+i*150
	    )

	    addComponents(tileLabel)
	}

	val gems = player.gems.entries

	var j = 0
	for(gem in gems) {
	    if(gem.value==0) continue
	    val gemLabel = Label(
		posX = index*500+25+120, posY = 980 - j*50,
		width = 25, height = 25,
		visual = imageLoader.tokenImage(gem.key)
	    )

	    val gemInfoLabel = Label(
		posX = index*500+25+120+25, posY = 980 - j*50,
		width = 25, height = 25,
		text = gem.value.toString(),
		font = Font(size = 20)
	    )
	    j++
	    addComponents(gemLabel, gemInfoLabel)
	}
	
	addComponents(
	    nameLabel,
	    typeLabel,
	    pointsLabel,
	)
    }

    override fun refreshAfterPopup(currentPlayer: Player) {
	clearComponents()
	standardComponents()
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	val playerList = game.currentGameState.playerList

	val playerDevCards = gameScene.playerDevCards
	val playerSaveCards = gameScene.playerSaveCards
	val playerNobleTiles = gameScene.playerNobleTiles

	var j = 0
	for(i in playerList.indices) {
	    val player = playerList[i]
	    if(currentPlayer.id == player.id) continue
	    drawPlayer(
		j,
		player,
		playerDevCards[i].components,
		playerSaveCards[i].components,
		playerNobleTiles[i].components,
	    )
	    j++
	}
    }

    init {
	opacity = 0.8
	
	background = ColorVisual(136, 221, 136)
	
	standardComponents()
    }
}
