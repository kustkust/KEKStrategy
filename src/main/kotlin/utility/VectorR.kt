package utility

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class VectorR(var x: Double, var y: Double) {
    constructor() : this(0.0, 0.0)
    constructor(x_: Int, y_: Int) : this(x_.toDouble(), y_.toDouble())

    operator fun plus(right: VectorR) = VectorR(x + right.x, y + right.y)
    operator fun minus(right: VectorR) = VectorR(x - right.x, y - right.y)
    operator fun times(right: Double) = VectorR(x * right, y * right)
    operator fun div(right: Double) = VectorR(x / right, y / right)
    operator fun div(right: Int) = this.div(right.toDouble())

    operator fun times(right: VectorR) = x*right.x+y*right.y

    override operator fun equals(other: Any?) = x == (other as VectorR).x && y == other.y
    fun distance(right: VectorR) = sqrt((x - right.x).sqr() + (y - right.y).sqr())
    fun cellDistance(right: VectorR) =
        (this.x - right.x).absoluteValue + (this.y - right.y).absoluteValue

    fun minVectorR(then: VectorR) = VectorR(min(this.x, then.x), min(this.y, then.y))
    fun comp(right: VectorR): Int {
        return if (this.x < right.x) {
            -1
        } else if (this.x > right.x) {
            1
        } else {
            if (this.y < right.y) {
                -1
            } else if (this.y > right.y) {
                1
            } else {
                0
            }
        }
    }

    fun copy() = VectorR(x, y)

    override fun toString(): String = "($x; $y)"

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        fun byAngleAndRadius(a: Double, r: Double) = VectorR(r * kotlin.math.cos(a), r * kotlin.math.sin(a))
        val Zero = VectorR()
        val Up = VectorR(0.0, -1.0)
        val Down = VectorR(0.0, 1.0)
        val Left = VectorR(-1.0, 0.0)
        val Right = VectorR(1.0, 0.0)
        val UpLeft = VectorR(-1.0, -1.0)
        val UpRight = VectorR(1.0, -1.0)
        val DownLeft = VectorR(-1.0, 1.0)
        val DownRight = VectorR(1.0, 1.0)
    }
}