import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class Mine(p: Vector) : BaseBuild(p) {
    override fun paint(g: Graphics) {
        //TODO("Not yet implemented")
    }

    override fun endTurn() {
        //TODO("Not yet implemented")
    }

    override fun newTurn() {
        owner.changeResource(ResourceType.Stone, 4)
    }

    override fun mouseClicked(ev: MouseEvent) {
        //TODO("Not yet implemented")
    }

    override fun mouseMoved(ev: MouseEvent) {
        TODO("Not yet implemented")
    }

    override fun keyClicked(ev: KeyEvent) {
        //TODO("Not yet implemented")
    }

    override val observableArea: Matrix<ObservableStatus>
        get() = makeMatrix(G.map.size) {
            if (pos.cellDistance(it) < 2) {
                ObservableStatus.Observable
            } else {
                ObservableStatus.Investigated
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
                intArrayOf(cs / 2, 2, cs - 2),
                intArrayOf(2, cs - 2, cs - 2),
                3
            )
        }

        override val cost: Cost = mapOf(
            ResourceType.Gold to 10,
            ResourceType.Tree to 10
        )

        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Mountain)

    }
}