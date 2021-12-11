package game.entities

import game.*
import graphics.Animation
import utility.Vector
import java.awt.Color
import java.awt.Graphics
import kotlin.math.absoluteValue

class MeleeUnit(owner: Player, pos: Vector = Vector()) : BaseUnit(owner, pos) {
    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost
    override val factory get() = Factory

    var damage = Factory.baseDamage

    override fun attack(entity: BaseEntity) {
        if (attackRem > 0) {
            attackRem--
            entity.curHp -= damage
            remMovePoints = 0
            entity.selfCheck(this)
            if (entity.isDead) {
                killCount++
                upgrade()
            }
        }
    }

    var killCount = 0
    var lvl = 1
    val maxLVL get() = Factory.maxLVL

    override fun upgrade() {
        if (lvl < maxLVL && killCount >= lvl) {
            lvl++
            damage += 1
        }
    }

    override fun canAttack(entity: BaseEntity): Boolean {
        val off = pos - entity.pos
        return owner != entity.owner && off.x.absoluteValue + off.y.absoluteValue <= 1
    }

    override fun canMoveTo(cell: Cell) = cell.type in allowedCells &&
            cell.unit == null &&
            (cell.build == null || owner own cell.build) &&
            cell.build !is Wall

    override fun paint(g: Graphics) {
        super.paint(g)
        val cs = G.map.cs
        val p = paintPos
        g.color = Color.black
        g.drawString(curHp.toString(), p.x + 1, p.y + g.font.size)
        g.drawString(remMovePoints.toString(), p.x + 1, p.y + cs - 1)
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = MeleeUnit(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override fun getPreview(color: Color) =
            super.getPreview(color).apply { curTagName = "IDL" }

        override val entityName = MeleeUnit::class.simpleName ?: ""
        const val maxLVL = 3

        const val baseDamage = 10

        override val cost = mapOf(ResourceType.Gold to 5)
        override var allowedCells = mutableListOf(
            Cell.Type.Ground,
            Cell.Type.Forest,
            Cell.Type.Mountain,
            Cell.Type.Hills
        )
        override val maxHP: Int = 10
        override val requiredTechnology: String = "game.entities.MeleeUnit"
    }
}