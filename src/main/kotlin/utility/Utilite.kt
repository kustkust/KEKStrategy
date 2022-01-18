package utility

import game.map.Cell
import java.awt.Color
import java.awt.Graphics
import java.awt.Polygon
import java.awt.event.MouseEvent
import kotlin.math.*

fun Int.sqr(): Int = this * this
fun Float.sqr(): Float = this * this
fun Double.sqr(): Double = this * this

typealias Matrix<T> = ArrayList<ArrayList<T>>

fun <T> ArrayList<T>.removeRange(from: Int, to: Int) {
    var n = to - 1
    while (n >= from) {
        removeAt(n)
        n--
    }
}

fun <T> ArrayList<T>.removeFrom(from: Int) = removeRange(from, size)
fun <T> makeArrayList(size: Int, init: (Int) -> T): ArrayList<T> =
    ((0 until size).map { init(it) }).toMutableList() as ArrayList<T>

operator fun <T> Matrix<T>.get(vec: Vector) = this[vec.x][vec.y]
operator fun <T> Matrix<T>.set(vec: Vector, value: T) {
    this[vec.x][vec.y] = value
}

val <T> Matrix<T>.width get() = size
val <T> Matrix<T>.height get() = if (isNotEmpty()) this[0].size else 0

fun <T> Matrix<T>.print_() {
    for (y in 0 until height) {
        print("[")
        for (x in 0 until width)
            print("${this[x][y]}")
        println("]")
    }
}

fun <T> Matrix<T>.changeMatrixSize(newWidth: Int, newHeight: Int, init: (Int, Int) -> T) {
    val oldWidth = width
    val oldHeight = height
    if (oldWidth < newWidth) {
        ensureCapacity(newWidth)
        var n = newWidth - oldWidth
        while (n > 0) {
            add(makeArrayList(newHeight) { init(newWidth - n, it) })
            n--
        }
    } else if (oldWidth > newWidth) {
        removeFrom(newWidth)
    }
    if (oldHeight < newHeight) {
        for (x in 0 until min(oldWidth, newWidth)) {
            this[x].ensureCapacity(newHeight)
            var n = newHeight - oldHeight
            while (n > 0) {
                this[x].add(init(x, newHeight - n))
                n--
            }
        }
    } else if (height > newHeight) {
        for (x in 0 until min(oldWidth, newWidth)) {
            this[x].removeFrom(newHeight)
        }
    }
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

/*inline fun <reified T> utilite.makeMatrix(width_: Int, height_: Int, init: (Int, Int) -> T) =
    utilite.Matrix<T>(width_).apply {
        (0 until width_).forEach { x ->
            this.add(ArrayList<T>(height_).apply {
                (0 until height_).forEach { y ->
                    this.add(init(x, y))
                }
            })
        }
    }*/
inline fun <reified T> makeMatrix(width_: Int, height_: Int, crossinline init: (Int, Int) -> T) =
    makeArrayList(width_) { x -> makeArrayList(height_) { y -> init(x, y) } }

inline fun <reified T> Matrix<T>.matrixClone() = makeMatrix(width, height) { x, y -> this[x][y] }


inline fun <reified T> makeMatrix(size: Vector, crossinline init: (Vector) -> T) =
    makeMatrix(size.x, size.y) { x, y -> init(Vector(x, y)) }

val MouseEvent.pos
    get() = Vector(x, y)

fun makePolygon(vararg points: Vector) =
    Polygon(points.map { it.x }.toIntArray(), points.map { it.y }.toIntArray(), points.size)

fun <T, R> Iterable<T>.filterMap(pred: (T) -> Boolean, transform: (T) -> R): List<R> {
    val ar = ArrayList<R>()
    forEach {
        if (pred(it)) {
            ar.add(transform(it))
        }
    }
    return ar
}

fun <K, V> Map<K, V>.joinToString(
    separator: CharSequence = ", ",
    keyValueSeparator: CharSequence = "=",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Map.Entry<K,V>) -> CharSequence)? = null
): String {
    val buffer = StringBuilder()
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            if(transform == null) {
                buffer.append(element.key)
                buffer.append(keyValueSeparator)
                buffer.append(element.value)
            } else {
                buffer.append(transform(element))
            }
        } else break
    }
    if (limit in 0 until count) buffer.append(truncated)
    buffer.append(postfix)
    return buffer.toString()
}

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
fun Graphics.drawMultiString(str: String, x: Int, y: Int) = drawMultiString(str, Vector(x, y))
fun Graphics.drawMultiString(str: String, pos: Vector) {
    val p = pos.copy()
    str.split('\n').forEach {
        p.y += fontMetrics.height
        drawString(it, p)
    }
}


val Map<Cell.CellDir, Boolean>.uls: String
    get() {
        var tmp = "UL"
        if (getValue(Cell.CellDir.UpLeft)) tmp += 'C'
        if (getValue(Cell.CellDir.Up)) tmp += 'U'
        if (getValue(Cell.CellDir.Left)) tmp += 'L'
        return tmp
    }

val Map<Cell.CellDir, Boolean>.urs: String
    get() {
        var tmp = "UR"
        if (getValue(Cell.CellDir.UpRight)) tmp += 'C'
        if (getValue(Cell.CellDir.Up)) tmp += 'U'
        if (getValue(Cell.CellDir.Right)) tmp += 'R'
        return tmp
    }

val Map<Cell.CellDir, Boolean>.dls: String
    get() {
        var tmp = "DL"
        if (getValue(Cell.CellDir.DownLeft)) tmp += 'C'
        if (getValue(Cell.CellDir.Down)) tmp += 'D'
        if (getValue(Cell.CellDir.Left)) tmp += 'L'
        return tmp
    }

val Map<Cell.CellDir, Boolean>.drs: String
    get() {
        var tmp = "DR"
        if (getValue(Cell.CellDir.DownRight)) tmp += 'C'
        if (getValue(Cell.CellDir.Down)) tmp += 'D'
        if (getValue(Cell.CellDir.Right)) tmp += 'R'
        return tmp
    }

fun Color.withAlpha(newAlpha: Int): Color = Color(red, green, blue, newAlpha)