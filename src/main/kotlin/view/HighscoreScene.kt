package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button
import entity.Highscore
import entity.Player
import entity.SplendorImageLoader
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.util.Font.FontStyle
import java.awt.Color

class HighscoreScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable{

	val imageLoader = SplendorImageLoader()
	val image = imageLoader.button()
	val backgroundImage = imageLoader.highscoreBackground()
	val highscores = imageLoader.highscores()
	private val headLineLabel = Label(
		width = 600, height = 200,
		posX = 660 , posY = 135,
		text = "",
		font = Font(size = 44),
		visual = highscores,
		alignment = Alignment.CENTER
	)
    private var highscoreLabels: MutableList<Label> = mutableListOf()

    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = image
    )

    private fun loadHighscores(highscoreList: MutableList<Highscore>) {
		highscoreLabels.forEach { it.removeFromParent() }
		highscoreLabels.clear()

		val size = highscoreList.size
		if (size == 0) return

		highscoreList.sortBy { -it.score }

		var player = highscoreList[0]

		labelList.add(rank0)
		labelList.add(rank1)
		labelList.add(rank2)
		labelList.add(rank3)
		labelList.add(rank4)
		println(highscoreList.size)
		for(i in 0..highscoreList.size-1){
			player= highscoreList[i]
			val index = i+1
			labelList.get(i).text=index.toString()+". Place: "+ player.playerName + ": " + player.score
		}

	}

	/**[rank0] : Label to display winner */
	val rank0 = Label(
		420, 100, 1000, 500, "",
		font = Font(size = 40, color = Color.ORANGE, fontStyle = Font.FontStyle.ITALIC)
	)

	/**[rank2] : Label to display runner up */
	val rank1 = Label(
		420, 200, 1000, 500, "",
		font = Font(size = 36, color = Color.PINK, fontStyle = Font.FontStyle.ITALIC)
	)

	/**[rank2] : Label to display third player */
	val rank2 = Label(
		420, 300, 1000, 500, "",
		font = Font(size = 32, color = Color.GREEN, fontStyle = Font.FontStyle.ITALIC)
	)

	/**[rank2] : Label to display loser */
	val rank3 = Label(
		420, 400, 1000, 500, "",
		font = Font(size = 28, color = Color.WHITE, fontStyle = Font.FontStyle.ITALIC)
	)
	/**[rank2] : Label to display third player */
	val rank4 = Label(
		420, 500, 1000, 500, "",
		font = Font(size = 24, color = Color.MAGENTA, fontStyle = Font.FontStyle.ITALIC)
	)


	private val labelList : MutableList<Label> = mutableListOf()

	override fun refreshAfterShowHighscores() {
		loadHighscores(rootService.ioService.loadHighscore())
	}
    
    init {
	refreshAfterShowHighscores()
	background = backgroundImage

	addComponents(

	    backButton,
		rank0,
		rank1,
		rank2,
		rank3,
		rank4,
		headLineLabel
	)
    }
}
