package gameinterface

import game.G
import game.Player
import game.ResourceType
import game.entities.BaseEntity
import java.awt.Color
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class SidePanel : JPanel() {
    private val miniMap = MiniMap()
    private val curPlayer = JLabel()
    private val playerResource = JTable()
    private val entityDescription = EntityDescription()

    fun init() {
        isFocusable = false
        background = Color.gray

        val l = VerticalLayout()
        layout = l

        G.map.cellTranslationChanged += { miniMap.repaint() }
        miniMap.maximumSize = Dimension(128, 128)
        miniMap.preferredSize = Dimension(128, 128)
        miniMap.minimumSize = Dimension(128, 128)
        add(miniMap)

        add(curPlayer)

        val m = playerResource.model as DefaultTableModel
        m.addColumn("Type")
        m.addColumn("Amount")
        ResourceType.values().forEach {
            m.addRow(java.util.Vector(arrayListOf(it.name, "")))
        }
        playerResource.isFocusable = false
        G.players.forEach { it.onResourceChanged += { _, _, _ -> setupResourceTable(G.curPlayer) } }
        add(playerResource)

        entityDescription.init()
        add(entityDescription)
    }

    fun setupForPlayer(player: Player) {
        curPlayer.text = player.name
        curPlayer.foreground = player.color
        setupResourceTable(player)
    }

    private fun setupResourceTable(player: Player) {
        val m = playerResource.model as DefaultTableModel
        ResourceType.values().forEachIndexed { i, res ->
            m.setValueAt(player.resource.getValue(res), i, 1)
        }
    }

    fun setEntityDescription(e: BaseEntity) {
        entityDescription.setEntityDescription(e)
        entityDescription.isVisible = true
    }

    fun setEmptyDescription() {
        entityDescription.setEmptyDescription()
        entityDescription.isVisible = false
    }
}