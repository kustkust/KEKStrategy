package game.entities

import game.*
import gameinterface.CreateMenu
import graphics.Animation
import utility.Vector
import utility.get
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class PlayerBase(owner_: Player, pos_: Vector) : BaseBuild(owner_, pos_) {
    override val factory get() = Factory

    var maxBuildDistance = 10

    private val selectedBuild get() = buildMenu.selected

    private val buildsList = arrayListOf(
        Barracks.Factory,
        Mine.Factory,
        Wall.Factory,
        Gate.Factory,
        Sawmill.Factory,
    )

    private val buildMenu = CreateMenu(buildsList, owner_)

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = paintPos
        super.paint(g)
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)

        if(selected && !owner.isTechOpen){
            selectedBuild?.let { selectedBuild ->
                val onMapPos = G.map.selectedCellPos
                val mPos = (onMapPos - G.map.cellTranslation) * G.map.cs
                selectedBuild.getPreview(owner.color).paint(g, mPos)
                g.color = if (canBuildOn(onMapPos) && owner.canPay(selectedBuild.cost))
                    Color.BLACK
                else
                    Color.RED
                g.drawRect(mPos.x, mPos.y, G.map.cs, G.map.cs)
            }
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
                        //owner.addBuild(selectedBuild.createEntity(owner, p) as BaseBuild)
                        selectedBuild.createEntity(owner,p)
                    }
                MouseEvent.BUTTON3 ->
                    buildMenu.unselect()
            }
        }
    }

    override fun keyClicked(ev: KeyEvent) {
        if (selected) {
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

    object Factory : BaseFactory {
        override fun createEntity(owner: Player, pos: Vector): BaseEntity = PlayerBase(owner, pos)
        override val animationPreviewCash = mutableMapOf<Color, Animation>()

        override val cost: Map<ResourceType, Int> = makeCost()
        override val entityName = PlayerBase::class.simpleName ?: ""
        override var allowedCells: MutableList<Cell.Type> = mutableListOf(Cell.Type.Ground)
        override val maxHP: Int = 50
        override val requiredTechnology: String? = null
    }
}