import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class Vector(var x: Int = 0, var y: Int = 0) {
    operator fun plus(right: Vector) = Vector(x + right.x, y + right.y)
    operator fun minus(right: Vector) = Vector(x - right.x, y - right.y)
    operator fun times(right: Int) = Vector(x * right, y * right)
    operator fun div(right: Int) = Vector(x / right, y / right)
    override operator fun equals(other: Any?) = x == (other as Vector).x && y == other.y
    fun distance(right: Vector) = sqrt((x - right.x).toFloat().pow(2) + (y - right.y).toFloat().pow(2))
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
}