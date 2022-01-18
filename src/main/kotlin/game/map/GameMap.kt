package game.map

import game.Direction
import game.G
import game.Player
import game.entities.builds.Barracks
import game.entities.BaseEntity
import game.entities.units.MeleeUnit
import game.entities.builds.PlayerBase
import kotlinx.serialization.Transient
import utility.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.*
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

//@Serializable
class GameMap {
    var width: Int
        get() = cells.width
        set(value) = cells.changeMatrixSize(value, cells.height) { x, y -> Cell(Vector(x, y), Cell.Type.Water) }
    var height: Int
        get() = cells.height
        set(value) = cells.changeMatrixSize(cells.width, value) { x, y -> Cell(Vector(x, y), Cell.Type.Water) }

    /**
     * Размер карты в клетках
     */
    var size
        get() = Vector(width, height)
        set(value) = cells.changeMatrixSize(value.x, value.y) { x, y -> Cell(Vector(x, y), Cell.Type.Water) }

    /**
     * Клетки карты
     */
    lateinit var cells: Matrix<Cell>

    var cellTranslation = Vector(0, 0)
        set(newT) {
            if (newT.x < 0) newT.x = 0
            else if (newT.x + winSizeInCells.x > width) newT.x = width - winSizeInCells.x
            if (newT.y < 0) newT.y = 0
            else if (newT.y + winSizeInCells.y > height) newT.y = height - winSizeInCells.y

            selectedCellPos += newT - field
            field = newT
            cellTranslationChanged(field)
        }
    val cellTranslationChanged = Event1<Vector>()

    /**
     * Размер одной клетки в пикселях
     */
    val cs
        get() = C.cs

