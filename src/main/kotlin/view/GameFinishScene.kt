package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.components.uicomponents.Button
import entity.Player
import entity.SplendorImageLoader
import java.awt.Color

/** [GameFinishScene] : [MenuScene] that is displayed after the game ends depending on condition
 *  [imageLoader] : Facilitates loading of various images needed for the [GameFinishScene] using SplendorImageLoader
 *  [image] : Facilitates loading of various buttons needed for the [GameFinishScene] using SplendorImageLoader
 *  [backgroundImage] : Facilitates loading of background needed for the [GameFinishScene] using SplendorImageLoader
 *  [headLineLabel] :   Label indicating endscene
 */

class GameFinishScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable{
    val imageLoader = SplendorImageLoader()
    val image = imageLoader.button()
    val backgroundImage = imageLoader.highscoreBackground()
    val carbon = imageLoader.carbon()
    private val headLineLabel = Label(
        width = 300, height = 200,
        posX = width/2 - 150, posY = 50,
        text = "EndScene",
        font = Font(size = 44)
    )
    /**[backButton] : Buttons for returning to previous scene*/
    val backButton = Button(
        width = 200, height = 100,
        posX = 50, posY = 930,
        text = "New Game",
        font = Font(size = 28), visual = image
    )

    /**[rank0] : Label to display winner */
    val rank0 = Label(
        900,300,1000,500,"",
        font= Font(size = 20, color = Color.ORANGE, fontStyle = Font.FontStyle.ITALIC)
    )
    /**[rank2] : Label to display runner up */
    val rank1 = Label(
        900,400,1000,500,"",
        font= Font(size = 17,  color = Color.BLUE, fontStyle = Font.FontStyle.ITALIC)
    )

    /**[rank2] : Label to display third player */
    val rank2 = Label(
        900,500,1000,500,"",
        font= Font(size = 15, color = Color.GREEN, fontStyle = Font.FontStyle.ITALIC)
    )

    /**[rank2] : Label to display loser */
    val rank3 = Label(
        900,600,1000,500,"",
        font= Font(size = 12, color = Color.WHITE, fontStyle = Font.FontStyle.ITALIC)
    )

    val labelList : MutableList<Label> = mutableListOf()

    /**[showScore] : Button that when pressed shows the scores of the players */
    private val showScore = Button(
        100,500,400,400,"SHOW HIGHSCORE", visual = image
    ).apply {
        onMouseClicked={
            ranking()
        }
    }

    /**[ranking] : Method used to display the scores in descending order.
     *  Initially , the function checks if we have an existing game.
     *  In the case of an existing game , we sort the scores in descending order using sortBy and
     *  these are then displayed.
     * */
    fun ranking (){
        val rankings : MutableList<Player> = mutableListOf()
        val game = rootService.currentGame
        if(game!=null) {
            for (i in 0..game.currentGameState.playerList.size-1){
                rankings.add(game.currentGameState.playerList.get(i))
            }
            rankings.sortBy { (it.score) }

            labelList.add(rank0)
            labelList.add(rank1)
            labelList.add(rank2)
            labelList.add(rank3)
            for(i in 0..rankings.size-1) {
                labelList.get(i).text = "Rank "+i+": Player "+rankings.get(i).name+" has reached "+rankings.get(i).score.toString()+ " Points"
            }
        }
    }

    init {

        background = backgroundImage
        ranking()
        addComponents(
            headLineLabel,
            backButton,
            rank0,
            rank1,
            rank2,
            rank3,
            showScore
        )

    }
}