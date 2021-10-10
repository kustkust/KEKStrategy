import java.awt.Color
import java.awt.Graphics

class GameMap(var width: Int = 20, var height: Int = 20) {
    /**
     * Размер карты, можно изменить после создания карты но лучше не надо
     */
    var size
        get() = Vector(width, height)
        set(value) {
            width = value.x
            height = value.y
        }

    /**
     * Клетки карты
     */
    var cells = Array(width) { Array(height) { Cell() } }

    /**
     * Размер одной клетки в пикселях
     */
    val cs = 30

    operator fun get(pos: Vector) = cells[pos.x][pos.y]
    operator fun get(x: Int, y: Int) = cells[x][y]

    fun paint(g: Graphics) {
        G.curPlayer.updateObservableArea()
        val curPlayerObs = G.curPlayer.observableArea
        cells.matrixForEachIndexed { x, y, cell ->
            if (curPlayerObs[x][y] == ObservableStatus.NotInvestigated) {
                g.color = Color.BLACK
                g.fillRect(x * cs, y * cs, cs, cs)
            } else {
                g.color = cell.type.color
                g.fillRect(x * cs, y * cs, cs, cs)
                if (curPlayerObs[x][y] == ObservableStatus.Investigated) {
                    g.color = Color(0, 0, 0, 128)
                    g.fillRect(x * cs, y * cs, cs, cs)
                } else {
                    cell.unit?.paint(g)
                    cell.build?.paint(g)
                }
            }
        }
        g.color = Color(128, 128, 128, 128)
        for (i in 0..height) {
            g.drawLine(0, i * cs, width * cs, i * cs)
        }
        for (i in 0..width) {
            g.drawLine(i * cs, 0, i * cs, height * cs)
        }
        if (G.curPlayer.selectedUnit != null) {
            val pos = G.curPlayer.selectedUnit!!.pos
            g.color = Color.BLACK
            g.drawRect(pos.x * cs + 1, pos.y * cs + 1, cs - 2, cs - 2)
            if (G.curPlayer.selectedUnit?.path != null) {
                drawPath(g, G.curPlayer.selectedUnit?.pos!!, G.curPlayer.selectedUnit?.path!!)
            }
        }
        if (G.curPlayer.selectedBuild != null) {
            val pos = G.curPlayer.selectedBuild!!.pos
            g.color = Color.BLACK
            g.drawRect(pos.x * cs + 1, pos.y * cs + 1, cs - 2, cs - 2)
        }
    }

    /**
     * Рисует заданный маршрут
     * @param g рисовалка
     * @param cur_ позиция откудова надо начинать рисовать
     * @param path путь, который надо нарисовать
     */
    fun drawPath(g: Graphics, cur_: Vector, path: MutableList<Direction>) {
        g.color = Color.BLACK
        var cur = cur_
        for (d in path) {
            g.drawLine(
                cur.x * cs + cs / 2,
                cur.y * cs + cs / 2,
                (cur.x + d.offset.x) * cs + cs / 2,
                (cur.y + d.offset.y) * cs + cs / 2
            )
            cur += d.offset
        }
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
    data class AStarC(var flag: AStarF, var l: Float, var el: Float, var from: Direction = Direction.Up)

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
        val fcells = Array(width) { Array(height) { AStarC(AStarF.No, 0f, 0f) } }
        fcells[beg].flag = AStarF.Complete
        var cur = beg
        var find = true
        while (fcells[end].flag != AStarF.Complete && find) {

            for (dir in Direction.values()) {
                val ncp = cur + dir.offset
                if (!inMap(ncp) ||
                    !allow(cells[ncp]) || cells[ncp].unit != null ||
                    fcells[ncp].flag == AStarF.Complete
                ) {
                    continue
                }
                fcells[ncp].flag = AStarF.Visit
                fcells[ncp].l = fcells[cur].l + cells[ncp].type.movePointCost
                fcells[ncp].el = ncp.distance(end)
                fcells[ncp].from = dir.oposite
            }

            var minL = Float.MAX_VALUE
            find = false
            fcells.matrixForEachIndexed { x, y, cf ->
                if (cf.flag == AStarF.Visit && cf.l + cf.el < minL) {
                    minL = cf.l + cf.el
                    cur = Vector(x, y)
                    find = true
                }
            }
            fcells[cur].flag = AStarF.Complete
        }
        val path = mutableListOf<Direction>()
        if (!find) {
            return path
        }
        while (cur != beg) {
            path.add(fcells[cur].from.oposite)
            cur += fcells[cur].from.offset
        }
        //path.add(cur)
        path.reverse()
        return path
    }
}