package game.entities

import game.map.Cell
import game.Cost
import game.G
import game.Player
import graphics.Animation
import utility.Vector
import java.awt.Color

/**
 * Базовый класс для создания сущностей. Так же содержит некоторую общую информацию
 * для типа создаваемой сущности. Должен добавляться в качестве object к каждому юниту
 * и строению
 */
interface BaseFactory {
    /**
     * В производных классах должен создавать сущность определённого типа,
     * к примеру MeleeUnitFactory возвращает game.entities.units.MeleeUnit
     */
    fun createEntity(owner: Player, pos: Vector): BaseEntity

    val animationPreviewCash: MutableMap<Color, Animation>

    /**
     * Рисует превью сущности для меню, к примеру меню создания юнитов
     */
    fun getPreview(color: Color, scale: Int = 2) =
        animationPreviewCash.getOrPut(color) {
            val tmp = G.animationManager.getAnimation(entityName, color)
            tmp.run = false
            tmp
        }.apply { this.scale = scale }

    /**
     * Возвращает стоимость создаваемого объекта
     */
    val cost: Cost


    val entityName: String

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