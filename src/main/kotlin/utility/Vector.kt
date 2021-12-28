package utility

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable
data class Vector(var x: Int, var y: Int) {
    constructor() : this(0, 0)

    operator fun plus(right: Vector) = Vector(x + right.x, y + right.y)
    operator fun minus(right: Vector) = Vector(x - right.x, y - right.y)
    operator fun times(right: Int) = Vector(x * right, y * right)
    operator fun div(right: Int) = Vector(x / right, y / right)
    override operator fun equals(other: Any?) = x == (other as Vector).x && y == other.y
    infix fun distance(right: Vector) = sqrt((x - right.x).toDouble().pow(2) + (y - right.y).toDouble().pow(2))
    fun cellDistance(right: Vector) =
        (this.x - right.x).absoluteValue + (this.y - right.y).absoluteValue

    fun minVector(then: Vector) = Vector(min(this.x, then.x), min(this.y, then.y))
    fun comp(right: Vector): Int {
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

    fun copy() = Vector(x, y)

    override fun toString(): String = "($x; $y)"

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    fun toVectorR() = VectorR(x.toDouble(), y.toDouble())

    companion object {
        val Zero = Vector()
        val Up = Vector(0, -1)
        val Down = Vector(0, 1)
        val Left = Vector(-1, 0)
        val Right = Vector(1, 0)
        val UpLeft = Vector(-1, -1)
        val UpRight = Vector(1, -1)
        val DownLeft = Vector(-1, 1)
        val DownRight = Vector(1, 1)
    }
}