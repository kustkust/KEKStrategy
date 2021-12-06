package game.entities

import game.*
import graphics.Animation
import utility.Vector
import java.awt.Color

class Sawmill(owner: Player, pos: Vector) : BaseBuild(owner, pos) {
    override val factory get() = Factory

    override fun newTurn() {
        super.newTurn()
        owner.changeResource(ResourceType.Stone, 4)
    }

    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost
        get() = Factory.cost

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Sawmill(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override val entityName = Sawmill::class.simpleName?:""
        override val cost = makeCost(
            ResourceType.Gold to 10,
            ResourceType.Tree to 10,
        )

        override var allowedCells: MutableList<Cell.Type> = mutableListOf(
            Cell.Type.Forest,
            Cell.Type.Hills,
        )
        override val maxHP: Int = 10
        override val requiredTechnology: String = "SawmillTech"
    }
}