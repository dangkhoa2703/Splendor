package entity

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import tools.aqua.bgw.visual.ImageVisual

private const val HUMAN_ICON = "/human_green.jpg"
private const val DRAG_N_DROP_ICON = "/dragAndDrop.png"

class SplendorImageLoader {
	fun humanIcon(): ImageVisual {
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource(HUMAN_ICON)
		)
		return ImageVisual(image)
	}

	fun dragAndDrop(): ImageVisual {
	    val image: BufferedImage = ImageIO.read(
		SplendorImageLoader::class.java.getResource(DRAG_N_DROP_ICON)
	    )
	    return ImageVisual(image)
	}

	fun frontImageFor(id :Int) : ImageVisual{
		val idString = (id+1).toString()
		val image: BufferedImage = ImageIO.read(
			SplendorImageLoader::class.java.getResource("/cards/"+idString+".png")
		)
		return ImageVisual(image)
	}
}