    /**
     * Текстура для рисования тумана войны
     */
    @Transient
    val shadow = BufferedImage(cs, cs, TYPE_INT_ARGB).apply {
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

    fun getCell(x: Int, y: Int) = if (inMap(Vector(x, y))) get(x, y) else null
    fun getCell(pos: Vector) = if (inMap(pos)) get(pos) else null

    fun upOf(pos: Vector) = getCell(pos + Vector.Up)
    fun downOf(pos: Vector) = getCell(pos + Vector.Down)
    fun leftOf(pos: Vector) = getCell(pos + Vector.Left)
    fun rightOf(pos: Vector) = getCell(pos + Vector.Right)

    @Transient
    var fogOfWar = true

    @Transient
    var showGrid = true

    @Transient
    var showPerlin = false

    fun getTranslatedForDraw(p: Vector) = (p - cellTranslation) * cs
    fun getTranslatedForDraw(x: Int, y: Int) = getTranslatedForDraw(Vector(x, y))

    val winSizeInCells
        get() = Vector(
            min((G.win.innerSize.x + cs - 1) / cs, width),
            min((G.win.innerSize.y + cs - 1) / cs, height)
        )

    private fun setAnimation() {
        cells.matrixForEachIndexed { _, c ->
            c.setAnimation()
        }
    }

    fun paint(g: Graphics) {
        //Рисуем клетки
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
                    cell.paint(g)
                    if (curPlayerObs[x][y] == ObservableStatus.Investigated) {
                        cell.buildsShadow[G.curPlayer]?.paint(g, Vector(px, py))
                        cell.unitsShadow[G.curPlayer]?.paint(g, Vector(px, py))
                        g.drawImage(shadow, px, py, null)
                    }
                }
            } else {
                cell.paint(g)
            }
        }

        //Рисуем сетку
        if (showGrid) {
            g.color = Color(128, 128, 128, 128)
            for (i in 0..height) {
                g.drawLine(0, i * cs, width * cs, i * cs)
            }
            for (i in 0..width) {
                g.drawLine(i * cs, 0, i * cs, height * cs)
            }
        }

        //Рисуем здания
        cells.matrixForEachIndexed(
            cellTranslation, winSizeInCells
        ) { x, y, cell ->
            if (!fogOfWar || curPlayerObs[x][y] == ObservableStatus.Observable) {
                cell.build?.paint(g)
            }
        }

        //Рисуем маршруты
        G.curPlayer.selectedUnit?.let { unit ->
            g.color = Color.gray
            val g2 = g as Graphics2D
            val bs = g2.stroke
            g2.stroke = BasicStroke(3f)
            G.map.drawPath(g2, unit.pos, unit.path)
            g2.stroke = bs
            val beg = if (G.win.isControlDown) unit.curDist else unit.pos
            val mp = G.map.selectedCellPos
            G.curPlayer.tmpPath = if (G.win.isControlDown) {
                G.map.aStar(beg, mp) { unit.canMoveTo(it) }
            } else {
                G.map.aStar(beg, mp) { unit.canMoveTo(it) }
            }
            G.curPlayer.tmpPath?.let {
                g.color = Color.white
                G.map.drawPath(g, beg, it)
            }
        }

        //Рисуем юнитов
        cells.matrixForEachIndexed(
            cellTranslation, winSizeInCells
        ) { x, y, cell ->
            if (!fogOfWar || curPlayerObs[x][y] == ObservableStatus.Observable) {
                cell.unit?.paint(g)
            }
        }

        //Рисуем рамку выбора
        G.curPlayer.selectedEntity?.let {
            g.color = Color.BLACK
            g.drawRect(it.paintPos.x + 1, it.paintPos.y + 1, cs - 2, cs - 2)
            it.paintInterface(g)
        }
    }

    /**
     * Рисует заданный маршрут
     * @param g рисовалка
     * @param cur_ позиция откудова надо начинать рисовать
     * @param path путь, который надо нарисовать
     */
    private fun drawPath(g: Graphics, cur_: Vector, path: MutableList<Direction>) {
        var cur = getTranslatedForDraw(cur_)
        val lx = IntArray(path.size + 1) { 0 }
        lx[0] = cur.x + cs / 2
        val ly = IntArray(path.size + 1) { 0 }
        ly[0] = cur.y + cs / 2
        path.forEachIndexed { i, dir ->
            cur += dir.offset * cs
            lx[i + 1] += cur.x + cs / 2
            ly[i + 1] += cur.y + cs / 2
        }
        g.drawPolyline(lx, ly, lx.size)
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
            VK_G -> showGrid = !showGrid
            VK_P -> showPerlin = !showPerlin
        }
    }

    fun keyPressed(ev: KeyEvent) {
        val d = when (ev.keyCode) {
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
        cellTranslation += d
    }

    /**
     * Истина, если после последнего движения мыши изменилась выбранная клетка
     */
    @Transient
    var selectedCellChanged: Boolean = false
        private set

    /**
     * Позиция выбранной клетки карты
     */
    @Transient
    var selectedCellPos: Vector = Vector(0, 0)
        private set

    /**
     * Выбранная клетка карты, над которой сейчас находится мышь
     */
    val selectedCell
        get() = this[selectedCellPos]

    /**
     * Проверяет, находится ли данная позиция внутри карты
     */
    fun inMap(v: Vector) =
        v.x in 0 until width && v.y in 0 until height

    operator fun contains(pos: Vector) = inMap(pos)

    fun centerOn(pos: Vector) {
        val p = pos - winSizeInCells / 2
        if (p.x < 0) p.x = 0
        if (p.y < 0) p.y = 0
        if (p.x + winSizeInCells.x > size.x) p.x = p.x + winSizeInCells.x
        if (p.y + winSizeInCells.y > size.y) p.y = p.y + winSizeInCells.y
        cellTranslation = p
    }

    fun centerOn(e: BaseEntity) = centerOn(e.pos)

    /*Алгоритм поиска пути*/

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

    private val fCells: Matrix<AStarC> by lazy {
        makeMatrix(size) { AStarC(AStarF.No, 0, 0, it) }
    }

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
            beg.cellDistance(end) * 5 / 2
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
                    fCells[ncp].l = fCells[cur].l + cells[ncp].movePointCost
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

    /*Загрузка карты*/

    fun initPLayers() {
        val p1 = Vector(2, 2)
        //val p2 = Vector(width - 2, height - 2)
        val p2 = Vector(4, 4)

        G.players = arrayOf(
            Player("1").apply {
                color = Color.red
                PlayerBase(this, p1)
                MeleeUnit(this, p1 + Vector.Down)
                Barracks(this, p1 + Vector.Right)
            },
            Player("2").apply {
                color = Color.orange
                PlayerBase(this, p2)
                MeleeUnit(this, p2 + Vector.Up)
                Barracks(this, p2 + Vector.Left)
            }
        )
    }

    /*Генерация карты*/

    fun generateMap(width_: Int, height_: Int) {
        cells = makeMatrix(Vector(width_, height_)) { pos -> Cell(pos) }

        val p1 = Vector(2, 2)
        val p2 = Vector(width - 2, height - 2)

        //generateMapByTwoPoints(p1, 4, p2, 2, 1)
        generateMapByPerlin(3)
        setAnimation()

        initPLayers()
    }

    fun generateEmptyMap(width_: Int, height_: Int) {
        cells = makeMatrix(Vector(width_, height_)) { pos -> Cell(pos) }
    }

    private fun generateMapByTwoPoints(
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
                        cells[p + dir.offset * t].type = Cell.Type.Ground
                        //if (Random.nextInt(1, 10) == 1) 0 else 1
                    }
                }
            }
        }
    }

    private fun generateMapByPerlin(step: Int) {
        val perlinSize = size / step + Vector.DownRight
        if (size.x % step != 0) perlinSize.x += 1
        if (size.y % step != 0) perlinSize.y += 1
        val perlinGrid = makeMatrix(perlinSize) {
            VectorR.byAngleAndRadius(2 * kotlin.math.PI * Random.nextFloat(), 1.0)
        }
        val norm = sqrt(2.0) * step / 2
        val cellTypes = Cell.Type.values()
        cells.matrixForEachIndexed { p, c ->
            val rPos = VectorR(p.x + 0.5, p.y + 0.5)
            val perlinPos = p / step
            val perlinRPos = (perlinPos * step).toVectorR()
            val t = (rPos - perlinRPos) / step

            val lt = (rPos - perlinRPos) * perlinGrid[perlinPos]
            val lb = (rPos - (perlinRPos + VectorR(0, step))) * perlinGrid[perlinPos + Vector.Down]
            val l = lt + t.y * (lb - lt)

            val rt = (rPos - (perlinRPos + VectorR(step, 0))) * perlinGrid[perlinPos + Vector.Right]
            val rb = (rPos - (perlinRPos + VectorR(step, step))) * perlinGrid[perlinPos + Vector.DownRight]
            val r = rt + t.y * (rb - rt)

            val rawNoise = l + t.x * (r - l)
            val normaliseNoise = (rawNoise + norm) / norm / 2 //должно быть от 0 до 1
            cells[p].noise = normaliseNoise
            val weightedMode = true
            if(weightedMode) {
                val chanceSum = cellTypes.sumOf { it.chance }
                var weightNoise = normaliseNoise * chanceSum
                for (type in cellTypes) {
                    if (weightNoise < type.chance) {
                        cells[p].type = type
                        break
                    }
                    weightNoise -= type.chance
                }
            } else {
                var cellType = floor(normaliseNoise * cellTypes.size).toInt()
                if (cellType >= cellTypes.size) cellType = cellTypes.size - 1
                cells[p].type = cellTypes[cellType]
            }
        }
    }
}