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

class ConfigScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {

    private val difficulties: Array<String> = arrayOf(
	"NEVER USED", "EASY", "MEDIUM", "HARD"
    )
    
    private val selection: IntArray = intArrayOf(
	1, -2, 1, -2, 1, -2, 1, -2,
    )
    
    private val headLineLabel = Label(
	width = 300, height = 200,
	posX = width/2 - 150, posY = 50,
	text = "Configurate Game!",
	font = Font(size=30)
    )

    private val bar: Label = Label(
	posX = 100, posY = 300, width = 10, height = 400,
	visual = ColorVisual.WHITE
    )

    private val knob: Label = Label(
	width = 20, height = 20, visual = ColorVisual.WHITE,
    ).apply {
	onMouseClicked = {
	    val x = MouseInfo.getPointerInfo().getLocation().getX()
	    val y = MouseInfo.getPointerInfo().getLocation().getY()
	}
    }
    
    private var textFields: List<TextField> = listOf()

    private var icons: List<Button> = listOf()

    private var difficultyTexts: List<Button> = listOf()

    private var size = 1

    private val delButton = Button(
	width = 50, height = 50,
	text = "-", visual = ColorVisual(221, 136, 136)
    ).apply {
	onMouseClicked = {
	    size = (size - 1)
	    if(size<=1) size = 1
	    refresh()
	}
    }

    private val addButton = Button(
	width = 50, height = 50,
	text = "+", visual = ColorVisual(136, 221, 136)
    ).apply {
	onMouseClicked = {
	    size = (size + 1)
	    if(size>=3) size = 3
	    refresh()
	}
    }
    
    val backButton = Button(
	width = 200, height = 100,
	posX = 50, posY = 930,
	text = "Back",
	font = Font(size = 28), visual = ColorVisual(136, 136, 221)
    )

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

    private fun refresh() {
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
	
	for(i in 0..size) {
	    textFields[i].isVisible = true
	    icons[i].isVisible = true
	    if(selection[i*2]>0) icons[i].opacity = 1.0
	    difficultyTexts[i].isVisible = true
	    if(selection[i*2+1]>0) difficultyTexts[i].opacity = 1.0
	}

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
    
    init{
	
	val imageLoader = SplendorImageLoader()
	val image: ImageVisual = imageLoader.humanIcon()
	
	for(i in 0..3) {
	    val textField = TextField(
		posX = width/2 - 200, posY = 300+i*150,
		width = 400, height = 100,
		text = "", font = Font(size = 26),
	    )

	    val icon = Button(
		posX = width/2 + 250, posY = 300+i*150,
		text = "", visual = image,
		width = 100, height = 100,
	    ).apply{
		onMouseClicked = {
		    select(i, true)
		    refresh()
		}
	    }

	    val difficultyText = Button(
		posX = width/2 + 400 , posY = (300+i*150),
		visual = ColorVisual(81,126,44),
		font = Font(size = 36),
		width = 200, height = 100
	    ).apply{
		onMouseClicked = {
		    select(i, false)
		    refresh()
		}
	    }

	    textFields+=textField
	    icons+=icon
	    difficultyTexts+=difficultyText
	}
	
	icons.forEach{ addComponents(it)}
	textFields.forEach{ addComponents(it) }
	difficultyTexts.forEach{ addComponents(it) }

	knob.posY = bar.posY - knob.height/2
	knob.posX = bar.posX + bar.width/2 - knob.width/2

	// 81,126,44 why ? 
	background = ColorVisual(108, 168, 59)
	
	refresh()

	addComponents(
	    headLineLabel,
	    backButton,
	    addButton,
	    delButton,
	    bar,
	    knob,
	)
    }
}
