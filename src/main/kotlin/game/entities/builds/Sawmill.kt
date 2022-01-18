package game.entities.builds

import game.G
import game.Player
import game.ResourceType
import game.entities.BaseFactory
import game.makeCost
import game.map.Cell
import graphics.Animation
import utility.Vector
import utility.pos
import java.awt.Color
import java.awt.event.MouseEvent

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

    override fun mouseClicked(ev: MouseEvent) {
        if (pos.cellDistance(G.map.selectedCellPos) < 3 && G.map.selectedCell.type == Cell.Type.Forest) {
            owner.changeResource(ResourceType.Tree, 10)
            G.map.selectedCell.type = Cell.Type.Ground
        }
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Sawmill(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override val entityName = Sawmill::class.simpleName ?: ""
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