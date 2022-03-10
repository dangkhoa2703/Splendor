package entity

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import tools.aqua.bgw.visual.ImageVisual

private const val HUMAN_ICON = "/human_green.jpg"
private const val DRAG_N_DROP_ICON = "/dragAndDrop.png"
private const val START_BACKGROUND = "/Splendor-background.jpg"

/**
 * 	class for loading the images of Splendor
 * */
class SplendorImageLoader {

	/** gets icons of players */
	fun humanIcon(): ImageVisual {
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource(HUMAN_ICON)
		)
		return ImageVisual(image)
	}

	/** dragAndDrop */
	fun dragAndDrop(): ImageVisual {
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource(DRAG_N_DROP_ICON)
		)
		return ImageVisual(image)
	}

	/** gets front Images for revealed cards */
	fun frontImageFor(id: Int): ImageVisual {
		val idString = (id+1).toString()
		return ImageVisual(
			ImageIO.read(
				SplendorImageLoader::class.java.getResource("/cards/$idString.jpg")
			)
		)
	}

	/** background */
	fun startBackground(): ImageVisual {
		return ImageVisual(
			ImageIO.read(
				SplendorImageLoader::class.java.getResource(START_BACKGROUND)
			)
		)
	}
}