package game.entities.builds

import game.*
import game.entities.*
import game.entities.units.BaseUnit
import game.entities.units.Gnom
import game.entities.units.MeleeUnit
import game.map.Cell
import graphics.Animation
import utility.Vector
import java.awt.Color
import java.awt.Graphics

class Barracks(owner: Player, pos: Vector = Vector(0, 0)) :
    BaseBuild(owner, pos) {
    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost
    override val factory get() = Factory

    private var maxSpawnPerTurn = 1
    private var curSpawned = 1
    val unitsList = ArrayList(Factory.creatableUnits)

    fun spawnUnit(i: Int) {
        if (curSpawned > 0 &&
            onCell.unit == null &&
            unitsList[i].isOpen(owner) &&
            owner.pay(unitsList[i].cost)
        ) {
            owner.addUnit(unitsList[i].createEntity(owner, pos) as BaseUnit)
            curSpawned--
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val p = paintPos
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)
    }

    override fun newTurn() {
        super.newTurn()
        curSpawned = maxSpawnPerTurn
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector): BaseEntity = Barracks(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override val entityName = Barracks::class.simpleName ?: ""

        val creatableUnits = mutableListOf<BaseFactory>(MeleeUnit.Factory, Gnom.Factory)
        override val cost: Map<ResourceType, Int> = mapOf(ResourceType.Gold to 5)
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
        override val maxHP: Int = 10
        override val requiredTechnology: String = "game.entities.units.MeleeUnit"
    }
}