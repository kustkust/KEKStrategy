package graphics

import game.G
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import utility.Rect
import utility.Vector
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.util.*

class Animation() {
    @Serializable
    data class FrameJson(
        val filename: String,
        val frame: Rect,
        val duration: Int,
        var spriteSourceSize: Rect,
    ) {
        fun toFrame() = Frame(frame, duration, spriteSourceSize)
    }

    data class Frame(
        var frame: Rect,
        var duration: Int,
        var spriteSourceSize: Rect,
    ) {
        fun paint(g: Graphics, p: Vector, scale: Int, source: BufferedImage) {
            g.drawImage(
                source,
                p.x + spriteSourceSize.l * scale,
                p.y + spriteSourceSize.t * scale,
                p.x + spriteSourceSize.r * scale,
                p.y + spriteSourceSize.b * scale,
                frame.l, frame.t, frame.r, frame.b,
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
        object AnimDirectSerializer : KSerializer<AnimDirect> {
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
    class ForRead(var frames: List<FrameJson>, var meta: Meta)

    @Serializable
    data class Meta(var image: String = "", var frameTags: List<FrameTagsJson> = emptyList())

    @Serializable
    data class FrameTagsJson(val name: String, val from: Int, val to: Int, val direction: AnimDirect)

    data class FrameTag(val from: Int, val to: Int, val direction: AnimDirect)

    lateinit var source: BufferedImage

    var scale: Int = 2

    var tags = mutableMapOf<String, FrameTag>()
    var curTagName: String = DefaultTagName
        set(newTagName) {
            field = newTagName
            curFrameInd = when (curTag.direction) {
                AnimDirect.Forward, AnimDirect.Pingpong -> curTag.from
                AnimDirect.Backward -> curTag.to
            }
        }
    val curTag
        get() = tags.getValue(curTagName)
    var pingPongDir = AnimDirect.Forward

    lateinit var frames: List<Frame>
    val size get() = frames.size
    var curFrameInd = 0
        set(value) {
            field = if (value < curTag.from)
                curTag.from
            else if (value > curTag.to)
                curTag.to
            else
                value
        }
    val curFrame get() = frames[curFrameInd]

    var run = true
    var lastTime = System.currentTimeMillis()

    fun copy() = Animation().apply {
        source = this@Animation.source
        tags = this@Animation.tags
        curTagName = this@Animation.curTagName
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

    fun nextFrame(curTime: Long) {
        if (run && lastTime + curFrame.duration <= curTime) {
            when (curTag.direction) {
                AnimDirect.Forward -> {
                    if (curFrameInd == curTag.to) {
                        curFrameInd = curTag.from
                    } else {
                        curFrameInd++
                    }
                }
                AnimDirect.Backward -> {
                    if (curFrameInd == curTag.from) {
                        curFrameInd = curTag.to
                    } else {
                        curFrameInd--
                    }
                }
                AnimDirect.Pingpong -> when (pingPongDir) {
                    AnimDirect.Forward -> {
                        if (curFrameInd == curTag.to) {
                            curFrameInd--
                            pingPongDir = AnimDirect.Backward
                        } else {
                            curFrameInd++
                        }
                    }
                    AnimDirect.Backward -> {
                        if (curFrameInd == curTag.from) {
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

    fun paint(g: Graphics, p: Vector) = curFrame.paint(g, p, scale, source)

    companion object {
        const val DefaultTagName = "__default"
        const val MaskLayerName = "Mask"
    }
}