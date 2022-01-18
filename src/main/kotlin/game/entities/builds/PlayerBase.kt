package game.entities.builds

import game.*
import game.entities.BaseEntity
import game.entities.BaseFactory
import game.map.Cell
import game.map.ObservableStatus
import graphics.Animation
import utility.C
import utility.Vector
import utility.get
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseEvent

class PlayerBase(owner_: Player, pos_: Vector) : BaseBuild(owner_, pos_) {
    override val factory get() = Factory

    override var selable = false

    var maxBuildDistance = 10

    var selectedBuild: BaseFactory? = null
        get() = field
        set(value) {
            if (value == null) {
                field = null
            } else if (value.isOpen(owner)) {
                field = value
            }
        }

    val buildsList = arrayListOf(
        Barracks.Factory,
        Mine.Factory,
        Wall.Factory,
        Gate.Factory,
        Sawmill.Factory,
    )

    override var height = 1

    //private val buildMenu = CreateMenu(buildsList, owner_)

    override fun paint(g: Graphics) {
        g.color = owner.color
        val p = paintPos
        super.paint(g)
        g.color = Color.BLACK
        g.drawString(curHp.toString(), p.x, p.y + g.font.size)

        if (selected && !owner.isTechOpen) {
            selectedBuild?.let { selectedBuild ->
                val onMapPos = G.map.selectedCellPos
                val mPos = (onMapPos - G.map.cellTranslation) * C.cs
                selectedBuild.getPreview(owner.color).paint(g, mPos)
                g.color = if (canBuildOn(onMapPos) && owner.canPay(selectedBuild.cost))
                    Color.BLACK
                else
                    Color.RED
                g.drawRect(mPos.x, mPos.y, C.cs, C.cs)
            }
        }
    }

    //override fun paintInterface(g: Graphics) = buildMenu.paint(g)

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
        selectedBuild = null
        return false
    }

    override fun mouseClicked(ev: MouseEvent) {
        when (ev.button) {
            MouseEvent.BUTTON1 -> selectedBuild?.let { it ->
                val p = G.map.selectedCellPos
                if (canBuildOn(p) && owner.pay(it.cost)) {
                    it.createEntity(owner, p)
                }
            }
            MouseEvent.BUTTON3 -> selectedBuild = null
        }
    }

    override fun selfCheck(from: BaseEntity?) {
        super.selfCheck(from)
        if (curHp <= 0) {
            owner.isLoose = true
            G.checkWin()
        }
    }

    override fun onUnselected(): Boolean {
        super.onUnselected()
        return true
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