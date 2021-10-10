import java.awt.Graphics

/**
 * Базовый класс для создания сущностей, так же содержит некоторую общую информацию
 * для типа создаваемой сущности. Должен добавляться в качестве object к каждому юниту
 * и строению
 */
interface BaseFactory {
    /**
     * В производных классах должен создавать сущность определённого типа,
     * к примеру MeleeUnitFactory возвращает MeleeUnit
     */
    abstract fun createEntity(pos: Vector): BaseEntity

    /**
     * Рисует превью сущности для меню, к примеру меню создания юнитов
     */
    abstract fun paintPreview(g: Graphics)

    /**
     * Возвращает стоимость создаваемого объекта
     */
    abstract val cost: Cost

    /**
     * Возвращает список типов клеток на которых может находиться сущность
     */
    abstract var allowedCells: MutableList<Cell.Type>
}