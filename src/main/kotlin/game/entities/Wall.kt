package game.entities

import game.*
import graphics.Animation
import utility.Vector
import java.awt.Color

class Wall(
    owner_: Player, pos_: Vector,
) : BaseBuild(owner_, pos_) {

    override val factory: BaseFactory
        get() = Factory

    init {
        setupAnimation()
        setupNeiAnimation()
    }

    private val BaseBuild.isWOrG
        get() = this is Wall || this is Gate

    fun setupAnimation() {
        var tag = ""
        val set = { d: Vector, l: String ->
            val b = G.map.getCell(pos + d)?.build
            if (b?.isWOrG == true) {
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
        override fun createEntity(owner: Player, pos: Vector) = Wall(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override fun getPreview(color: Color) =
            super.getPreview(color).apply { curTagName = "LR" }

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