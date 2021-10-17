import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import kotlin.math.round
import kotlin.random.Random

class GameMap(val width: Int = 20, val height: Int = 20) {
    /**
     * Размер карты
     */
    val size
        get() = Vector(width, height)

    /**
     * Клетки карты
     */
    private var cells = Array(width) { Array(height) { Cell() } }

    var cellTranslation = Vector(0, 0)

    /**
     * Размер одной клетки в пикселях
     */
    val cs = 30

    /**
     * Текстура для рисования тумана войны
     */
    private val shadow = BufferedImage(cs, cs, TYPE_INT_ARGB).apply {
        val g = graphics
        g.color = Color(0, 0, 0, 0)
        g.fillRect(0, 0, cs, cs)
        g.color = Color.BLACK
        for (x in 0 until cs) {
            for (y in 0 until cs) {
                if ((x + y) % 2 == 0) {
                    g.drawLine(x, y, x, y)
                }
            }
        }
    }

    operator fun get(pos: Vector) = cells[pos.x][pos.y]
    operator fun get(x: Int, y: Int) = cells[x][y]

    var fogOfWar = true

    fun getTranslatedForDraw(p: Vector) = (p - cellTranslation) * cs
    fun getTranslatedForDraw(x: Int, y: Int) = getTranslatedForDraw(Vector(x, y))

    val winSizeInCells
        get() = Vector(
            (G.win.innerSize.x + cs - 1) / cs,
            (G.win.innerSize.y + cs - 1) / cs
        )

    fun paint(g: Graphics) {
        val curPlayerObs = G.curPlayer.observableArea
        cells.matrixForEachIndexed(
            cellTranslation, winSizeInCells
        ) { x, y, cell ->
            val (px, py) = getTranslatedForDraw(x, y)
            if (fogOfWar) {
                if (curPlayerObs[x][y] == ObservableStatus.NotInvestigated) {
                    g.color = Color.BLACK
                    g.fillRect(px, py, cs, cs)
                } else {
                    g.color = cell.type.color
                    g.fillRect(px, py, cs, cs)
                    if (curPlayerObs[x][y] == ObservableStatus.Investigated) {
                        g.drawImage(shadow, px, py, null)
                    }
                }
            } else {
                g.color = cell.type.color
                g.fillRect(px, py, cs, cs)
            }
        }
        cells.matrixForEachIndexed(
            cellTranslation, winSizeInCells
        ) { x, y, cell ->
            if (!fogOfWar || curPlayerObs[x][y] == ObservableStatus.Observable) {
                cell.unit?.paint(g)
                cell.build?.paint(g)
            }
        }
        g.color = Color(128, 128, 128, 128)
        for (i in 0..height) {
            g.drawLine(0, i * cs, width * cs, i * cs)
        }
        for (i in 0..width) {
            g.drawLine(i * cs, 0, i * cs, height * cs)
        }

        /**g.color = Color.pink
        getCellCircle(
        Vector(10, 10),
        round(G.win.mPos.distance(Vector(10 * cs + cs / 2, 10 * cs + cs / 2))/cs).toInt()
        ).forEachIndexed() { i, it->
        g.drawRect(it.x*cs+cs/4,it.y*cs+cs/4,cs/2,cs/2)
        g.drawString(i.toString(),it.x*cs+cs/4,it.y*cs+cs/4)
        }*/
    }

    fun mouseMoved(ev: MouseEvent) {
        val tmp = ev.pos / cs + cellTranslation
        if (inMap(tmp)) {
            selectedCellChanged = tmp != selectedCellPos
            selectedCellPos = tmp
        }
    }

    fun keyClicked(ev: KeyEvent) {
        when (ev.keyCode) {
            VK_F -> fogOfWar = !fogOfWar
        }
        /*val tmp = cellTranslation + when(ev.keyCode) {
            VK_UP->Direction.Up.offset
            VK_DOWN->Direction.Down.offset
            VK_LEFT->Direction.Left.offset
            VK_RIGHT->Direction.Right.offset
            else->Vector()
        }
        if(inMap(tmp)){
            cellTranslation = tmp
        }*/
    }

    fun keyPressed(ev: KeyEvent) {
        val tmp = cellTranslation + when (ev.keyCode) {
            VK_UP -> Direction.Up.offset
            VK_DOWN -> Direction.Down.offset
            VK_LEFT -> Direction.Left.offset
            VK_RIGHT -> Direction.Right.offset
            VK_W -> Direction.Up.offset
            VK_S -> Direction.Down.offset
            VK_A -> Direction.Left.offset
            VK_D -> Direction.Right.offset
            else -> Vector()
        }
        if (inMap(tmp) && inMap(tmp + winSizeInCells)) {
            cellTranslation = tmp
        }
    }

    /**
     * Истина, если после последнего движения мыши изменилась выбранная клетка
     */
    var selectedCellChanged: Boolean = false
        private set

    /**
     * Позиция выбранной клетки карты
     */
    var selectedCellPos: Vector = Vector(0, 0)
        private set

