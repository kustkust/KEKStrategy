package game.entities.builds

import game.*
import game.entities.BaseFactory
import game.map.Cell
import graphics.Animation
import utility.Vector
import java.awt.Color

class Wall(
    owner_: Player, pos_: Vector,
) : BaseWall(owner_, pos_) {

    override val factory: BaseFactory
        get() = Factory

    override fun setupAnimation() {
        var tag = ""
        val set = { d: Vector, l: String ->
            val b = G.map.getCell(pos + d)?.build
            if (b is BaseWall) {
                tag += l
            }
        }
        set(Vector.Up, "U")
        set(Vector.Down, "D")
        set(Vector.Left, "L")
        set(Vector.Right, "R")
        if (tag.isEmpty()) tag = "W"
        animation.curTagName = tag
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Wall(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override fun getPreview(color: Color, scale: Int) =
            super.getPreview(color, scale).apply { curTagName = "LR" }

        override val entityName = Wall::class.simpleName ?: ""

        override val cost: Cost = makeCost(
            ResourceType.Gold to 2,
            ResourceType.Stone to 1,
            ResourceType.Tree to 1
        )
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(
            Cell.Type.Ground,
            Cell.Type.Hills,
            Cell.Type.Forest,
        )
        override val maxHP: Int = 30
        override val requiredTechnology = "WallTech"

    }
}