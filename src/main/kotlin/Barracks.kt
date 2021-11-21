import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_9
import java.awt.event.MouseEvent
import kotlin.math.min

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
    private val unitsMenu = CreateMenu(unitsList, owner)


    private fun spawnUnit(i: Int) {
        if (curSpawned > 0 &&
            onCell.unit == null &&
            unitsList[i].isOpen(owner) &&
            owner.pay(unitsList[i].cost)
        ) {
            owner.addUnit(unitsList[i].createEntity(owner,pos) as BaseUnit)
            curSpawned--
        }
    }

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = paintPos
        g.fillRect(
            p.x + 2,
            p.y + G.map.cs / 2 + 2,
            G.map.cs - 4,
            G.map.cs / 2 - 2
        )
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)
    }

    override fun paintInterface(g: Graphics) = unitsMenu.paint(g)

    override fun newTurn() {
        super.newTurn()
        curSpawned = maxSpawnPerTurn
    }

    override fun mouseClicked(ev: MouseEvent) {
        unitsMenu.mouseClicked(ev)
        if(unitsMenu.selectedIndex!=-1){
            spawnUnit(unitsMenu.selectedIndex)
            unitsMenu.unselect()
        }
    }

    override fun mouseMoved(ev: MouseEvent) {
        //TO DO("Not yet implemented")
    }

    override fun keyClicked(ev: KeyEvent) {
        /*if (ev.keyCode in VK_1..min(VK_9, unitsList.size + VK_1 - 1)) {
            spawnUnit(ev.keyCode - VK_1)
        }*/
        unitsMenu.keyClicked(ev)
        if(unitsMenu.selectedIndex!=-1){
            spawnUnit(unitsMenu.selectedIndex)
            unitsMenu.unselect()
        }
    }

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector): BaseEntity = Barracks(owner, pos)
        override fun paintPreview(g: Graphics) {
            g.fillRect(2, G.map.cs / 2 + 2, G.map.cs - 4, G.map.cs / 2 - 2)
        }

        val creatableUnits = mutableListOf<BaseFactory>(MeleeUnit.Factory)
        override val cost: Map<ResourceType, Int> = mapOf(ResourceType.Gold to 5)
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
        override val maxHP: Int = 10
        override val requiredTechnology: String = "MeleeUnit"
    }
}