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

    private val fileChooser = FileChooser()
    private var openedFileChooser: Boolean = false

    private val headLineLabel = Label(
	width = 300, height = 200,
	posX = width/2 - 150, posY = 50,
	text = "Load Game",
	font = Font(size = 44)
    )

    private val areaLabel = Label(
	width = 400, height = 400,
	posX = width/2 - 200, posY = height/2 - 200,
	text = ""
    )

    private val chooseFileButton = Button(
	width = 100, height = 50,
	posX = width/2 - 50, posY = height/2 - 25,
	text = "loadFile", font = Font(size=16),
	visual = ColorVisual(255, 255, 255)
    ).apply{
	onMouseClicked = {
	    if(!openedFileChooser) {
		openedFileChooser = true
		val file: File? = fileChooser.showOpenDialog(null)
		openedFileChooser = false
		if(file!=null) println(file.getName())
	    }
	}
    }

    private val startButton = Button(
	width = 200, height = 100,
	posX = 1650, posY = 930,
	text = "Start", font = Font(size=18),
	visual = ColorVisual(136, 221, 136)
    ) 

    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = ColorVisual(136, 136, 221)
    )
    
    init{
	background = ColorVisual(108, 168, 59)

	val imageLoader = SplendorImageLoader()
	val image: ImageVisual = imageLoader.dragAndDrop()

	areaLabel.visual = image

	startButton.isDisabled = true

	addComponents(
	    headLineLabel,
	    backButton,
	    startButton,
	    chooseFileButton,
	    areaLabel,
	)
    }
}
