package graphics

import game.G
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Timer

class AnimationManager {
    val PATH = "./src/main/resources/graphics/animations/"

    val animations = mutableListOf<Animation>()
    val textures: MutableMap<String, BufferedImage> = mutableMapOf()
    fun getTexture(name: String) : BufferedImage {
        if (!textures.containsKey(name)){
            textures[name] = ImageIO.read(File(PATH + name))
        }
        return textures.getValue(name)
    }

    val timer: Timer = Timer(100) {
        animations.forEach {
            it.nextFrame(System.currentTimeMillis())
            try {
                G.win.gamePanel.repaint()
            } catch (t: Throwable) {

            }
        }
    }

    fun start() {
        timer.start()
    }

    fun stop() {
        timer.stop()
    }
}