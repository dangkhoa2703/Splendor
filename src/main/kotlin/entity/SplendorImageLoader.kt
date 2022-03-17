package entity

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import tools.aqua.bgw.visual.ImageVisual

private const val HUMAN_ICON = "/human.png"
//private const val DRAG_N_DROP_ICON = "/dragAndDrop.png"
private const val START_BACKGROUND = "/Splendor-background.jpg"
private const val BUTTON_IMAGE = "/button.jpg"
private const val REDO_IMAGE = "/redo.png"
private const val UNDO_IMAGE = "/Undo.png"
private const val HINT_IMAGE = "/hint.png"
private const val TABLE_IMAGE = "/Background.png"
private const val CARD_BACK = "/card_back.jpg"
//private const val BACK_IMAGE = "/Back.png"
private const val CONFIG_BACKGROUND = "/configScene.jpg"
private const val HIGHSCORE_BACKGROUND = "/BackgroundHighScore.png"
private const val SAVEGAME_IMAGE = "/savegame.png"
//private const val LOAD_IMAGE = "savegame.png"
//private const val HIGHSCORES_IMAGE = "highscores.png"

/**
 * Class to load Images from resources
 */
class SplendorImageLoader {

    private var tokenImages: List<ImageVisual> = listOf()

    private var images: List<ImageVisual> = listOf()

    fun image(path: String): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(
		    path
		)
	    )
	)
    }

	/**[nextPlayersImage]: function that returns next players image
	 * @return image(players.png)*/
    fun nextPlayersImage(): ImageVisual {
	return image("/players.png")
    }

	/**[saveGameImage]: function that returns save game image
	 * @return image(SAVEGAME_IMAGE)*/
    fun saveGameImage(): ImageVisual {
	return image(SAVEGAME_IMAGE)
    }

	/**[humanIcon]: function that returns Human Icon
	 * @return image(HUMAN_ICON)*/
    fun humanIcon(): ImageVisual {
	return image(HUMAN_ICON)
    }

	/**[highscores]: function that returns highscores
	 * @return highscores.png*/
    fun highscores(): ImageVisual {
	return image("/highscores.png")
    }

	/**[startBackground]: function that returns startbackground
	 * @return Splendor Background*/
    fun startBackground(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(START_BACKGROUND)
	    )
	)
    }

	/**[button]: function that returns button images
	 * @return BUTTON_IMAGE */
    fun button(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(BUTTON_IMAGE)
	    )
	)
    }

    /**function that returns Redo Button Image*/
    fun redoButton(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(REDO_IMAGE)
	    )
	)
    }
    /**
     * function that returns Hint Button Image
     */
    fun hintButton(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(HINT_IMAGE)
	    )
	)
    }
    /**
     * function that returns Undo Button Image
     */
    fun undoButton(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(UNDO_IMAGE)
	    )
	)
    }
    /**
     * function that returns Table Background Image
     */
    fun table(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(TABLE_IMAGE)
	    )
	)
    }

	/** [cardBack] : Method, facilitating te visualisation of the back of a card.*/
    fun cardBack(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(CARD_BACK)
	    )
	)
    }

	/**[velocity] : Method applied to visualize velocity icon in configScene*/
    fun velocity(index: Int): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource("/velocity/velocity_$index.png")
	    )
	)
    }

	/**[shuffleImage] : Method applied to visualize shuffle visuals.*/
    fun shuffleImage(index: Int): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource("/shuffle/shuffle_$index.png")
	    )
	)
    }

	/**[configBackground] : Method applied to visualize configuration Background.*/
    fun configBackground(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(CONFIG_BACKGROUND)
	    )
	)
    }

	/**[highscoreBackground] : Method applied to visualize Highscore Background.*/
    fun highscoreBackground(): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(HIGHSCORE_BACKGROUND)
	    )
	)
    }

	/**[tokenImage] : Method applied to visualize various Tokens aka Gems.*/
    fun tokenImage(int: Int): ImageVisual {
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource(
			"/tokens/token$int.png"
		)
	    )
	)
    }

	/**[carbon] : Method applied to visualize reserve card and buy card placeholders.*/
	fun carbon(): ImageVisual {
		return ImageVisual(
			ImageIO.read(
				SplendorImageLoader::class.java.getResource(
					"/carbon.png"
				)
			)
		)
	}

	/**[loadGame] : Method applied to visualize load Game icon in LoadGameScene.*/
	fun loadGame(): ImageVisual {
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource(
				"/load.png"
			)
		)
		return ImageVisual(image)
	}

	/**[tokeImage] : Method applied to visualize corresponding tokens aka Gems.*/
    fun tokenImage(type: GemType): ImageVisual {
	return tokenImages[type.toInt()-1]
    }

    /** [frontImageFor] : function that returns Card Image Front with help of corresponding card id*/
    fun frontImageFor(id: Int): ImageVisual {
	return images[id]
    }

	/**[preload] : Method applied to preload the images of the various cards.*/
    fun preload() {
	for(i in 1..100){
		images = images + imageFor(i)
	}
    }

	/**[imageFor] : Method applied to load the images of a correspdoning card.*/
    fun imageFor(id: Int): ImageVisual {
	val idString = (id).toString()
	return ImageVisual(
	    ImageIO.read(
		SplendorImageLoader::class.java.getResource("/cards/$idString.jpg")
	    )
	)
    }

    init {
	for(i in 1..6) {
		tokenImages = tokenImages + tokenImage(i)
	}
    }
}
