package game.entities.units

import game.map.Cell
import game.Player
import game.ResourceType
import game.entities.BaseEntity
import game.entities.BaseFactory
import game.entities.builds.Wall
import graphics.Animation
import utility.Vector
import java.awt.Color
import kotlin.math.absoluteValue

class Gnom(owner: Player, pos: Vector) : BaseUnit(owner, pos) {
    var damage = Factory.baseDamage

    override fun attack(entity: BaseEntity) {
        if (attackRem > 0) {
            attackRem--
            val curDamage = if(onCell.type == Cell.Type.Mountain) damage * 2 else damage
            entity.curHp -= curDamage
            remMovePoints = 0
            entity.selfCheck(this)
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

    override val factory get() = Factory

    object Factory: BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Gnom(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override fun getPreview(color: Color, scale: Int) =
            super.getPreview(color, scale).apply { curTagName = "IDL" }

        override val entityName = Gnom::class.simpleName ?: ""
        const val maxLVL = 3

        const val baseDamage = 3

        override val cost = mapOf(ResourceType.Gold to 5)
        override var allowedCells = mutableListOf(
            Cell.Type.Ground,
            Cell.Type.Forest,
            Cell.Type.Mountain,
            Cell.Type.Hills,
        )
        override val maxHP: Int = 10
        override val requiredTechnology: String = "GnomTech"
    }
}