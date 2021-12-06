package game.entities

import game.*
import graphics.Animation
import utility.Vector
import utility.makePolygon
import java.awt.Color
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class Mine(owner: Player, pos: Vector) : BaseBuild(owner, pos) {
    override val factory get() = Factory

    init {
        animation = G.animationManager.getAnimation("Mine", owner.color)
    }

    override fun newTurn() {
        super.newTurn()
        owner.changeResource(ResourceType.Stone, 4)
    }

    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Mine(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override val entityName = Mine::class.simpleName?:""
        override val cost: Cost = makeCost(
            ResourceType.Gold to 10,
            ResourceType.Tree to 10,
        )

        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Mountain)
        override val maxHP: Int = 10
        override val requiredTechnology: String = "MineTech"
    }
}