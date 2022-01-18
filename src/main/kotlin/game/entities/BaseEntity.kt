package game.entities

import game.*
import game.map.Cell
import game.map.ObservableStatus
import utility.*
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import kotlin.properties.Delegates

/**
 * Базовый класс для юнитов и построек
 */
abstract class BaseEntity(val owner: Player, var pos: Vector = Vector(0, 0)) {
    /**
     * Выбрана ли сущность владельцем
     */
    val selected get() = owner.selectedBuild == this

    open var selable = true

    /**
     * Максимальный запас здоровья сущности
     */
    var maxHp = factory.maxHP

    val curHpChanged = Event1<Int>()

    /**
     * Текущее здоровье сущности
     */
    var curHp: Int by Delegates.observable(maxHp) { _, _, new ->
        curHpChanged(new)
    }

    /**
     * Мертва ли сущность
     */
    open val isDead get() = curHp <= 0

    /**
     * Технология, требуемая для открытия данной сущности
     */
    private val requiredTech
        get() = factory.requiredTechnology?.let { owner.technologies[it] }

    /**
     * Открыта ли в дереве технологий данная сущность
     */
    val isOpen get() = requiredTech?.isOpen ?: true

    /**
     * Уникальный идентификатор
     */
    private val id = idCounter++

    companion object {
        private var idCounter = 0
    }

    /**
     * Положение левого верхнего угла сущности на экране
     */
    open val paintPos: Vector
        get() = (pos - G.map.cellTranslation) * C.cs

    var animation = G.animationManager.getAnimation(factory.entityName, owner.color)

    /**
     * Рисует сущность на карте
     */
    open fun paint(g: Graphics) {
        animation.paint(g, paintPos)
    }

    /**
     * Рисует интерфейс сущности, если она выбрана
     */
    open fun paintInterface(g: Graphics) {}

    /**
     * Вызывается в начале хода владельца
     */
    open fun newTurn() {
    }

    var isTurnEnded = false

    /**
     * Вызывается в конце хода владельца, возвращает истину, если сущность что то сделала
     */
    open fun endTurn() =
        false

    /**
     * Проверяет себя, например жива ли сущность и если нет, то удаляется
     */
    abstract fun selfCheck(from: BaseEntity? = null)

    open fun onSelected() {
        if (owner == G.curPlayer)
            G.win.gameInterfacePanel.setEntityDescription(this)
    }

    /**
     * При нажатии кнопки Q сущность может самостоятельно на неё отреагировать, в таком
     * случае функция возвращает false и сущность должна остаться выбранной, иначе
     * возвращает true
     */
    open fun onUnselected(): Boolean {
        if (owner == G.curPlayer) {
            G.win.gameInterfacePanel.setEmptyDescription()
        }
        return true
    }

    open fun onRemoving() {
        owner.updateObservableArea()
    }

    /**
     * Обработка нажатий клавиш мыши
     */
    open fun mouseClicked(ev: MouseEvent) {}

    open fun mousePressed(ev: MouseEvent) {}

    /**
     * Обработка движений мыши
     */
    open fun mouseMoved(ev: MouseEvent) {}

    /**
     * Обработка нажатий клавиш клавиатуры
     */
    open fun keyClicked(ev: KeyEvent) {}

    /**
     * Радиус, который видит сущность, расстояние по умолчанию считается как x+y
     */
    var observableRadius = 2

    /**
     * Высота сущности, нужна для расчёта видимой области
     */
    open var height = 0

    /**
     * Последовательно вызывает функцию iter для каждой клетки, которую видит сущность
     */
    open fun iterateInvestigatedArea(iter: (pos: Vector) -> Unit) =
        epsNei(observableRadius, pos) { invCellPos ->
            if (invCellPos in G.map) {
                val l = getCellLine(pos, invCellPos)
                val visible = if (l.size<2) {
                    true
                } else {
                    val mHeight = onCell.height + height
                    l.subList(1, l.size - 1).all { G.map[it].visibleHeight <= mHeight }
                }
                if (visible) {
                    iter(invCellPos)
                }
            }
        }

    /**
     * Обновляет территорию, которую открыл владелец
     */
    open fun updateOwnerInvestigatedArea() =
        iterateInvestigatedArea { owner.investigatedArea[it] = ObservableStatus.Investigated }


    /**
     * Прокачивает юнита, если это возможно
     */
    open fun upgrade() {}

    /**
     * Клетки на которых может находиться сущность
     */
    open val allowedCells: MutableList<Cell.Type> get() = factory.allowedCells

    /**
     * Стоимость сущности
     */
    open val cost: Cost get() = factory.cost

    /**
     * Фабрика для данной сущности
     */
    abstract val factory: BaseFactory

    /**
     * Клетка на которой находится сущность
     */
    val onCell get() = G.map[pos]

    override operator fun equals(other: Any?) =
        if (other != null) id == (other as BaseEntity).id else false

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