package game.entities.builds

import game.*
import game.entities.BaseFactory
import game.map.Cell
import graphics.Animation
import utility.Vector
import java.awt.Color

class Gate(owner_: Player, pos_: Vector) : BaseWall(owner_, pos_) {
    override val factory
        get() = Factory

    override fun setupAnimation() {
        animation.curTagName =
            if (G.map.leftOf(pos)?.build is BaseWall && G.map.rightOf(pos)?.build is BaseWall) "LR"
            else if (G.map.upOf(pos)?.build is BaseWall && G.map.downOf(pos)?.build is BaseWall) "UD"
            else if (G.map.leftOf(pos)?.build is BaseWall || G.map.rightOf(pos)?.build is BaseWall) "LR"
            else "UD"
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Gate(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()
        override fun getPreview(color: Color, scale: Int) =
            super.getPreview(color, scale).apply { curTagName = "LR" }

        override val entityName = Gate::class.simpleName ?: ""
        override val cost = makeCost(
            ResourceType.Gold to 10,
            ResourceType.Stone to 20,
            ResourceType.Tree to 20,
        )
        override var allowedCells = mutableListOf(
            Cell.Type.Ground,
            Cell.Type.Hills,
        )
        override val maxHP: Int = 10
        override val requiredTechnology: String = "WallTech"

    }
}