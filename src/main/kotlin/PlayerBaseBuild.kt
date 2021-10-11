import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_9
import java.awt.event.MouseEvent
import kotlin.math.min

class PlayerBaseBuild(pos: Vector = Vector(0, 0)) : BaseBuild(pos) {
    override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
    override val cost: Cost = mapOf()

    private val maxBuildDistance = 5

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = pos * G.map.cs
        g.fillRect(p.x + 2, p.y + 2, G.map.cs - 4, G.map.cs - 4)
        //g.fillPolygon(makePolygon(arrayOf(Vector())))
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)

        if (selectedBuild != null) {
            G.drawTask += {
                val onMapPos = G.win.mPos / G.map.cs
                val mPos = onMapPos * G.map.cs
                val gr = it.create(
                    mPos.x, mPos.y,
                    G.win.width - mPos.x, G.win.height - mPos.y
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
    }

    private fun canBuildOn(pos: Vector) = G.map[pos].type in selectedBuild!!.allowedCells &&
            owner.observableArea[pos] != ObservableStatus.NotInvestigated &&
            pos.cellDistance(pos) <= maxBuildDistance &&
            G.map[pos].build == null &&
            (G.map[pos].unit == null || owner.own(G.map[pos].unit))

    override fun endTurn() {
        owner.resource.keys.forEach {
            owner.changeResource(it, 10)
        }
    }

    override fun newTurn() {
        //TODO("Not yet implemented")
    }

    override fun mouseClicked(ev: MouseEvent) {
        if (selectedBuild != null) {
            val p = ev.pos / G.map.cs
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
            if (ev.keyCode in VK_1..min(VK_9, buildList.size + VK_1)) {
                selectedBuild = buildList[ev.keyCode - VK_1]
            }
        }
    }

    override fun selfCheck() {
        super.selfCheck()
        //тут по идее должен быть код для проигрыша игрока в случае разрушения
        //его базы
        //TODO("Not yet implemented")
    }

    override val observableArea: Matrix<ObservableStatus>
        get() = makeMatrix(G.map.size) {
            if (pos.cellDistance(it) < 2) {
                ObservableStatus.Observable
            } else {
                ObservableStatus.Investigated
            }
        }

    private var selectedBuild: BaseFactory? = null

    private val buildList = arrayOf(
        Mine.Factory,
        Barracks.Factory
    )
}