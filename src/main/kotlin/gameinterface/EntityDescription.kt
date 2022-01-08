package gameinterface

import game.entities.Barracks
import game.entities.BaseEntity
import game.entities.BaseUnit
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

    init {
        background = Color.gray
        layout = VerticalLayout()
        add(entityName)
        add(entityHP)
        add(unitAP)
        sellButton.text = "Sell"
        sellButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                entity?.let{ it.owner.sellEntity(it) }
            }
        })
        sellButton.isFocusable = false
        add(sellButton)
    }

    open fun setEntityDescription(entity_: BaseEntity) {
        entity = entity_

        entity?.let { entity ->
            entityName.text = entity.factory.entityName

            setEntityHp(0)
            entity.curHpChanged += setEntityHp

            if (entity is BaseUnit) {
                setUnitAp(0)
                entity.remMovePointsChanged += setUnitAp
                unitAP.isVisible = true
            } else {
                unitAP.isVisible = false
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