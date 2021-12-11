package utility

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
class Rect(
    override var pos: Vector = Vector(),
    override var size: Vector = Vector()
) : IRect {
    @SerialName("x")
    override var x
        get() = pos.x
        set(value) {
            pos.x = value
        }

    @SerialName("y")
    override var y: Int
        get() = pos.y
        set(value) {
            pos.y = value
        }

    @SerialName("w")
    override var w: Int
        get() = size.x
        set(value) {
            size.x = value
        }

    @SerialName("h")
    override var h: Int
        get() = size.y
        set(value) {
            size.y = value
        }

    constructor(x: Int, y: Int, width: Int, height: Int) :
            this(Vector(x, y), Vector(width, height))

    override var l
        get() = pos.x
        set(v) {
            pos.x = v
        }
    override var r
        get() = pos.x + size.x
        set(v) {
            size.x = v - pos.x
        }
    override var t
        get() = pos.y
        set(v) {
            pos.y = v
        }
    override var b
        get() = pos.y + size.y
        set(v) {
            size.y = v - pos.y
        }
    override var lt: Vector
        get() = pos
        set(v) {
            pos = v
        }
    override var lb: Vector
        get() = Vector(l, b)
        set(v) {
            l = v.x; b = v.y
        }
    override var rt: Vector
        get() = Vector(r, t)
        set(v) {
            r = v.x; t = v.y
        }
    override var rb: Vector
        get() = pos + size
        set(v) {
            r = v.x; b = v.y
        }

    @Serializer(forClass = Rect::class)
    companion object : KSerializer<Rect> {
        override val descriptor = buildClassSerialDescriptor("Rect") {
            element<Int>("x")
            element<Int>("y")
            element<Int>("w")
            element<Int>("h")
        }

        override fun deserialize(decoder: Decoder): Rect {
            val res = Rect()
            decoder.beginStructure(descriptor).run {
                loop@ while (true) {
                    when (val i = decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> break@loop
                        0 -> res.x = decodeIntElement(descriptor, i)
                        1 -> res.y = decodeIntElement(descriptor, i)
                        2 -> res.w = decodeIntElement(descriptor, i)
                        3 -> res.h = decodeIntElement(descriptor, i)
                        else -> throw SerializationException("Unknown index $i")
                    }
                }
                endStructure(descriptor)
            }
            return res
        }

        override fun serialize(encoder: Encoder, value: Rect) {
            encoder.beginStructure(descriptor).run {
                encodeIntElement(descriptor, 0, value.x)
                encodeIntElement(descriptor, 1, value.y)
                encodeIntElement(descriptor, 2, value.w)
                encodeIntElement(descriptor, 3, value.h)
                endStructure(descriptor)
            }
        }

    }
}