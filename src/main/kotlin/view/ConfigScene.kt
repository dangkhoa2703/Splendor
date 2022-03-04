package view

import service.RootService
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.TextField

class ConfigScene(private val rootService: RootService): MenuScene(1920, 1080), Refreshable {
    private val headLineLabel = Label(
	width = 300, height = 200,
	posX = width/2 - 150, posY = 50,
	text = "Configurate Game!",
	font = Font(size=30)
    )

    private val textFields: MutableList<TextField> = mutableListOf()

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
	posX = 1650, posY = 950,
	text = "Back",
	font = Font(size = 28), visual = ColorVisual(136, 136, 221)
    )

    private fun refresh() {
	textFields.forEach{ it.removeFromParent()}
	textFields.clear()

	for(i in 0..size) {
	    val textField = TextField(
		posX = width/2 - 200, posY = 300+i*150,
		width = 400, height = 100,
		text = "", font = Font(size = 26)
	    )

	    textFields.add(textField)
	}

	textFields.forEach{ addComponents(it) }

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
	background = ColorVisual(108, 168, 59)
	
	refresh()

	addComponents(
	    headLineLabel,
	    backButton,
	    addButton,
	    delButton,
	)
    }
}
