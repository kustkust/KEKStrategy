package graphics

import game.G
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import utilite.Rect
import utilite.Vector
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import kotlin.random.Random

class Animation() {
    @Serializable
    data class Frame(
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

    @Serializable(with = AnimDirect.AnimDirectSerializer::class)
    enum class AnimDirect {
        Forward,
        Backward,
        Pingpong;

        @Serializer(forClass = AnimDirect::class)
        object AnimDirectSerializer: KSerializer<AnimDirect> {
            override val descriptor: SerialDescriptor
                get() = PrimitiveSerialDescriptor("AnimDirect", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): AnimDirect {
                val s = decoder.decodeString().capitalize()
                return valueOf(s)
            }

            override fun serialize(encoder: Encoder, value: AnimDirect) {
                encoder.encodeString(value.name.lowercase(Locale.getDefault()))
            }
        }
    }

    @Serializable
    class ForRead(var frames: List<Frame>, var meta: Meta)

    @Serializable
    data class Meta(var image: String = "", var frameTags: List<FrameTagsJson>? = null)

    @Serializable
    data class FrameTagsJson(val name: String, val from: Int, val to: Int, val direction: AnimDirect)

    data class FrameTag(val from: Int, val to: Int, val direction: AnimDirect)

    private lateinit var source: BufferedImage

    var frameTags = mutableMapOf<String, FrameTag>()
    var curFrameTagName: String = DefaultFrameName
    val curFrameTag
        get() = frameTags.getValue(curFrameTagName)
    var pingPongDir = AnimDirect.Forward

    private lateinit var frames: List<Frame>
    val size get() = frames.size
    var curFrameInd = 0
        set(value) {
            field = if (value < curFrameTag.from)
                curFrameTag.from
            else if (value > curFrameTag.to)
                curFrameTag.to
            else
                value
        }
    val curFrame get() = frames[curFrameInd]

    val run = true
    var lastTime = System.currentTimeMillis()

    fun copy() = Animation().apply {
        source = this@Animation.source
        frameTags = this@Animation.frameTags
        curFrameTagName = this@Animation.curFrameTagName
        pingPongDir = this@Animation.pingPongDir
        frames = this@Animation.frames
        curFrameInd = this@Animation.curFrameInd
        lastTime = this@Animation.lastTime
    }

    init {
        G.animationManager.animations.add(this)
    }

    constructor(source_: BufferedImage, frames_: List<Frame>) : this() {
        source = source_
        frames = frames_
    }

    constructor(name: String) : this() {
        loadFromJson(name)
    }

    fun nextFrame(curTime: Long) {
        if (run && lastTime + curFrame.duration <= curTime) {
            when (curFrameTag.direction) {
                AnimDirect.Forward -> {
                    if (curFrameInd == curFrameTag.to) {
                        curFrameInd = curFrameTag.from
                    } else {
                        curFrameInd++
                    }
                }
                AnimDirect.Backward -> {
                    if (curFrameInd == curFrameTag.from) {
                        curFrameInd = curFrameTag.to
                    } else {
                        curFrameInd--
                    }
                }
                AnimDirect.Pingpong -> when(pingPongDir) {
                    AnimDirect.Forward -> {
                        if (curFrameInd == curFrameTag.to) {
                            curFrameInd--
                            pingPongDir = AnimDirect.Backward
                        } else {
                            curFrameInd++
                        }
                    }
                    AnimDirect.Backward -> {
                        if (curFrameInd == curFrameTag.from) {
                            curFrameInd++
                            pingPongDir = AnimDirect.Forward
                        } else {
                            curFrameInd--
                        }
                    }
                    else -> {}
                }
            }
            lastTime += curFrame.duration
        }
    }

    fun paint(g: Graphics, p: Vector) = curFrame.paint(g, p, source)

    fun loadFromJson(name: String) {
        val path = G.animationManager.PATH + name + ".json"
        val s = File(path).bufferedReader().readText()
        val json = Json {
            ignoreUnknownKeys = true
        }
        val forRead: ForRead = json.decodeFromString(s)
        frames = forRead.frames
        forRead.meta.frameTags?.forEach {
            frameTags[it.name] = FrameTag(it.from, it.to, it.direction)
        }
        frameTags[DefaultFrameName] = FrameTag(0, frames.size - 1, AnimDirect.Forward)
        source = G.animationManager.getTexture(forRead.meta.image)
        curFrameInd = Random.nextInt(0, frames.size)
    }

    companion object {
        const val DefaultFrameName = "__default"
    }
}