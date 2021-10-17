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

    val paintPos: Vector
        get() = (pos - G.map.cellTranslation) * G.map.cs

    /**
     * Рисует сущность на карте
     */
    abstract fun paint(g: Graphics)

    /**
     * Вызывается в начале хода владельца
     */
    abstract fun newTurn()

    /**
     * Вызывается в конце хода владельца
     */
    abstract fun endTurn()

    /**
     * Проверяет себя, например жива ли сущность и если нет, то удаляется
     */
    abstract fun selfCheck()

    /**
     * Обработка нажатий клавиш мыши
     */
    abstract fun mouseClicked(ev: MouseEvent)

    /**
     * Обработка движений мыши
     */
    abstract fun mouseMoved(ev: MouseEvent)

    /**
     * Обработка нажатий клавиш клавиатуры
     */
    abstract fun keyClicked(ev: KeyEvent)

    /**
     * Последовательно вызывает функцию iter для каждой клетки, которую видит сущность
     */
    abstract fun iterateInvestigatedArea(iter: (pos: Vector) -> Unit)

    /**
     * Обновляет территорию, которую открыл владелец
     */
    open fun updateOwnerInvestigatedArea() =
        iterateInvestigatedArea { owner.investigatedArea[it] = ObservableStatus.Investigated }

    /**
     * Обновляет территорию, которую видит владелец
     */
    open fun updateOwnerObservableArea() =
        iterateInvestigatedArea { owner.observableArea[it] = ObservableStatus.Observable }

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