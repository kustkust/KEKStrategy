import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import kotlin.math.abs

class Mine(p: Vector) : BaseBuild(p) {
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

    override fun endTurn() {
        //TO DO("Not yet implemented")
    }

    override fun newTurn() {
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

    override fun iterateInvestigatedArea(iter: (pos: Vector) -> Unit) {
        for (i in -2..2) {
            for (j in -2 + abs(i)..2 - abs(i)) {
                val dp = pos + Vector(i, j)
                if (G.map.inMap(dp)) {
                    iter(dp)
                }
            }
        }
    }

    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost

    object Factory : BaseFactory {
        override fun createEntity(pos: Vector) = Mine(pos)

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

    }
}