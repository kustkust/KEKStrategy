package game.entities

import game.*
import utilite.Vector
import utilite.makePolygon
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class Mine(owner: Player, pos: Vector = Vector(0, 0)) : BaseBuild(owner, pos) {
    override fun paint(g: Graphics) {
        val cs = G.map.cs
        val p = paintPos
        g.color = owner.color
        g.fillPolygon(
            makePolygon(
                p + Vector(2, cs / 2),
                p + Vector(cs - 2, cs / 2),
                p + Vector(cs - 2, cs - 2),
                p + Vector(cs * 2 / 3, cs - 2),
                p + Vector(cs * 2 / 3, cs * 3 / 4),
                p + Vector(cs / 3, cs * 3 / 4),
                p + Vector(cs / 3, cs - 2),
                p + Vector(2, cs - 2)
            )
        )
    }

    override val factory get() = Factory

    override fun newTurn() {
        super.newTurn()
        owner.changeResource(ResourceType.Stone, 4)
    }

    override fun mouseClicked(ev: MouseEvent) {
        //TO DO("Not yet implemented")
    }

    override fun mouseMoved(ev: MouseEvent) {
        //TO DO("Not yet implemented")
    }

    override fun keyClicked(ev: KeyEvent) {
        //TO DO("Not yet implemented")
    }

    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector) = Mine(owner, pos)

        override fun paintPreview(g: Graphics) {
            val cs = G.map.cs
            g.fillPolygon(
                makePolygon(
                    Vector(2, cs / 2),
                    Vector(cs - 2, cs / 2),
                    Vector(cs - 2, cs - 2),
                    Vector(cs * 2 / 3, cs - 2),
                    Vector(cs * 2 / 3, cs * 3 / 4),
                    Vector(cs / 3, cs * 3 / 4),
                    Vector(cs / 3, cs - 2),
                    Vector(2, cs - 2)
                )
            )
        }

        override val cost: Cost = mapOf(
            ResourceType.Gold to 10,
            ResourceType.Tree to 10,
        )

        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Mountain)
        override val maxHP: Int = 10
        override val requiredTechnology: String = "MineTech"
    }
}