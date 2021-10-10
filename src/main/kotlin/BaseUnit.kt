import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON3

abstract class BaseUnit(pos: Vector = Vector(0, 0)) : BaseEntity(pos) {
    var maxMovePoints = 2
    var remMovePoints = 2
    var maxAttackPerTurn = 1
    var attackRem = 1
    var path = mutableListOf<Direction>()

    abstract fun attack(entity: BaseEntity)
    abstract fun canAttack(entity: BaseEntity): Boolean
    abstract fun canMoveTo(cell: Cell): Boolean

    fun move(dir: Direction): Boolean {
        val newPos = pos + dir.offset
        val newCell = G.map[newPos]
        if (remMovePoints >= newCell.type.movePointCost && canMoveTo(newCell)) {
            G.map[pos].unit = null
            remMovePoints -= newCell.type.movePointCost
            pos = newPos
            newCell.unit = this
            owner.updateObservableArea(observableArea)
            return true
        }
        return false
    }

    override fun mouseMoved(ev: MouseEvent) {
        if (owner.selectedUnit == this) {
            val mp = ev.pos / G.map.cs
            val path: MutableList<Direction>
            val beg: Vector
            if (ev.isControlDown) {
                beg = curDist
                path = G.map.aStar(beg, mp) { canMoveTo(it) }
            } else {
                beg = pos
                path = G.map.aStar(beg, mp) { it.type in allowedCells }
            }
            G.drawTask += { G.map.drawPath(it, beg, path) }
        }
    }

    fun finishMove() {
        while (path.isNotEmpty() && move(path.first())) {
            path.removeAt(0)
        }
    }

    override fun endTurn() {
        finishMove()
    }

    override fun newTurn() {
        remMovePoints = maxMovePoints
        attackRem = maxAttackPerTurn
    }

    override fun selfCheck() {
        if (curHp <= 0) {
            owner.removeUnit(this)
        }
    }

    private fun buildPathTo(dist: Vector, add: Boolean = false) {
        if (add) {
            path += G.map.aStar(curDist, dist) { canMoveTo(it) }
        } else {
            path = G.map.aStar(pos, dist) { canMoveTo(it) }
        }
    }

    override fun mouseClicked(ev: MouseEvent) {
        if (ev.button == BUTTON3) {
            val p = Vector(ev.x, ev.y) / G.map.cs
            val u = G.map[p].unit
            val b = G.map[p].build
            if (u != null) {
                if (canAttack(u)) {
                    attack(u)
                    u.selfCheck()
                }
            } else if (b != null) {
                if (owner.own(b)) {
                    moveControl(ev)
                } else if (canAttack(b)) {
                    attack(b)
                    b.selfCheck()
                }
            } else {
                moveControl(ev)
            }
        }
    }

    fun moveControl(ev: MouseEvent) {
        val p = Vector(ev.x, ev.y) / G.map.cs
        buildPathTo(p, ev.isControlDown)
        if (ev.clickCount == 2) {
            finishMove()
        }
    }

    override fun keyClicked(ev: KeyEvent) {
        //TODO("Not yet implemented")
    }

    val curDist: Vector
        get() = path.fold(pos) { p, d -> p + d.offset }
}