package view

import entity.SplendorImageLoader
import service.RootService
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.image.BufferedImage

class GameScene(private val rootService: RootService): BoardGameScene(1920, 1080), Refreshable {

	val imageLoader = SplendorImageLoader()
	val image = imageLoader.button()
	val undo = imageLoader.undoButton()
	val redo = imageLoader.redoButton()
	val hint = imageLoader.hintButton()
	val tableImage = imageLoader.table()

    private val quitButton = Button(
		width = 25, height = 25,
		posX = 0, posY = 0,
		text = "X",
		font = Font(size = 15),
		visual = ColorVisual(255,0,0)
    )

	private val undoButton = Button(
		width = 100, height = 100,
		posX = 50, posY = 350,
		text = "",
		font = Font(size = 28),
		visual = undo
	)

	private val redoButton = Button(
		width = 100, height = 100,
		posX = 200, posY = 350,
		text = "",
		font = Font(size = 28),
		visual = redo
	)

    private val hintButton = Button(
		width = 100, height = 50,
		posX = 50, posY = 500,
		text = "",
		font = Font(size = 28),
		visual = hint
	)

	private val saveGameButton = Button(
		width = 200, height = 100,
		posX = 50, posY = 950,
		text = "Save Game",
		font = Font(size = 17),
		visual = image
	)

	private val loadHighscoreButton = Button(
		width = 200, height = 100,
		posX = 50, posY = 800,
		text = "Highscores",
		font = Font(size = 17),
		visual = image
	)

	private val returnGemsButton= Button(
		width = 200, height = 100,
		posX = 50, posY =650 ,
		text = "Return Gems",
		font = Font(size = 17),
		visual = image
	)

	private val nextPlayerButton= Button(
		width = 200, height = 100,
		posX = 1650, posY =540 ,
		text = "Next Player",
		font = Font(size = 28),
		visual = image
	)

    init {

		background = tableImage


	addComponents(
	    quitButton,
	)
    }
}
