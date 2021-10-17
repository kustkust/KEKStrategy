import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_9
import java.awt.event.MouseEvent
import kotlin.math.abs
import kotlin.math.min

class Barracks(pos: Vector) : BaseBuild(pos) {
    override val allowedCells: MutableList<Cell.Type>
        get() = Factory.allowedCells
    override val cost: Cost
        get() = Factory.cost

    private var maxSpawnPerTurn = 1
    private var curSpawned = 1
    private val creatableUnits = listOf(MeleeUnit.Factory)

    private fun spawnUnit(i: Int) {
        if (curSpawned > 0 && onCell.unit == null && owner.pay(creatableUnits[i].cost)) {
            owner.addUnit(creatableUnits[i].createEntity(pos) as BaseUnit)
            curSpawned--
        }
    }

    override fun iterateInvestigatedArea(iter: (pos: Vector) -> Unit) {
        for (i in -2..2) {
            for (j in -2 + abs(i)..2 - abs(i)) {
                val dp = pos + Vector(i, j)
                if (G.map.inMap(dp)) {
                    iter(dp)
                }
            }
        }
    }

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = paintPos
        g.fillRect(p.x + 2, p.y + G.map.cs / 2 + 2, G.map.cs - 4, G.map.cs / 2 - 2)
        //g.fillPolygon(makePolygon(arrayOf(Vector())))
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)

    }

    override fun endTurn() {
        //TO DO("Not yet implemented")
    }

    override fun newTurn() {
        curSpawned = maxSpawnPerTurn
    }

    override fun mouseClicked(ev: MouseEvent) {
        //TO DO("Not yet implemented")
    }

    override fun mouseMoved(ev: MouseEvent) {
        //TO DO("Not yet implemented")
    }

    override fun keyClicked(ev: KeyEvent) {
        if (ev.keyCode in VK_1..min(VK_9, creatableUnits.size + VK_1 - 1)) {
            spawnUnit(ev.keyCode - VK_1)
        }
    }

    object Factory : BaseFactory {
        override fun createEntity(pos: Vector): BaseEntity = Barracks(pos)
        override fun paintPreview(g: Graphics) {
            //g.color = owner.color
            g.fillRect(2, G.map.cs / 2 + 2, G.map.cs - 4, G.map.cs / 2 - 2)

        }

        override val cost: Map<ResourceType, Int> = mapOf(ResourceType.Gold to 5)
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
    }
}