package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.uicomponents.Button
import javafx.stage.FileChooser
import java.io.File
import entity.SplendorImageLoader
import tools.aqua.bgw.visual.ImageVisual

class LoadGameScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

	val imageLoader = SplendorImageLoader()
	val image = imageLoader.button()
	val backgroundImage = imageLoader.highscoreBackground()
	val load = imageLoader.loadGame()
    private val fileChooser = FileChooser()
    private var openedFileChooser: Boolean = false

    private val fileName = Label(
	width = 200, height = 50,
	posX = width/2 - 150, posY = 215,
	text = "", font = Font(size=18)
    )

    private val headLineLabel = Label(
	width = 600, height = 200,
	posX = width/2 - 300, posY = 50,
	text = "Press below to Load",
	font = Font(size = 40),

    )


    private val chooseFileButton = Button(
	width = 800, height = 450,
	posX = width/2 - 400, posY = height/2 - 225,
	text = "", font = Font(size=16),
	visual = load
    ).apply{
	onMouseClicked = {
	    if(!openedFileChooser) {
		openedFileChooser = true
		val file: File? = fileChooser.showOpenDialog(null)
		openedFileChooser = false
		if(file!=null) {
		    println(file.getName())
		    fileName.text = file.getName()
		}
	    }
	}
    }

    private val startButton = Button(
	width = 200, height = 100,
	posX = 1650, posY = 930,
	text = "Start", font = Font(size=18),
	visual = image
    ) 

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
	    headLineLabel,
	    backButton,
	    startButton,
	    chooseFileButton,
	    fileName,
	)
    }
}
