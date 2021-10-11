import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_Q
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1

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
     * Список сущьностей, принадлежащих игроку, объединяет список юнитов и зданий
     */
    val entitys: List<BaseEntity>
        get() = List(units.size + builds.size) {
            if (it < units.size) {
                units[it]
            } else {
                builds[it - units.size]
            }
        }

    /**
     * ресурсы игрока
     */
    val resource = mutableMapOf<ResourceType, Int>().apply {
        ResourceType.values().forEach { this[it] = 10 }
    }

    fun changeResource(r: ResourceType, value: Int) {
        resource[r] = resource[r]!! + value
    }

    /**
     * Выбранный юнит. Если выбран не юнит или выбранно здание то возвращает null
     */
    var selectedUnit: BaseUnit?
        set(value) {
            selectedEntity = value
        }
        get() = if(selectedEntity is BaseUnit) selectedEntity as BaseUnit else null

    /**
     * Выбранное здание. Если здание не выбрано или выбран юнит, то возвращает null
     */
    var selectedBuild: BaseBuild?
        set(value) {
            selectedEntity = value
        }
        get() = if(selectedEntity is BaseBuild) selectedEntity as BaseBuild else null

    var selectedEntity: BaseEntity? = if(entitys.isNotEmpty()) entitys[0] else null

    /**
     * Цвет игрока, отражается на юнитах и зданиях
     */
    var color: Color = Color.RED

    /**
     * Обработка нажатий клавишь мыши
     */
    fun mouseClicked(ev: MouseEvent) {
        var doSomething = false
        if (ev.button == BUTTON1) {
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
        if (!doSomething) {
            selectedEntity?.mouseClicked(ev)
        }
    }

    fun mouseMoved(ev: MouseEvent) {
        selectedEntity?.mouseMoved(ev)
    }

    fun keyClicked(ev: KeyEvent) {
        when(ev.keyCode){
            VK_Q->selectedEntity = null
        }
        selectedEntity?.keyClicked(ev)
    }

    fun pain(g:Graphics) {
        if(G.map.selectedCellChanged) {
            selectedUnit?.apply {
                val mp = G.map.selectedCellPos
                val path: MutableList<Direction>
                val beg: Vector
                if (G.win.isControlDown) {
                    beg = curDist
                    path = G.map.aStar(beg, mp) { canMoveTo(it) }
                } else {
                    beg = pos
                    path = G.map.aStar(beg, mp) { canMoveTo(it) }
                }
                G.map.drawPath(g, beg, path)
            }
        }
    }

    /**
     * Вызывается при завершении игроком хода
     */
    fun endTurn() {
        entitys.forEach { it.endTurn() }
    }

    /**
     * Вызывается при начале игроком хода
     */
    fun newTurn() {
        entitys.forEach { it.newTurn() }
    }

    /**
     * Добавить юнита игроку
     * @param newUnit юнит, который будет добавлен игроку
     */
    fun addUnit(newUnit: BaseUnit) {
        newUnit.owner = this
        G.map[newUnit.pos].unit = newUnit
        units.add(newUnit)
        updateInvestegatedArea(newUnit.observableArea)
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
        updateInvestegatedArea(newBuild.observableArea)
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
     * Праоверяет, может ли персонаж заплптить указанную цену
     * @param cost стоимость
     */
    fun canPay(cost: Map<ResourceType, Int>) = cost.all { resource[it.key]!! >= it.value }

    /**
     * Праоверяет, может ли персонаж заплптить указанную цену, и если да,
     * то отнимает соответствующее количество ресурсов
     * @param cost стоимость
     */
    fun pay(cost: Map<ResourceType, Int>): Boolean {
        if (canPay(cost)) {
            cost.forEach {
                //val tmp = resource[it.key]?.minus(it.value)!!
                //resource[it.key] = resource[it.key]?.minus(it.value)!!
                changeResource(it.key, -it.value)
            }
            return true
        }
        return false
    }

    /**
     * Проверяет, принадлежит ли данная сущьность игроку
     * @param entity сущьность, принадлежность которой проверяется,или является null
     * @return true, если принадлежит, иначе false
     */
    fun own(entity: BaseEntity?) = if (entity == null) false else entity.owner == this

    /**
     * Содержит информацию о исследованной игроком территории, каждая клетка
     * может иметь значение либо ObservableStatus.NotInvestigated либо
     * ObservableStatus.Investigated
     */
    var investegatedArea =
        makeMatrix(G.map.size) { ObservableStatus.NotInvestigated }

    /**
     * Обновляет investegatedArea согласно с расположением юнитов и строений, по идее
     * такой вариант метода не нужен, так ка квсе сущности автоматически обновляют
     * investegatedArea
     */
    fun updateInvestegatedArea() =
        entitys.forEach { updateInvestegatedArea(it.observableArea) }

    fun updateInvestegatedArea(area: Matrix<ObservableStatus>) =
        area.matrixForEachIndexed { pos, status ->
            if (status == ObservableStatus.Observable) {
                investegatedArea[pos] = ObservableStatus.Investigated
            }
        }

    /**
     * Возвращает данные о том, какие клетки видит игрок
     */
    val observableArea: Matrix<ObservableStatus>
        get() {
            var res = investegatedArea.matrixClone()
            entitys.forEach {
                it.observableArea.matrixForEachIndexed { pos, status ->
                    if (status == ObservableStatus.Observable) {
                        res[pos] = ObservableStatus.Observable
                    }
                }
            }
            return res
        }

    override operator fun equals(other: Any?) = name == (other as Player).name
}