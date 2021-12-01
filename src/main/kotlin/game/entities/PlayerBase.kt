package game.entities

import game.*
import gameinterface.CreateMenu
import graphics.Animation
import utilite.*
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class PlayerBase(owner: Player, pos: Vector = Vector(0, 0)) : BaseBuild(owner, pos) {
    override var allowedCells = mutableListOf(Cell.Type.Ground)
    override val cost: Cost = mapOf()
    override val factory get() = Factory

    private val maxBuildDistance = 5

    init {
        animation = G.animationManager.getAnimation("PlayerBase", owner.color)
    }

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = paintPos
        //g.fillRect(p.x + 2, p.y + 2, G.map.cs - 4, G.map.cs - 4)
        super.paint(g)
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

    override fun paintInterface(g: Graphics) = buildMenu.paint(g)

    private fun canBuildOn(cellPos: Vector) = G.map[cellPos].type in selectedBuild!!.allowedCells &&
            owner.observableArea[cellPos] != ObservableStatus.NotInvestigated &&
            cellPos.cellDistance(pos) <= maxBuildDistance &&
            G.map[cellPos].build == null &&
            (G.map[cellPos].unit == null || owner.own(G.map[cellPos].unit))

    override fun endTurn(): Boolean {
        super.endTurn()
        owner.resource.keys.forEach {
            owner.changeResource(it, 10)
        }
        buildMenu.unselect()
        return false
    }

    override fun mouseClicked(ev: MouseEvent) {
        buildMenu.mouseClicked(ev)
        selectedBuild?.let { selectedBuild ->
            val p = G.map.selectedCellPos
            when (ev.button) {
                MouseEvent.BUTTON1 ->
                    if (canBuildOn(p) &&
                        owner.pay(selectedBuild.cost)
                    ) {
                        owner.addBuild(selectedBuild.createEntity(owner, p) as BaseBuild)
                    }
                MouseEvent.BUTTON3 ->
                    buildMenu.unselect()
            }
        }
    }

    override fun mouseMoved(ev: MouseEvent) {

    }

    override fun keyClicked(ev: KeyEvent) {
        if (owner.selectedBuild == this) {
            buildMenu.keyClicked(ev)
        }
    }

    override fun selfCheck(from: BaseEntity?) {
        super.selfCheck(from)
        if (curHp <= 0) {
            owner.isLoose = true
            G.checkWin()
        }
    }

    private val selectedBuild get() = buildMenu.selected
    private val buildsList = arrayListOf(Barracks.Factory, Mine.Factory)
    private val buildMenu = CreateMenu(buildsList, owner)

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector): BaseEntity = PlayerBase(owner, pos)
        override fun paintPreview(g: Graphics) {
            g.fillRect(2, 2, G.map.cs - 4, G.map.cs - 4)
        }

        override val cost: Map<ResourceType, Int> = mapOf()
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
        override val maxHP: Int = 10
        override val requiredTechnology: String? = null
    }
}