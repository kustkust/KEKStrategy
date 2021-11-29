package game.entities

import game.Cell
import game.Cost
import game.Player
import utilite.Vector
import java.awt.Graphics

/**
 * Базовый класс для создания сущностей. Так же содержит некоторую общую информацию
 * для типа создаваемой сущности. Должен добавляться в качестве object к каждому юниту
 * и строению
 */
interface BaseFactory {
    /**
     * В производных классах должен создавать сущность определённого типа,
     * к примеру MeleeUnitFactory возвращает game.entities.MeleeUnit
     */
    fun createEntity(owner: Player, pos: Vector): BaseEntity

    /**
     * Рисует превью сущности для меню, к примеру меню создания юнитов
     */
    fun paintPreview(g: Graphics)

    /**
     * Возвращает стоимость создаваемого объекта
     */
    val cost: Cost

    /**
     * Возвращает список типов клеток на которых может находиться сущность
     */
    var allowedCells: MutableList<Cell.Type>

    /**
     * Максимальный запас здоровья сущности
     */
    val maxHP: Int

    /**
     * Требуемая для открытия технология, null, если не требуется технологий
     */
    val requiredTechnology: String?

    /**
     * Проверяет открыта ли требуемая технология у данного игрока
     */
    fun isOpen(player: Player) =
        requiredTechnology?.let { player.technologies[it]?.isOpen } ?: true
}