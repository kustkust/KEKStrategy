package gameinterface

import game.G
import game.entities.BaseEntity
import game.entities.builds.Barracks
import game.entities.builds.PlayerBase
import game.entities.units.BaseUnit
import java.awt.Color
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

open class EntityDescription : JPanel() {
    private var entity: BaseEntity? = null

    private val entityName = JLabel()

    private val entityHP = JLabel()
    private val setEntityHp: (Int) -> Unit = { entityHP.text = "HP: ${entity?.curHp}/${entity?.maxHp}" }

    private val unitAP = JLabel()
    private val setUnitAp: (Int) -> Unit =
        { (entity as? BaseUnit)?.let { unitAP.text = "MP: ${it.remMovePoints}/${it.maxMovePoints}" } }

    private val sellButton = JButton()

    val actionList = GameList()

    fun init() {
        isFocusable = false
        background = Color.gray
        layout = VerticalLayout()

        add(entityName)
        add(entityHP)
        add(unitAP)
        sellButton.text = "Sell"
        sellButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                entity?.let { it.owner.sellEntity(it) }
            }
        })
        sellButton.isVisible = true
        sellButton.isFocusable = false
        add(sellButton)
        add(actionList)

        G.players.forEach { player ->
            player.technologies.onTechnologyOpened += { repaint() }
        }
    }

    open fun setEntityDescription(entity: BaseEntity) {
        this.entity = entity
        entityName.text = entity.factory.entityName

        setEntityHp(0)
        entity.curHpChanged += setEntityHp

        when (entity) {
            is BaseUnit -> {
                setUnitAp(0)
                entity.remMovePointsChanged += setUnitAp
                unitAP.isVisible = true
            }
            else -> unitAP.isVisible = false
        }

        actionList.onSelected.clear()
        when (entity) {
            is PlayerBase -> {
                actionList.setFactories(entity.buildsList)
                actionList.isItemsSelectable = true
                actionList.onSelected += { entity.selectedBuild = actionList.selected }
                actionList.color = entity.owner.color
                actionList.isVisible = true
            }
            is Barracks -> {
                actionList.setFactories(entity.unitsList)
                actionList.isItemsSelectable = false
                actionList.onSelected += { entity.spawnUnit(it.firstIndex) }
                actionList.color = entity.owner.color
                actionList.isVisible = true
            }
            else -> {
                actionList.isVisible = false
            }
        }
        validate()
    }

    open fun setEmptyDescription() {
        entityName.text = ""
        entityHP.text = ""
        unitAP.text = ""
    }

    final override fun add(comp: Component?): Component = super.add(comp)
}