package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.components.uicomponents.Button
import entity.SplendorImageLoader
import java.awt.Color
import kotlin.math.pow
import kotlin.math.roundToInt

/** [GameFinishScene] : [MenuScene] that is displayed after the game ends depending on condition
 *  [imageLoader] : Facilitates loading of various images needed for the [GameFinishScene] using SplendorImageLoader
 *  [image] : Facilitates loading of various buttons needed for the [GameFinishScene] using SplendorImageLoader
 *  [backgroundImage] : Facilitates loading of background needed for the [GameFinishScene] using SplendorImageLoader
 *  [headLineLabel] :   Label indicating endScene
 */

class GameFinishScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable{
    private val imageLoader = SplendorImageLoader()
    private val image = imageLoader.button()
    private val backgroundImage = imageLoader.highscoreBackground()
    private val highscores = imageLoader.highscores()
    private val headLineLabel = Label(
        width = 700, height = 200,
        posX = 600 , posY = 135,
        text = "",
        font = Font(size = 44),
        visual = highscores
    )
    /**[backButton] : Buttons for returning to previous scene*/
    val backButton = Button(
        width = 200, height = 100,
        posX = 50, posY = 930,
        text = "New Game",
        font = Font(size = 28), visual = image
    )

    /**[rank0] : Label to display winner */
    private val rank0 = Label(
        420,100,1000,500,"",
        font= Font(size = 28, color = Color.ORANGE, fontStyle = Font.FontStyle.ITALIC)
    )
    /**[rank2] : Label to display runner up */
    private val rank1 = Label(
        420,200,1000,500,"",
        font= Font(size = 25,  color = Color.PINK, fontStyle = Font.FontStyle.ITALIC)
    )

    /**[rank2] : Label to display third player */
    private val rank2 = Label(
        420,300,1000,500,"",
        font= Font(size = 23, color = Color.GREEN, fontStyle = Font.FontStyle.ITALIC)
    )

    /**[rank3] : Label to display loser */
    private val rank3 = Label(
        420,400,1000,500,"",
        font= Font(size = 19, color = Color.WHITE, fontStyle = Font.FontStyle.ITALIC)
    )

    private val labelList : MutableList<Label> = mutableListOf()

    /**[ranking] : Method used to display the scores in descending order.
     *  Initially , the function checks if we have an existing game.
     *  In the case of an existing game , we sort the scores in descending order using sortBy() and
     *  these are then displayed.
     * */
    private fun ranking (){
        val game = rootService.currentGame!!
//        val turnScore = game.turnCount.toDouble() / game.currentGameState.playerList.size.toDouble()
        val rankings = game.currentGameState.playerList.sortedByDescending { it.score }
        labelList.add(rank0)
        labelList.add(rank1)
        labelList.add(rank2)
        labelList.add(rank3)
        for(i in rankings.indices) { labelList[i].text = rankings[i].name + " has reached " +
                rankings[i].score + " Points" }
    }

    /** [refreshAfterEndGame] : override function of refreshAfterEndGame, various rank texts are refreshed */
    override fun refreshAfterEndGame() {
	rank0.text=""
	rank1.text=""
	rank2.text=""
	rank3.text=""
        ranking()
    }

    /**The view components are initially executed with the help of init */
    init {

        background = backgroundImage

        addComponents(
            headLineLabel,
            backButton,
            rank0,
            rank1,
            rank2,
            rank3,
        )

    }
}
