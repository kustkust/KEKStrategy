package game.entities

import game.*
import graphics.Animation
import utility.Vector
import java.awt.Color

class Gate(owner_: Player, pos_: Vector) : BaseBuild(owner_, pos_) {
    override val factory
        get() = Factory

    init {
        setupAnimation()
        setupNeiAnimation()
    }

    private val BaseBuild.isWOrG
        get() = this is Wall || this is Gate

    fun setupAnimation() {
        animation.curTagName =
            if (G.map.upOf(pos)?.build?.isWOrG == true ||
                G.map.downOf(pos)?.build?.isWOrG == true
            )
                "UD"
            else "LR"
    }

    fun setupNeiAnimation() {
        val set = { d: Vector ->
            val b = G.map.getCell(pos + d)?.build
            if (b is Wall) {
                b.setupAnimation()
            }
            if (b is Gate) {
                b.setupAnimation()
            }
        }
        set(Vector.Up)
        set(Vector.Down)
        set(Vector.Left)
        set(Vector.Right)
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Gate(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()
        override fun getPreview(color: Color) =
            super.getPreview(color).apply { curTagName = "LR" }

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