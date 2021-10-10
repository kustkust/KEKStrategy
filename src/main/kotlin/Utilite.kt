import java.awt.Polygon
import java.awt.event.MouseEvent

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

fun <T> Matrix<T>.matrixForEachIndexed(iter: (Vector, T) -> Unit) =
    this.forEachIndexed { x, row ->
        row.forEachIndexed { y, cell ->
            iter(Vector(x, y), cell)
        }
    }

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

fun makePolygon(points: Array<Vector>) =
    Polygon(points.map { it.x }.toIntArray(), points.map { it.y }.toIntArray(), points.size)

val java.awt.Point.toVector
    get() = Vector(x,y)