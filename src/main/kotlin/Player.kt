import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_Q
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1
import java.awt.event.MouseEvent.BUTTON3

class Player(val name: String) {
    /**
     * Список юнитов, принадлежащих игроку
     */
    val units = mutableListOf<BaseUnit>()

    /**
     * Список зданий, принадлежащих игроку
     */
    val builds = mutableListOf<BaseBuild>()

    /**
     * Список сущностей, принадлежащих игроку, объединяет список юнитов и зданий
     */
    val entities: List<BaseEntity>
        get() = List(units.size + builds.size) {
            if (it < units.size) {
                units[it]
            } else {
                builds[it - units.size]
            }
        }

    /**
     * Ресурсы игрока
     */
    val resource = mutableMapOf<ResourceType, Int>().apply {
        ResourceType.values().forEach { this[it] = 10 }
    }

    fun changeResource(r: ResourceType, value: Int) {
        resource[r] = resource[r]!! + value
    }

    /**
     * Выбранный юнит. Если выбран не юнит или выбрано здание, то возвращает null
     */
    var selectedUnit: BaseUnit?
        set(value) {
            selectedEntity = value
        }
        get() = if (selectedEntity is BaseUnit) selectedEntity as BaseUnit else null

    /**
     * Выбранное здание. Если здание не выбрано или выбран юнит, то возвращает null
     */
    var selectedBuild: BaseBuild?
        set(value) {
            selectedEntity = value
        }
        get() = if (selectedEntity is BaseBuild) selectedEntity as BaseBuild else null

    var selectedEntity: BaseEntity? = if (entities.isNotEmpty()) entities[0] else null

    /**
     * Цвет игрока, отражается на юнитах и зданиях
     */
    var color: Color = Color.RED

    /**
     * Обработка нажатий клавиш мыши
     */
    fun mouseClicked(ev: MouseEvent) {
        var doSomething = false
        when (ev.button) {
            BUTTON1 -> {
                val pos = G.map.selectedCellPos
                val u = G.map[pos].unit
                val b = G.map[pos].build
                if (own(u) && own(b)) {
                    if (selectedUnit == u) {
                        selectedBuild = b
                    } else {
                        selectedUnit = u
                    }
                    doSomething = true
                } else if (own(u)) {
                    selectedUnit = u
                    doSomething = true
                } else if (own(b)) {
                    selectedBuild = b
                    doSomething = true
                }
            }
            BUTTON3 -> {
                tmpPath = null
            }
        }
        if (!doSomething) {
            selectedEntity?.mouseClicked(ev)
        }
    }

    fun mouseMoved(ev: MouseEvent) {
        selectedEntity?.mouseMoved(ev)
    }

    fun keyClicked(ev: KeyEvent) {
        when (ev.keyCode) {
            VK_Q -> selectedEntity = null
        }
        selectedEntity?.keyClicked(ev)
    }

    var tmpPath: MutableList<Direction>? = null

    fun paint(g: Graphics) {
        if (G.map.selectedCellChanged || true) {
            val cs = G.map.cs
            selectedUnit?.let { unit ->
                G.map.drawPath(g, unit.paintPos, unit.path)
                val beg = if (G.win.isControlDown) unit.curDist else unit.pos
                if (G.map.selectedCellChanged) {
                    val mp = G.map.selectedCellPos
                    tmpPath = if (G.win.isControlDown) {
                        G.map.aStar(beg, mp) { unit.canMoveTo(it) }
                    } else {
                        G.map.aStar(beg, mp) { unit.canMoveTo(it) }
                    }
                }
                tmpPath?.let { G.map.drawPath(g, (beg - G.map.cellTranslation) * cs, it) }
            }
            selectedEntity?.let {
                g.color = Color.BLACK
                g.drawRect(it.paintPos.x + 1, it.paintPos.y + 1, cs - 2, cs - 2)
            }
        }
    }

    /**
     * Вызывается при завершении игроком хода
     */
    fun endTurn() {
        entities.forEach { it.endTurn() }
    }

    /**
     * Вызывается при начале игроком хода
     */
    fun newTurn() {
        entities.forEach { it.newTurn() }
    }

    /**
     * Добавить юнита игроку
     * @param newUnit юнит, который будет добавлен игроку
     */
    fun addUnit(newUnit: BaseUnit) {
        newUnit.owner = this
        G.map[newUnit.pos].unit = newUnit
        units.add(newUnit)
        //updateInvestigatedArea(newUnit.observableArea)
        newUnit.updateOwnerInvestigatedArea()
        updateObservableArea()
    }

    /**
     * Удалить юнита у игрока
     * @param unit юнит, который будет удалён
     */
    fun removeUnit(unit: BaseUnit) {
        G.map[unit.pos].unit = null
        units.removeIf { it == unit }
        if (selectedUnit == unit) {
            selectedUnit = null
        }
    }

    /**
     * Добавить здание игроку
     * @param newBuild здание, который будет добавлен игроку
     */
    fun addBuild(newBuild: BaseBuild) {
        newBuild.owner = this
        G.map[newBuild.pos].build = newBuild
        builds.add((newBuild))
        newBuild.updateOwnerInvestigatedArea()
        updateObservableArea()
    }

    /**
     * Удалить здание у игрока
     * @param build здание, которое будет удалено
     */
    fun removeBuild(build: BaseBuild) {
        G.map[build.pos].build = null
        builds.removeIf { it == build }
        if (selectedBuild == build) {
            selectedBuild = null
        }
    }

    /**
     * Проверяет, может ли персонаж заплатить указанную цену
     * @param cost стоимость
     */
    fun canPay(cost: Map<ResourceType, Int>) =
        cost.all { resource[it.key]!! >= it.value }

    /**
     * Проверяет, может ли персонаж заплатить указанную цену, и если да,
     * то отнимает соответствующее количество ресурсов
     * @param cost стоимость
     */
    fun pay(cost: Map<ResourceType, Int>): Boolean {
        if (canPay(cost)) {
            cost.forEach { changeResource(it.key, -it.value) }
            return true
        }
        return false
    }

    /**
     * Проверяет, принадлежит ли данная сущность игроку
     * @param entity сущность, принадлежность которой проверяется, или является null
     * @return true, если принадлежит, иначе false
     */
    fun own(entity: BaseEntity?) =
        if (entity == null) false else entity.owner == this

    /**
     * Содержит информацию об исследованной игроком территории, каждая клетка
     * может иметь значение либо ObservableStatus.NotInvestigated либо
     * ObservableStatus.Investigated
     */
    var investigatedArea =
        makeMatrix(G.map.size) { ObservableStatus.NotInvestigated }

    /**
     * Возвращает данные о том, какие клетки видит игрок
     */
    val observableArea = makeMatrix(G.map.size) { ObservableStatus.NotInvestigated }

    /**
     * Обновить наблюдаемую игроком зону
     */
    fun updateObservableArea() {
        investigatedArea.matrixForEachIndexed { pos, cellStatus ->
            observableArea[pos] = cellStatus
        }
        entities.forEach { entity ->
            entity.updateOwnerObservableArea()
        }
    }

    override operator fun equals(other: Any?) = name == (other as Player).name
    override fun hashCode() = name.hashCode()
}