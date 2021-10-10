import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

abstract class BaseEntity(var pos: Vector = Vector(0, 0)) {
    lateinit var owner: Player

    var maxHp = 10
    var curHp = 10


    private val id = idCounter++

    companion object {
        private var idCounter = 0
    }

    abstract fun paint(g: Graphics)
    abstract fun endTurn()
    abstract fun newTurn()
    abstract fun selfCheck()
    abstract fun mouseClicked(ev: MouseEvent)
    abstract fun mouseMoved(ev: MouseEvent)
    abstract fun keyClicked(ev: KeyEvent)

    abstract val observableArea: Matrix<ObservableStatus>
    abstract val allowedCells: MutableList<Cell.Type>
    abstract val cost: Cost

    val onCell get() = G.map[pos]

    override operator fun equals(other: Any?) = id == (other as BaseEntity).id
    override fun hashCode(): Int {
        var result = pos.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + id
        result = 31 * result + maxHp
        result = 31 * result + curHp
        result = 31 * result + allowedCells.hashCode()
        return result
    }
}