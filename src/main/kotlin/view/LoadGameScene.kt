package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.components.uicomponents.Button
import javafx.stage.FileChooser
import java.io.File
import entity.SplendorImageLoader
import javafx.stage.DirectoryChooser
import org.intellij.lang.annotations.JdkConstants.FontStyle
import java.awt.Color


/** [LoadGameScene] : [MenuScene] that is displayed after the game ends depending on condition
 *  [imageLoader] : Facilitates loading of various images needed for the [LoadGameScene] using SplendorImageLoader
 *   [image] : Facilitates loading of various buttons needed for the [LoadGameScene] using SplendorImageLoader
 *   [backgroundImage] : Facilitates loading of background needed for the [LoadGameScene] using SplendorImageLoader
 *   [headLineLabel] :   Label indicating LoadScene
 *   [fileName] : File names
 */

class LoadGameScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

	private val imageLoader = SplendorImageLoader()
	private val image = imageLoader.button()
	private val backgroundImage = imageLoader.highscoreBackground()
	private val load = imageLoader.loadGame()
    private val fileChooser = DirectoryChooser()
    private var openedFileChooser: Boolean = false
	private var file: File? = null

    private val fileName = Label(
	width = 400, height = 100,
	posX = width/2 - 200, posY = 215,
	text = "", font = Font(size=40, color = Color.WHITE, fontStyle=Font.FontStyle.ITALIC),
    )

	/**[chooseFileButton] : Button that facilitates selection of a saved game. */
    private val chooseFileButton = Button(
	width = 800, height = 450,
	posX = width/2 - 400, posY = height/2 - 225,
	text = "", font = Font(size=16),
	visual = load
    ).apply{
	onMouseClicked = {
	    if(!openedFileChooser) {
		openedFileChooser = true
		val tempFile: File? = fileChooser.showDialog(null)
		openedFileChooser = false
		if(tempFile!=null) {
		    file = tempFile
		    fileName.text = tempFile.name
			startButton.isDisabled = false
		}
	    }
	}
    }

	/**[startButton] : Button that visually represents the start button of loadGameScene. */
    val startButton = Button(
	width = 200, height = 100,
	posX = 1650, posY = 930,
	text = "Start", font = Font(size=18),
	visual = image
    ).apply {
		isDisabled = true
//		onMouseClicked = {
//			val ioService = rootService.ioService
//			checkNotNull(file) { "No file found. "}
//			try {
//				ioService.loadGame((file as File).absolutePath)
//			}
//			catch(e: Exception) {
//				println(e)
//			}
//		}
	}

	/**[backButton] : Button that visually represents the back button of loadGameScene. */
    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = image
    )
    
    init{
	background = backgroundImage


	startButton.isDisabled = true

	addComponents(
	    backButton,
	    startButton,
	    chooseFileButton,
	    fileName,
	)
    }

	/**[loadGame] : Method facilitatin loading of a previously "existing" saved game.*/
	fun loadGame(){
		val ioService = rootService.ioService
		try {
			ioService.loadGame((file as File).absolutePath)
		}
		catch(e: Exception) {
			println(e)
		}
	}
}
