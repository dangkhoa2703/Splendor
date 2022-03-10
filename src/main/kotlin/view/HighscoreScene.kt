package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button
import entity.Highscore
import entity.SplendorImageLoader
import tools.aqua.bgw.util.Font.FontStyle

class HighscoreScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable{

	val imageLoader = SplendorImageLoader()
	val image = imageLoader.button()
    private val headLineLabel = Label(
	width = 300, height = 200,
	posX = width/2 - 150, posY = 50,
	text = "Highscores",
	font = Font(size = 44)
    )

    private var highscoreLabels: MutableList<Label> = mutableListOf()

    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = image
    )

    private fun loadHighscores(highscoreList: MutableList<Highscore>) {
	highscoreLabels.forEach{ it.removeFromParent() }
	highscoreLabels.clear()

	val size = highscoreList.size
	if(size==0) return
	
	highscoreList.sortBy{ -it.score }

	var player = highscoreList[0]

	var label = Label(
	    width = 400, height = 180,
	    posX = width/2 - 200, posY = 200,
	    text = player.playerName+": "+player.score,
	    font = Font(size=42, fontStyle = FontStyle.ITALIC)
	)

	highscoreLabels.add(label)
	
	for(i in 1..size-1) {
	    player = highscoreList[i]
	    label = Label(
		width = 300, height = 80,
		text = player.playerName+": "+player.score, 
		font = Font(size=38, fontStyle = FontStyle.ITALIC)
	    )

	    if(size <= 6) {
		label.posX = width/2 - 130.0
		label.posY = 380.0+(i-1)*130.0
	    }
	    else {
		label.posX = width/2 - 350.0
		if((i-1)>4) label.posX+=400.0
		label.posY = 400.0+(i-1)*130.0
		if((i-1)>4) label.posY-=(5)*130.0
	    }

	    highscoreLabels.add(label)
	}

	highscoreLabels.forEach{ addComponents(it) }
    }
    
    init {
	val exampleHighscoreList: MutableList<Highscore> = mutableListOf(
	    Highscore("Alice", 123),
	    Highscore("Bob", 213),
	    Highscore("Chris", 11),
	    Highscore("A1", 45),
	    Highscore("aaaaa", 34),
	    Highscore("A23", 345),
	    Highscore("Basdgtf", 1120),
	    Highscore("asda", 12345)
	)

	loadHighscores(exampleHighscoreList)
	
	background = ColorVisual(108, 168, 59)

	addComponents(
	    headLineLabel,
	    backButton,
	)
    }
}
