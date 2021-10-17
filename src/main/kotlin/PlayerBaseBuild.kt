import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_9
import java.awt.event.MouseEvent
import kotlin.math.abs
import kotlin.math.min

class PlayerBaseBuild(pos: Vector = Vector(0, 0)) : BaseBuild(pos) {
    override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
    override val cost: Cost = mapOf()

    private val maxBuildDistance = 5

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = paintPos
        g.fillRect(p.x + 2, p.y + 2, G.map.cs - 4, G.map.cs - 4)
        //g.fillPolygon(makePolygon(arrayOf(Vector())))
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)

        if (selectedBuild != null && owner.selectedBuild == this) {
            val onMapPos = G.map.selectedCellPos
            val mPos = (onMapPos - G.map.cellTranslation) * G.map.cs
            val gr = g.create(
                mPos.x, mPos.y,
                G.win.width, G.win.height
            )
            gr.color = Color(owner.color.red, owner.color.green, owner.color.blue, 128)
            selectedBuild!!.paintPreview(gr)
            gr.color = if (canBuildOn(onMapPos) && owner.canPay(selectedBuild!!.cost))
                Color.BLACK
            else
                Color.RED
            gr.drawRect(0, 0, G.map.cs, G.map.cs)
        }
    }

    private fun canBuildOn(cellPos: Vector) = G.map[cellPos].type in selectedBuild!!.allowedCells &&
            owner.observableArea[cellPos] != ObservableStatus.NotInvestigated &&
            cellPos.cellDistance(pos) <= maxBuildDistance &&
            G.map[cellPos].build == null &&
            (G.map[cellPos].unit == null || owner.own(G.map[cellPos].unit))

    override fun endTurn() {
        owner.resource.keys.forEach {
            owner.changeResource(it, 10)
        }
        selectedBuild = null
    }

    override fun newTurn() {
        //TO DO("Not yet implemented")
    }

    override fun mouseClicked(ev: MouseEvent) {
        if (selectedBuild != null) {
            val p = G.map.selectedCellPos
            when (ev.button) {
                MouseEvent.BUTTON1 ->
                    if (canBuildOn(p) &&
                        owner.pay(selectedBuild!!.cost)
                    ) {
                        owner.addBuild(selectedBuild!!.createEntity(p) as BaseBuild)
                    }
                MouseEvent.BUTTON3 ->
                    selectedBuild = null
            }
        }
    }

    override fun mouseMoved(ev: MouseEvent) {

    }

    override fun keyClicked(ev: KeyEvent) {
        if (owner.selectedBuild == this) {
            if (ev.keyCode in VK_1..min(VK_9, buildList.size + VK_1 - 1)) {
                selectedBuild = buildList[ev.keyCode - VK_1]
            }
        }
    }

    override fun selfCheck() {
        super.selfCheck()
        //тут по идее должен быть код для проигрыша игрока в случае разрушения
        //его базы
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

    private var selectedBuild: BaseFactory? = null

    private val buildList = arrayOf(
        Mine.Factory,
        Barracks.Factory
    )
}