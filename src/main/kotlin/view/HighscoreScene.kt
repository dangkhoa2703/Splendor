package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.components.uicomponents.Button
import entity.Highscore
import entity.SplendorImageLoader
import tools.aqua.bgw.core.Alignment
import java.awt.Color

/**[MenuScene] that is used for visualizing highscores of previous games. Navigable when clicking the load Highscores
 * button in [StartScene].
 */
class HighscoreScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable{

	private val imageLoader = SplendorImageLoader()
	private val image = imageLoader.button()
	private val backgroundImage = imageLoader.highscoreBackground()
	private val highscores = imageLoader.highscores()
	private val headLineLabel = Label(
		width = 700, height = 200,
		posX = 600 , posY = 135,
		text = "",
		font = Font(size = 44),
		visual = highscores,
		alignment = Alignment.CENTER
	)
    private var highscoreLabels: MutableList<Label> = mutableListOf()

	/**[backButton] : Button that visually represents the back button of HighScorescene. */
    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = image
    )

	/** Method that loads the highscores of the various players*/
    private fun loadHighscores(highscoreList: MutableList<Highscore>) {
		highscoreLabels.forEach { it.removeFromParent() }
		highscoreLabels.clear()

		val size = highscoreList.size
		if (size == 0) return

		highscoreList.sortBy { -it.score }

		var player: Highscore

		labelList.add(rank0)
		labelList.add(rank1)
		labelList.add(rank2)
		labelList.add(rank3)
		labelList.add(rank4)
		for(i in 0..4){
			player= highscoreList[i]
			val index = i+1
			labelList[i].text=index.toString()+". Place: "+ player.playerName + ": " + player.score
		}

	}

	/**[rank0] : Label to display player with the topmost prestige points */
	private val rank0 = Label(
		420, 100, 1000, 500, "",
		font = Font(size = 40, color = Color.ORANGE, fontStyle = Font.FontStyle.ITALIC)
	)

	/**[rank1] : Label to display runner up with topmost points */
	private val rank1 = Label(
		420, 200, 1000, 500, "",
		font = Font(size = 36, color = Color.PINK, fontStyle = Font.FontStyle.ITALIC)
	)

	/**[rank2] : Label to display third player with most points */
	private val rank2 = Label(
		420, 300, 1000, 500, "",
		font = Font(size = 32, color = Color.GREEN, fontStyle = Font.FontStyle.ITALIC)
	)

	/**[rank3] : Label to display fourth player with most points */
	private val rank3 = Label(
		420, 400, 1000, 500, "",
		font = Font(size = 28, color = Color.WHITE, fontStyle = Font.FontStyle.ITALIC)
	)
	/**[rank4] : Label to display loser of the winners */
	private val rank4 = Label(
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
