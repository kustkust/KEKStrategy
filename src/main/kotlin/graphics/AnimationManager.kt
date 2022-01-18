package graphics

import game.map.Cell
import game.G
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import utility.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Timer

class AnimationManager {
    val animations = mutableListOf<Animation>()

    private var lastTime = System.currentTimeMillis()
    var delta = 0L

    private val timer: Timer = Timer(1000 / 30) {
        val t = System.currentTimeMillis()
        delta = t - lastTime
        lastTime = t
        animations.forEach {
            it.nextFrame(System.currentTimeMillis())
        }
        try {
            G.win.gameRenderPanel.repaint()
        } catch (_: Throwable) {

        }
    }

    private val textures: MutableMap<String, BufferedImage> = mutableMapOf()

    private fun getTexture(name: String) = textures.getOrPut(name) {
        ImageIO.read(File("${C.Paths.animation}/$name"))
    }

    private fun getTexture(name: String, color: Color, frames: List<Pair<Rect, Rect>>) =
        textures.getOrPut("$name;$color") {
            val r = color.red
            val g = color.green
            val b = color.blue
            val fr = Color(255, 0, 0, 0).rgb
            val fg = Color(0, 255, 0, 0).rgb
            val fb = Color(0, 0, 255, 0).rgb
            val fa = Color(0, 0, 0, 255).rgb
            val tmp = ImageIO.read(File("${C.Paths.animation}/$name"))
            val imageData = IntArray(frames.first().first.w * frames.first().first.h)
            val maskData = IntArray(frames.first().first.w * frames.first().first.h)
            val resData = IntArray(frames.first().first.w * frames.first().first.h)
            val res =
                BufferedImage(frames.maxOf { it.first.r }, frames.maxOf { it.first.b }, BufferedImage.TYPE_INT_ARGB)
            for ((frame, mask) in frames) {
                tmp.getRGB(
                    frame.l, frame.t, frame.w, frame.h,
                    imageData,
                    0, frame.w
                )
                tmp.getRGB(
                    mask.l, mask.t, mask.w, mask.h,
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
            res
        }

    private val animationCash: MutableMap<String, Animation> = mutableMapOf()
    fun getAnimation(name: String) =
        animationCash.getOrPut(name) { loadAnimation(name) }.copy()

    fun getAnimation(name: String, color: Color) =
        animationCash.getOrPut("$name;$color") { loadAnimation(name, color) }.copy()

    private fun loadAnimation(name: String): Animation {
        val a = Animation()
        val path = "${C.Paths.animation}/$name.json"
        val json = Json {
            ignoreUnknownKeys = true
        }
        val forRead: Animation.ForRead = json.decodeFromStream(File(path).inputStream())
        a.frames = List(forRead.frames.size) { forRead.frames[it].toFrame() }
        forRead.meta.frameTags.forEach {
            a.tags[it.name] = Animation.FrameTag(it.from, it.to, it.direction)
        }
        a.tags[Animation.DefaultTagName] =
            Animation.FrameTag(0, a.frames.size - 1, Animation.AnimDirect.Forward)
        a.source = G.animationManager.getTexture(forRead.meta.image)
        return a
    }

    private fun loadAnimation(name: String, color: Color): Animation {
        val a = Animation()
        val path = "${C.Paths.animation}/$name.json"
        val json = Json {
            ignoreUnknownKeys = true
        }
        val forRead: Animation.ForRead = json.decodeFromStream(File(path).inputStream())
        a.frames = forRead.frames.filterMap({ it.filename != Animation.MaskLayerName }) {
            it.toFrame()
        }
        val maskFrames = forRead.frames.filterMap({ it.filename == Animation.MaskLayerName }) {
            it.toFrame()
        }
        forRead.meta.frameTags.forEach {
            a.tags[it.name] = Animation.FrameTag(it.from, it.to, it.direction)
        }
        a.tags[Animation.DefaultTagName] =
            Animation.FrameTag(0, a.frames.size - 1, Animation.AnimDirect.Forward)
        a.source = G.animationManager.getTexture(
            forRead.meta.image,
            color,
            a.frames.map { it.frame } zip maskFrames.map { it.frame })
        return a
    }

    fun getAnimation(name: String, d: Map<Cell.CellDir, Boolean>) =
        animationCash.getOrPut("$name;${d.uls};${d.urs};${d.dls};${d.drs}") {
            loadAnimation(name, d)
        }.copy()

    private fun loadAnimation(name: String, d: Map<Cell.CellDir, Boolean>): Animation {
        val path = "${C.Paths.animation}/$name.json"
        val json = Json { ignoreUnknownKeys = true }
        val forRead: Animation.ForRead = json.decodeFromStream(File(path).inputStream())
        val t = getTexture(forRead.meta.image)
        val framesTag = { tagName: String ->
            val tag = forRead.meta.frameTags.first { it.name == tagName }
            (tag.from..tag.to).map { forRead.frames[it] }
        }
        val mf = framesTag("M")
        val resT = BufferedImage(
            mf.maxOf { it.frame.r },
            mf.maxOf { it.frame.b },
            BufferedImage.TYPE_INT_ARGB
        )
        val g = resT.graphics as Graphics2D
        val ulf = framesTag(d.uls)
        val urf = framesTag(d.urs)
        val dlf = framesTag(d.dls)
        val drf = framesTag(d.drs)
        val drawCorner = { mf_: Animation.FrameJson, f: Animation.FrameJson ->
            g.drawImage(
                t,
                mf_.frame.l + f.spriteSourceSize.l,
                mf_.frame.t + f.spriteSourceSize.t,
                mf_.frame.l + f.spriteSourceSize.r,
                mf_.frame.t + f.spriteSourceSize.b,
                f.frame.l, f.frame.t, f.frame.r, f.frame.b,
                null
            )
        }
        for (i in mf.indices) {
            drawCorner(mf[i], ulf[i])
            drawCorner(mf[i], urf[i])
            drawCorner(mf[i], dlf[i])
            drawCorner(mf[i], drf[i])
        }
        textures["$name;${d.uls};${d.urs};${d.dls};${d.drs}"] = resT
        if (G.tmp == null) G.tmp = resT

        val a = Animation()
        a.source = resT
        a.frames = mf.map { it.toFrame() }
        a.tags[Animation.DefaultTagName] =
            Animation.FrameTag(0, a.frames.size - 1, Animation.AnimDirect.Forward)
        animationCash["$name;${d.uls};${d.urs};${d.dls};${d.drs}"] = a
        return a
    }

    fun start() = timer.start()

    fun stop() = timer.stop()
}