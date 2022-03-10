package entity

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import tools.aqua.bgw.visual.ImageVisual

private const val HUMAN_ICON = "/human_green.jpg"
private const val DRAG_N_DROP_ICON = "/dragAndDrop.png"
private const val START_BACKGROUND = "/Splendor-background.jpg"
private const val BUTTON_IMAGE = "/button.jpg"
private const val REDO_IMAGE = "/redo.png"
private const val UNDO_IMAGE = "/Undo.png"
private const val HINT_IMAGE = "/hint.png"
private const val TABLE_IMAGE = "/Table.jpg"

/**
 * Class to load Images from ressources
 */
class SplendorImageLoader {
	/**
	 * function that returns Human Icon
	 */
	fun humanIcon(): ImageVisual {
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource(HUMAN_ICON)
		)
		return ImageVisual(image)
	}
	/**
	 * function that returns drag and drop image
	 */
	fun dragAndDrop(): ImageVisual {
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource(DRAG_N_DROP_ICON)
		)
		return ImageVisual(image)
	}
	/**
	 * function that returns Card Image
	 */
	fun frontImageFor(id: Int): ImageVisual {
		val idString = (id+1).toString()
		return ImageVisual(
			ImageIO.read(
				SplendorImageLoader::class.java.getResource("/cards/"+idString+".jpg")
			)
		)
	}
	/**
	 * function that returns Start Background
	 */
	fun startBackground(): ImageVisual {
		return ImageVisual(
			ImageIO.read(
				SplendorImageLoader::class.java.getResource(START_BACKGROUND)
			)
		)
	}
	/**
	 * function that returns Button Design
	 */
	fun button(): ImageVisual {
		return ImageVisual(
			ImageIO.read(
				SplendorImageLoader::class.java.getResource(BUTTON_IMAGE)
			)
		)
	}
	/**
	 * function that returns Redo Button Image
	 */
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
}
