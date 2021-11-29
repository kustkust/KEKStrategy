package graphics

import game.G
import utilite.Rect
import utilite.Vector
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import kotlin.random.Random

class Animation() {
    @Serializable
    class Frame(
        var frame: Rect,
        var duration: Int = 500
    ) {
        fun paint(g: Graphics, p: Vector, source: BufferedImage) {
            g.drawImage(
                source,
                p.x, p.y, p.x + frame.w, p.y + frame.h,
                frame.x, frame.y, frame.r, frame.b,
                null,
            )
        }
    }

    @Serializable
    class ForRead(var frames: List<Frame>, var meta: Meta)

    @Serializable
    data class Meta(var image: String = "")

    lateinit var frames: List<Frame>
    lateinit var source: BufferedImage

    val run = true
    var curFrameInd = 0
        set(value) {
            field = if (value < 0) 0 else value % frames.size
        }
    val curFrame get() = frames[curFrameInd]
    var lastTime = System.currentTimeMillis()
    init {
        G.animationManager.animations.add(this)
    }

    constructor(source_: BufferedImage, frames_: List<Frame>): this() {
        source = source_
        frames = frames_
    }

    constructor(name: String) : this() {
        loadFromJson(name)
    }

    fun nextFrame(curTime: Long) {
        if(run && lastTime + curFrame.duration <= curTime){
            curFrameInd++
            lastTime += curFrame.duration
        }
    }

    fun paint(g: Graphics, p: Vector) = curFrame.paint(g,p,source)

    fun loadFromJson(name: String) {
        val path = G.animationManager.PATH + name + ".json"
        val s = File(path).bufferedReader().readText()
        val json = Json {
            ignoreUnknownKeys = true
        }
        val forRead: ForRead = json.decodeFromString(s)
        frames = forRead.frames
        source = G.animationManager.getTexture(forRead.meta.image)
        curFrameInd = Random.nextInt(0, frames.size)
    }
}