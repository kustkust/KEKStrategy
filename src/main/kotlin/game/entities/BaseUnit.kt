package game.entities

import game.Cell
import game.Direction
import game.G
import game.Player
import utilite.Vector
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON3

abstract class BaseUnit(owner: Player, pos: Vector = Vector(0, 0)) :
    BaseEntity(owner, pos) {
    var maxMovePoints = 1000
    var remMovePoints = maxMovePoints
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
            onCell.unit = null
            remMovePoints -= newCell.type.movePointCost
            pos = newPos
            newCell.unit = this
            updateOwnerInvestigatedArea()
            return true
        }
        return false
    }

    override fun mouseMoved(ev: MouseEvent) {

    }

    fun finishMove(): Boolean {
        var makeTurn = false
        while (path.isNotEmpty() && move(path.first())) {
            makeTurn = true
            path.removeAt(0)
        }
        owner.updateObservableArea()
        return makeTurn
    }

    override fun endTurn(): Boolean {
        return finishMove()
    }

    override fun newTurn() {
        super.newTurn()
        remMovePoints = maxMovePoints
        attackRem = maxAttackPerTurn
    }

    override fun selfCheck(from: BaseEntity?) {
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
        when (ev.button) {
            BUTTON3 -> {
                val p = G.map.selectedCellPos
                val u = G.map[p].unit
                val b = G.map[p].build
                if (u != null) {
                    if (u == this) {
                        path.clear()
                    } else if (canAttack(u)) {
                        attack(u)
                    }
                } else if (b != null) {
                    if (owner.own(b)) {
                        moveControl(ev)
                    } else if (canAttack(b)) {
                        attack(b)
                    }
                } else {
                    moveControl(ev)
                }
            }
        }
    }

    private fun moveControl(ev: MouseEvent) {
        val p = G.map.selectedCellPos
        buildPathTo(p, ev.isControlDown)
        if (ev.clickCount == 2) {
            finishMove()
        }
    }

    override fun keyClicked(ev: KeyEvent) {
        //TO DO("Not yet implemented")
    }

    val curDist: Vector
        get() = path.fold(pos) { p, d -> p + d.offset }
}