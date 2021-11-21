import java.awt.Graphics
import java.awt.Polygon
import java.awt.event.MouseEvent
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sqrt

typealias Matrix<T> = Array<Array<T>>

operator fun <T> Matrix<T>.get(vec: Vector) = this[vec.x][vec.y]
operator fun <T> Matrix<T>.set(vec: Vector, value: T) {
    this[vec.x][vec.y] = value
}

fun <T> Matrix<T>.matrixForEachIndexed(iter: (Int, Int, T) -> Unit) =
    this.forEachIndexed { x, row ->
        row.forEachIndexed { y, cell ->
            iter(x, y, cell)
        }
    }

fun <T> Matrix<T>.matrixForEachIndexed(x0: Int, x1: Int, y0: Int, y1: Int, iter: (Int, Int, T) -> Unit) {
    for (x in x0 until x1) {
        for (y in y0 until y1) {
            iter(x, y, this[x][y])
        }
    }
}

fun <T> Matrix<T>.matrixForEachIndexed(iter: (Vector, T) -> Unit) =
    this.forEachIndexed { x, row ->
        row.forEachIndexed { y, cell ->
            iter(Vector(x, y), cell)
        }
    }

fun <T> Matrix<T>.matrixForEachIndexed(pos: Vector, size: Vector, iter: (Int, Int, T) -> Unit) =
    matrixForEachIndexed(pos.x, pos.x + size.x, pos.y, pos.y + size.y, iter)

inline fun <reified T> makeMatrix(width: Int, height: Int, init: (Int, Int) -> T) =
    Matrix(width) { x ->
        Array(height) { y ->
            init(x, y)
        }
    }

inline fun <reified T> Matrix<T>.matrixClone() = Matrix(size) { this[it].clone() }


inline fun <reified T> makeMatrix(size: Vector, init: (Vector) -> T) =
    makeMatrix(size.x, size.y) { x, y -> init(Vector(x, y)) }

val MouseEvent.pos
    get() = Vector(x, y)

fun makePolygon(vararg points: Vector) =
    Polygon(points.map { it.x }.toIntArray(), points.map { it.y }.toIntArray(), points.size)

fun <T> MutableList<T>.copy() = ArrayList<T>(this)

val java.awt.Point.toVector
    get() = Vector(x, y)

fun getCellLine(p0: Vector, p1: Vector): MutableList<Vector> {
    val l = mutableListOf<Vector>()
    if (abs(p0.x - p1.x) > abs(p0.y - p1.y)) {
        val k = (p1.y - p0.y).toDouble() / (p1.x - p0.x).toDouble()
        val b = p0.y - k * p0.x
        val r = if (p0.x < p1.x) p0.x..p1.x else p1.x..p0.x
        for (i in r) {
            l += Vector(i, round(k * i + b).toInt())
        }
    } else {
        val k = (p1.x - p0.x).toDouble() / (p1.y - p0.y).toDouble()
        val b = p0.x - k * p0.y
        val r = if (p0.y < p1.y) p0.y..p1.y else p1.y..p0.y
        for (i in r) {
            l += Vector(round(k * i + b).toInt(), i)
        }
    }
    return l
}

fun getCellCircle(p: Vector, r: Int): MutableList<Vector> {
    val tmp = arrayListOf<Vector>()
    for (x in 0..floor(r / sqrt(2.0)).toInt()) {
        tmp += Vector(x, round(sqrt((r * r - x * x).toDouble())).toInt())
    }
    val rmp = tmp.asReversed()
    return (tmp.map { Vector(it.x, it.y) + p } +
            rmp.map { Vector(it.y, it.x) + p } +
            tmp.map { Vector(-it.y, it.x) + p } +
            rmp.map { Vector(it.x, -it.y) + p } +
            tmp.map { Vector(-it.x, -it.y) + p } +
            rmp.map { Vector(-it.y, -it.x) + p } +
            tmp.map { Vector(it.y, -it.x) + p } +
            rmp.map { Vector(-it.x, it.y) + p }
            ).toMutableList()
}

fun epsNei(epsilon: Int, pos: Vector = Vector(), iter: (Vector) -> Unit) {
    for (i in -epsilon..epsilon)
        for (j in -epsilon + abs(i)..epsilon - abs(i))
            iter(pos + Vector(i, j))
}

fun Graphics.drawLine(p1: Vector, p2: Vector) = drawLine(p1.x, p1.y, p2.x, p2.y)

fun Graphics.drawRect(pos: Vector, size: Vector) = drawRect(pos.x, pos.y, size.x, size.y)
fun Graphics.drawRect(rect: Rect) = drawRect(rect.pos, rect.size)
fun Graphics.fillRect(pos: Vector, size: Vector) = fillRect(pos.x, pos.y, size.x, size.y)
fun Graphics.fillRect(rect: Rect) = fillRect(rect.pos, rect.size)

fun Graphics.drawString(str: String, pos: Vector) = drawString(str, pos.x, pos.y)
fun Graphics.drawMultiString(str: String, pos: Vector) {
    val p = pos.copy()
    str.split('\n').forEach {
        p.y += fontMetrics.height
        drawString(it, p)
    }
}