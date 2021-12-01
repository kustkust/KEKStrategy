package graphics

import game.G
import utilite.Rect
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Timer

class AnimationManager {
    val PATH = "./src/main/resources/graphics/animations/"

    val animations = mutableListOf<Animation>()

    private val textures: MutableMap<String, BufferedImage> = mutableMapOf()
    fun getTexture(name: String): BufferedImage {
        if (!textures.containsKey(name)) {
            textures[name] = ImageIO.read(File(PATH + name))
        }
        return textures.getValue(name)
    }

    fun getTexture(name: String, color: Color, frames: List<Pair<Rect, Rect>>): BufferedImage {
        val fullName = "$name;$color"
        if (!textures.containsKey(fullName)) {
            val r = color.red
            val g = color.green
            val b = color.blue
            val fr = Color(255, 0, 0, 0).rgb
            val fg = Color(0, 255, 0, 0).rgb
            val fb = Color(0, 0, 255, 0).rgb
            val fa = Color(0, 0, 0, 255).rgb
            val rgb = color.rgb and 0x00FFFFFF
            val tmp = ImageIO.read(File(PATH + name))
            val imageData = IntArray(frames.first().first.w * frames.first().first.h)
            val maskData = IntArray(frames.first().first.w * frames.first().first.h)
            val resData = IntArray(frames.first().first.w * frames.first().first.h)
            val res =
                BufferedImage(frames.maxOf { it.first.r }, frames.maxOf { it.first.b }, BufferedImage.TYPE_INT_ARGB)
            val gg = res.graphics
            for ((frame, mask) in frames) {
                tmp.getRGB(
                    frame.l,  frame.t, frame.w, frame.h,
                    imageData,
                    0, frame.w
                )
                tmp.getRGB(
                    mask.l,  mask.t, mask.w, mask.h,
                    maskData,
                    0, mask.w
                )
                for (i in imageData.indices) {
                    val m = ((maskData[i] shr 24) and 0x000000FF) / 255f

                    val ir = imageData[i] shr 16 and 0xFF
                    val rr = ((ir + ((r - ir) * m).toInt()) shl 16) and fr

                    val ig = imageData[i] shr 8 and 0Xff
                    val rg = ((ig + ((g - ig) * m).toInt()) shl 8) and fg

                    val ib = imageData[i] and 0xFF
                    val rb = (ib + ((b - ib) * m).toInt()) and fb

                    resData[i] = imageData[i] and fa or rr or rg or rb
                }
                res.setRGB(
                    frame.l,
                    frame.t,
                    frame.w,
                    frame.h,
                    resData,
                    0,
                    frame.w
                )
                /* for (x in mask.l until mask.r) {
                    for (y in mask.t until mask.b) {
                        tmp.setRGB(x, y, (tmp.getRGB(x, y) and fa) or rgb)
                    }
                }

                gg.drawImage(
                    tmp,
                    frame.l, frame.t, frame.r, frame.b,
                    frame.l, frame.t, frame.r, frame.b,
                    null
                )
                gg.drawImage(
                    tmp,
                    frame.l, frame.t, frame.r, frame.b,
                    mask.l, mask.t, mask.w, mask.h,
                    null
                )*/
            }
            textures[fullName] = res
        }
        return textures.getValue(fullName)
    }

    private val animationCash: MutableMap<String, Animation> = mutableMapOf()
    fun getAnimation(name: String, color: Color? = null): Animation {
        val fullName = "$name;$color"
        if (!animationCash.containsKey(fullName)) {
            animationCash[fullName] = Animation(name, color)
        }
        return animationCash.getValue(fullName).copy()
    }

    val timer: Timer = Timer(100) {
        animations.forEach {
            it.nextFrame(System.currentTimeMillis())
        }
        try {
            G.win.gamePanel.repaint()
        } catch (t: Throwable) {

        }
    }

    fun start() {
        timer.start()
    }

    fun stop() {
        timer.stop()
    }
}