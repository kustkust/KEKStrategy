package game.entities

import game.*
import gameinterface.CreateMenu
import graphics.Animation
import utility.Vector
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent

class Barracks(owner: Player, pos: Vector = Vector(0, 0)) :
    BaseBuild(owner, pos) {
    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost
    override val factory get() = Factory

    private var maxSpawnPerTurn = 1
    private var curSpawned = 1
    private val unitsList = ArrayList(Factory.creatableUnits)
    private val unitsMenu: CreateMenu = CreateMenu(unitsList, owner) { unitsMenu ->
        if (unitsMenu.selectedIndex != -1) {
            spawnUnit(unitsMenu.selectedIndex)
            unitsMenu.unselect()
        }
    }

    private fun spawnUnit(i: Int) {
        if (curSpawned > 0 &&
            onCell.unit == null &&
            unitsList[i].isOpen(owner) &&
            owner.pay(unitsList[i].cost)
        ) {
            owner.addUnit(unitsList[i].createEntity(owner, pos) as BaseUnit)
            curSpawned--
        }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val p = paintPos
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)
    }

    //override fun paintInterface(g: Graphics) = unitsMenu.paint(g)

    override fun onSelected() {
        super.onSelected()
        G.win.gameInterfacePanel.setBuildList(unitsMenu)
    }

    override fun onUnselected() {
        super.onUnselected()
        G.win.gameInterfacePanel.setBuildList(null)
    }

    override fun newTurn() {
        super.newTurn()
        curSpawned = maxSpawnPerTurn
    }

    override fun keyClicked(ev: KeyEvent) {
        /*if (ev.keyCode in VK_1..min(VK_9, unitsList.size + VK_1 - 1)) {
            spawnUnit(ev.keyCode - VK_1)
        }*/
        unitsMenu.keyClicked(ev)
        if (unitsMenu.selectedIndex != -1) {
            spawnUnit(unitsMenu.selectedIndex)
            unitsMenu.unselect()
        }
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector): BaseEntity = Barracks(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override val entityName = Barracks::class.simpleName ?: ""

        val creatableUnits = mutableListOf<BaseFactory>(MeleeUnit.Factory)
        override val cost: Map<ResourceType, Int> = mapOf(ResourceType.Gold to 5)
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
        override val maxHP: Int = 10
        override val requiredTechnology: String = "game.entities.MeleeUnit"
    }
}