    /**
     * Выбранная клетка карты, над которой сейчас находится мышь
     */
    @Suppress("unused")
    val selectedCell
        get() = this[selectedCellPos]

    /**
     * Рисует заданный маршрут
     * @param g рисовалка
     * @param cur_ позиция откудова надо начинать рисовать
     * @param path путь, который надо нарисовать
     */
    fun drawPath(g: Graphics, cur_: Vector, path: MutableList<Direction>) {
        g.color = Color.BLACK
        var cur = cur_
        val lx = IntArray(path.size + 1) { 0 }
        lx[0] = cur.x + cs / 2
        val ly = IntArray(path.size + 1) { 0 }
        ly[0] = cur.y + cs / 2
        path.forEachIndexed { i, dir ->
            cur += dir.offset * cs
            lx[i + 1] += cur.x + cs / 2
            ly[i + 1] += cur.y + cs / 2
        }
        /*for (d in path) {
            g.drawLine(
                cur.x + cs / 2,
                cur.y + cs / 2,
                cur.x + d.offset.x * cs + cs / 2,
                cur.y + d.offset.y * cs + cs / 2
            )
            cur += dir.offset * cs
        }*/
        g.drawPolyline(lx, ly, lx.size)
    }

    /**
     * Проверяет, находится ли данная позиция внутри карты
     */
    fun inMap(v: Vector) = v.x in 0 until width && v.y in 0 until height

    /**
     * Флаги для алгоритма A*
     */
    enum class AStarF { No, Visit, Complete }

    /**
     * Клетки для алгоритма A*
     */
    @Suppress("EqualsOrHashCode")
    data class AStarC(
        var flag: AStarF,
        var l: Int,
        var el: Int,
        val pos: Vector,
        var from: Direction = Direction.Up,
        var gen: Int = 0,
    ) {
        override fun equals(other: Any?) = this.pos == (other as AStarC).pos
    }

    private val fCells = makeMatrix(size) { AStarC(AStarF.No, 0, 0, it) }

    /**
     * Ищет маршрут от начальной точки beg до конечной точки end, для определения
     * доступных клеток использует метод allow
     * @param beg   начало пути
     * @param end   конец пути
     * @param allow функций, определяющая можно ли ходить по данной клетке
     * @return список направлений по которым нужно двигаться что бы попасть из beg в end
     */
    fun aStar(beg: Vector, end: Vector, allow: (Cell) -> Boolean): MutableList<Direction> {
        if (!allow(cells[end]) || !inMap(beg) || !inMap(end)) {
            return mutableListOf()
        }
        fCells.matrixForEachIndexed { p, _ ->
            fCells[p].flag = AStarF.No
            fCells[p].l = 0
            fCells[p].el = 0

        }
        fCells[beg].flag = AStarF.Complete
        val visitedCells = SortedArrayList<AStarC>(
            beg.cellDistance(end) * 3 / 2
        ) { l, r ->
            val tmp = (r.l + r.el) - (l.l + l.el)
            if (tmp == 0) r.pos.comp(l.pos) else tmp
        }
        var cur = beg
        var find = true
        while (fCells[end].flag != AStarF.Complete && find) {
            for (dir in Direction.values()) {
                val ncp = cur + dir.offset
                if (inMap(ncp) &&
                    allow(cells[ncp]) &&
                    fCells[ncp].flag == AStarF.No
                ) {
                    fCells[ncp].flag = AStarF.Visit
                    fCells[ncp].l = fCells[cur].l + cells[ncp].type.movePointCost
                    fCells[ncp].el = ncp.cellDistance(end)
                    fCells[ncp].from = dir.oposite
                    fCells[ncp].gen = fCells[cur].gen + 1
                    visitedCells.add(fCells[ncp])
                }
            }
            find = !visitedCells.isEmpty()
            if (find) {
                cur = visitedCells.takeLast().pos
                fCells[cur].flag = AStarF.Complete
            }
        }
        if (fCells[end].flag != AStarF.Complete) {
            return ArrayList()
        }
        val path = ArrayList<Direction>(fCells[end].gen)
        while (cur != beg) {
            path.add(fCells[cur].from.oposite)
            cur += fCells[cur].from.offset
        }
        path.reverse()
        return path
    }

    fun generateMapByTwoPoints(
        p0: Vector,
        p0Power: Int,
        p1: Vector,
        p1Power: Int,
        minPower: Int
    ) {
        val l = getCellLine(p0, p1)
        val s = l.size.toDouble()
        val m = s / 2
        val a = (p0Power + p1Power - 2 * minPower).toDouble() / (s * s - 2 * m * m)
        val b = -minPower * 2 * a
        val c = p0Power

        l.forEachIndexed { i, p ->
            val power = round(a * i * i + b * i + c).toInt()
            Direction.values().forEach { dir ->
                val dirPower = power + Random.nextInt(-1, 1)
                for (t in 0..dirPower) {
                    if (inMap(p + dir.offset * t)) {
                        cells[p + dir.offset * t].type +=
                            if (Random.nextInt(1, 10) == 1) 0 else 1
                    }
                }
            }
        }
    }
}