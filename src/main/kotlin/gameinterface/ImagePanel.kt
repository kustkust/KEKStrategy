package gameinterface

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JPanel

class ImagePanel(path: String) : JPanel() {
    private var image: BufferedImage? = null
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(image, 0, 0, size.width, size.height, this)
    }

    init {
        try {
            image = ImageIO.read(File(path))
        } catch (ex: IOException) {
            // handle exception...
        }
    }
}