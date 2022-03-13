package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.visual.ImageVisual
import entity.SplendorImageLoader
import java.awt.MouseInfo
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.SwingUtilities
import entity.PlayerType
import tools.aqua.bgw.components.uicomponents.CheckBox
import java.awt.Color

/**
 *  Graphical User Interface Scene, used to configure Players and AI for [SplendorApplication]
 *  It is of type MenuScene with dimensions 1920x1080 px
 */
class ConfigScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

	/**
	 * [imageLoader] : used to load various images from the SplendorImageLoader() class, needed for configScene
	 * [image] : provides facility for buttons needed for the configScene
	 * [backgroundImage] : background image of config scene
	 */
    private val imageLoader = SplendorImageLoader()
    private val image = imageLoader.button()
	private val backgroundImage = imageLoader.configBackground()

	/** [speeds] : values to configure the speed simulation of the AI Players */
    private val speeds: Array<String> = arrayOf(
	" SLOW ", "NORMAL", " FAST "
    )

	/**[difficulties] : values to configure play difficulty / intensity of an AI player*/
    private val difficulties: Array<String> = arrayOf(
	"NEVER USED", "EASY", "MEDIUM", "HARD"
    )
    
    private val selection: IntArray = intArrayOf(
	1, -2, 1, -2, 1, -2, 1, -2, 1, 0
    )

	/**[headLineLabel] : text to indicate configuration of [SplendorApplication]*/
    private val headLineLabel = Label(
	width = 400, height = 200,
	posX = width/2 - 200, posY = 50,
	text = "Configurate Game!",
	font = Font(size=44)
    )

	/**[textFields] : input fields for 2 to 4 unique players/AIs*/
    private var textFields: List<TextField> = listOf()

	/**[icons] : icons indicating a player or an AI*/
    private var icons: List<Button> = listOf()

	/**[difficultyTexts] : Texts indicating the three different difficulties*/
    private var difficultyTexts: List<Button> = listOf()

	/**[speedButtons] : Buttons for indicating the three different speeds*/
    private var speedButtons: List<Button> = listOf()

	/**[shuffleButtons] : Buttons for creating random AIs for gameplay*/
	private var shuffleButtons: List<Button> = listOf()

    private var size = 1

	/**[delButton] : Buttons for deleting Player/AI input field in configScene*/
	private val delButton = Button(
	width = 50, height = 50,
	text = "-", visual = image
    ).apply {
	onMouseClicked = {
	    selection[size*2] = 1
	    selection[size*2+1] = -2
	    textFields[size].text = ""

	    size = (size - 1)
	    if(size<=1) size = 1
	    refresh()
	    refreshStartButton()
	}
    }
	/**[addButton] : Buttons for adding Player/AI input field in configScene*/
    private val addButton = Button(
	width = 50, height = 50,
	text = "+", visual = image
    ).apply {
	onMouseClicked = {
	    size = (size + 1)
	    if(size>=3) size = 3
	    refresh()
	    refreshStartButton()
	}
    }
	/**[backButton] : Button for closing application*/
    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = image
    )

	/**[refreshStartButton] : Function for refreshing/enabling/disabling the start button
	 * the startbutton is disable when a given [textFields] is left blank
	 * */
    private fun refreshStartButton() {
	var startDisabled = false

	for(i in 0..size) {
	    if(textFields[i].text.isBlank()) {
		startDisabled = true
		break
	    }
	}

	startButton.isDisabled = startDisabled
    }

    private fun select(row: Int, isHuman: Boolean) {
	if(isHuman) {
	    if(selection[row*2+1]>0 )selection[row*2+1]*=-1
	    selection[row*2]=1
	}
	else {
	    var state = selection[row*2+1]
	    if(state>0) {
		state--
		state = (state+1)%3
		state++
		selection[row*2+1]=state
	    }
	    else {
		selection[row*2]=-1
		selection[row*2+1]*=-1
	    }
	}
    }
	/**[refresh] :*/
    private fun refresh() {
	speedButtons.forEach{ it.isVisible= false}
	shuffleButtons.forEach{ it.isVisible = false}
	shuffleButtons[selection[9]].isVisible = true
	textFields.forEach{ it.isVisible = false }
	icons.forEach{
	    it.isVisible = false
	    it.opacity = 0.2
	}
	for(i in 0..difficultyTexts.size-1) {
	    val but: Button = difficultyTexts[i]
	    var absIndex: Int = selection[i*2+1]
	    if(absIndex<0) absIndex*=-1
	    but.isVisible = false
	    but.opacity = 0.5
	    but.text = difficulties[absIndex]
	}

	var speedVisible: Boolean = true
	for(i in 0..size) {
	    textFields[i].isVisible = true
	    icons[i].isVisible = true
	    if(selection[i*2]>0) {
		icons[i].opacity = 1.0
		speedVisible = false
	    }
	    difficultyTexts[i].isVisible = true
	    if(selection[i*2+1]>0) difficultyTexts[i].opacity = 1.0
	}
	speedButtons[selection[8]].isVisible = speedVisible
	
	addButton.posY = 300.0+(size+1)*150.0
	delButton.posY = 300.0+(size+1)*150.0

	when(size) {
	    1 -> {
		delButton.isVisible = false
		addButton.isVisible = true
		addButton.posX = width/2 - 25
	    }
	    2 -> {
		addButton.isVisible = true
		delButton.isVisible = true
		addButton.posX = width/2 - 75
		delButton.posX = width/2 + 25
	    }
	    3 -> {
		addButton.isVisible = false
		delButton.isVisible = true
		delButton.posX = width/2 - 25
	    }
	}
    }

    private fun start() {
	var players: List<Pair<String, PlayerType>> = listOf()
	for(i in 0..size) {
	    var type: PlayerType = PlayerType.HUMAN
	    if(selection[i*2]<1) {
		when(selection[i*2+1]) {
		    1 -> {
			type = PlayerType.EASY
		    }
		    2 -> {
			type = PlayerType.MEDIUM
		    }
		    3 -> {
			type = PlayerType.HARD
		    }
		}
	    }
	    val namePair: Pair<String, PlayerType> = Pair(textFields[i].text, type)
	    players+=namePair
	}
	
	rootService.gameService.startNewGame(
	    players, selection[9]==0, selection[8]
	)
    }
	/**[startButton] : Button to start game after successful configuration*/
    private val startButton = Button(
	width = 200, height = 100,
	posX = 1650, posY = 930,
	text = "Start", font = Font(size = 28),
	visual = ColorVisual(136, 221, 136)
    ).apply{
	onMouseClicked = {
	    start()
	}
    }
    
    init{
	val humanIcon: ImageVisual = imageLoader.humanIcon()
	
	
	for(i in 0..3) {
	    val textField = TextField(
		posX = width/2 - 200, posY = 300+i*150,
		width = 400, height = 100,
		text = "", font = Font(size = 26),
	    ).apply{
		onKeyTyped = {
		    refreshStartButton()
		}
	    }

	    val icon = Button(
		posX = width/2 + 250, posY = 300+i*150,
		text = "", visual = humanIcon,
		width = 100, height = 100,
	    ).apply{
		onMouseClicked = {
		    select(i, true)
		    refresh()
		}
	    }

	    val difficultyText = Button(
		posX = width/2 + 400 , posY = (300+i*150),
			visual = ColorVisual.TRANSPARENT,
		font = Font(size = 36, color = Color.WHITE),
		width = 200, height = 100
	    ).apply{
		onMouseClicked = {
		    select(i, false)
		    refresh()
		}
	    }

	    if(i<3) {
		val speedButton = Button(
		    posX = 500, posY = 450, width = 100, height = 100,
		    text="", visual = imageLoader.velocity(i)
		).apply {
		    onMouseClicked = {
			selection[8]=(selection[8]+1)%3
			refresh()
		    }
		}
		speedButtons+=speedButton
	    }

	    if(i<2) {
		val shuffleButton = Button(
		    posX = 500, posY = 300, width = 100, height = 100,
		    text="", visual = imageLoader.shuffleImage(i)
		).apply{
		    onMouseClicked = {
			selection[9]=1-selection[9]
			refresh()
		    }
		}
		shuffleButtons+=shuffleButton
	    }

	    textFields+=textField
	    icons+=icon
	    difficultyTexts+=difficultyText
	    
	}
	
	icons.forEach{ addComponents(it)}
	textFields.forEach{ addComponents(it) }
	difficultyTexts.forEach{ addComponents(it) }
	speedButtons.forEach{ addComponents(it) }
	shuffleButtons.forEach{ addComponents(it) }

	textFields[0].text = listOf("Fry", "Bender", "Leela", "Amy", "Zoidberg").random();
		textFields[1].text = listOf("Fry", "Bender", "Leela", "Amy", "Zoidberg").random();

	// 81,126,44 why ?
	background = backgroundImage
	
	refresh()

	addComponents(
	    headLineLabel,
	    backButton,
	    addButton,
	    delButton,
	    startButton,
	)
    }
}
