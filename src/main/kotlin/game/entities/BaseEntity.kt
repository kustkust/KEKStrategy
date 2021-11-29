package game.entities

import utilite.Vector
import graphics.Animation
import game.*
import utilite.epsNei
import utilite.*
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

/**
 * Базовый класс для юнитов и построек
 */
abstract class BaseEntity(val owner: Player, var pos: Vector = Vector(0, 0)) {
    /**
     * Выбрана ли сущность владельцем
     */
    val selected get() = owner.selectedBuild == this

    /**
     * Максимальный запас здоровья сущности
     */
    var maxHp = factory.maxHP

    /**
     * Текущее здоровье сущности
     */
    var curHp = 10

    /**
     * Мертва ли сущность
     */
    open val isDead get() = curHp <= 0

    /**
     * Технология, требуемая для открытия данной сущности
     */
    val requiredTech get() = factory.requiredTechnology?.let { owner.technologies[it] }

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
    val paintPos: Vector
        get() = (pos - G.map.cellTranslation) * G.map.cs

    lateinit var animation: Animation

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
    open fun endTurn() = false

    /**
     * Проверяет себя, например жива ли сущность и если нет, то удаляется
     */
    abstract fun selfCheck(from: BaseEntity? = null)

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
     * Радиус, который видит сущность, расстояние по умолчанию считается как x+y
     */
    var observableradius = 2

    /**
     * Последовательно вызывает функцию iter для каждой клетки, которую видит сущность
     */
    open fun iterateInvestigatedArea(iter: (pos: Vector) -> Unit) =
        epsNei(observableradius, pos) { if (it in G.map) iter(it) }

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

    /**
     * Прокачивает юнита, если это возможно
     */
    open fun upgrade() {}

    /**
     * Клетки на которых может находиться сущность
     */
    abstract val allowedCells: MutableList<Cell.Type>

    /**
     * Стоимость сущности
     */
    abstract val cost: Cost

    /**
     * Фабрика для данной сущности
     */
    abstract val factory: BaseFactory

    /**
     * Клетка на которой находися сущность
     */
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