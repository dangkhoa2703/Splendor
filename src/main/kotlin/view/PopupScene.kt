package view

import entity.Player
import entity.DevCard
import entity.SplendorImageLoader
import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.Font

class PopupScene(
    private val rootService: RootService,
    private val gameScene: GameScene
): MenuScene(1300, 1080), Refreshable {

    private val imageLoader: SplendorImageLoader = SplendorImageLoader()

    val quitButton = Button(
	width = 50, height = 50,
	posX = 1150, posY = 50,
	text = "X", visual = ColorVisual(221, 136, 136)
    )

    private fun standartComponents(): Unit {
	addComponents(
	    quitButton,
	)
    }

    private fun drawPlayer(
	index: Int,
	player: Player,
	devCardsLayout: List<CardView>,
	saveCardsLayout: List<CardView>
    ){
	val nameLabel = Label(
	    posX =index*400+25+50, posY = 100,
	    height = 100, width = 200,
	    text = player.name, font = Font(size = 24)
	)

	val typeLabel = Label(
	    posX =index*400+25+50, posY = 150,
	    height = 100, width = 200,
	    text = player.playerType.toString(), font = Font(size = 24)
	)

	val devCards: MutableList<DevCard> = player.devCards
	
	for(i in 0..devCards.size-1) {
	    val devCardLabel = Label(
		width = 95, height = 150,
		visual = devCardsLayout[i].frontVisual,
		posX = index*400+25, posY = 225+i*40
	    )

	    addComponents(devCardLabel)
	}

	val saveCards: MutableList<DevCard> = player.reservedCards

	for(i in 0..saveCards.size-1) {
	    val saveCardLabel = Label(
		width = 95, height = 150,
		visual = saveCardsLayout[i].frontVisual,
		posX = index*400+25+180, posY = 225+i*150
	    )

	    addComponents(saveCardLabel)
	}

	val gems = player.gems.entries

	var j = 0
	for(gem in gems) {
	    if(gem.value==0) continue
	    var gemLabel = Label(
		posX = index*400+25+180, posY = 980 - j*50,
		width = 25, height = 25,
		visual = imageLoader.tokenImage(gem.key)
	    )

	    var gemInfoLabel = Label(
		posX = index*400+25+180+25, posY = 980 - j*50,
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
	)
    }

    override fun refreshAfterPopup(currentPlayer: Player): Unit {
	clearComponents()
	standartComponents()
	
	val game = rootService.currentGame
	checkNotNull(game) { "No game found. "}

	val playerList = game.currentGameState.playerList

	val playerDevCards = gameScene.playerDevCards
	val playerSaveCards = gameScene.playerSaveCards

	var j = 0
	for(i in 0..playerList.size-1) {
	    val player = playerList[i]
	    if(player.equals(currentPlayer)) continue
	    drawPlayer(j, player, playerDevCards[i].components, playerSaveCards[i].components)
	    j++
	}
    }

    init {
	background = ColorVisual(136, 221, 136)
	
	standartComponents()
    }
}
