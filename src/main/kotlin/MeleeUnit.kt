import java.awt.Color
import java.awt.Graphics
import kotlin.math.absoluteValue

class MeleeUnit(pos: Vector = Vector()) : BaseUnit(pos) {
    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost

    override fun attack(entity: BaseEntity) {
        if (attackRem > 0) {
            attackRem--
            entity.curHp -= 3
            remMovePoints = 0
        }
    }

    override fun canAttack(entity: BaseEntity): Boolean {
        val off = pos - entity.pos
        return owner != entity.owner && off.x.absoluteValue + off.y.absoluteValue <= 1
    }

    override fun canMoveTo(cell: Cell) = cell.type in allowedCells &&
            cell.unit == null &&
            (cell.build == null || owner.own(cell.build!!))

    override fun paint(g: Graphics) {
        val cs = G.map.cs
        g.color = owner.color
        val p = pos * cs
        g.fillPolygon(
            intArrayOf(p.x + cs / 2, p.x + 2, p.x + cs - 2),
            intArrayOf(p.y + 2, p.y + cs - 2, p.y + cs - 2),
            3
        )
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)
        g.drawString(remMovePoints.toString(), p.x, p.y + cs)
    }

    override val observableArea: Matrix<ObservableStatus>
        get() = makeMatrix(G.map.size) {
            if (pos.cellDistance(it) < 2) {
                ObservableStatus.Observable
            } else {
                ObservableStatus.Investigated
            }
        }

    object Factory : BaseFactory {
        override fun createEntity(pos: Vector): BaseEntity = MeleeUnit(pos)
        override fun paintPreview(g: Graphics) {
            val cs = G.map.cs
            g.fillPolygon(
                intArrayOf(cs / 2, 2, cs - 2),
                intArrayOf(2, cs - 2, cs - 2),
                3
            )
        }

        override val cost: Map<ResourceType, Int> = mapOf(ResourceType.Gold to 5)
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
    }
}