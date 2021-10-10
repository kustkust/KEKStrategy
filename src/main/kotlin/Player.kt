import java.awt.Color
import java.awt.event.KeyEvent
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

    fun changeResource(r: ResourceType, value: Int){
        resource[r] = resource[r]!! + value
    }

    /**
     * Выбранный юнит. Если выбран юнит то selectedBuild обязательно null,
     * возможно стоит объединить с selectedBuild
     */
    var selectedUnit: BaseUnit? = if (units.isNotEmpty()) units.first() else null
        set(value) {
            if (value != null && selectedBuild != null) {
                selectedBuild = null
            }
            field = value
        }

    /**
     * Выбранное здание. Если здание выбрано, то selectedUnit обязательно null,
     * возможно стоит объединить с selectedUnit
     */
    var selectedBuild: BaseBuild? = null
        set(value) {
            if (value != null && selectedUnit != null) {
                selectedUnit = null
            }
            field = value
        }

    val selectedEntity: BaseEntity?
        get() = selectedUnit ?: selectedBuild

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
            val pos = Vector(ev.x, ev.y) / G.map.cs
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
        selectedEntity?.keyClicked(ev)
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
                resource[it.key] = resource[it.key]?.minus(it.value)!!
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
     * Обновляет investegatedArea согласно с расположением юнитов и строений
     */
    fun updateObservableArea() =
        entitys.forEach {
            it.observableArea.matrixForEachIndexed { pos, status ->
                if (status == ObservableStatus.Observable) {
                    investegatedArea[pos] = ObservableStatus.Investigated
                }
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