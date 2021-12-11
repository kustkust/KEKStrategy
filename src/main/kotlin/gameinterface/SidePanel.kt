package gameinterface

import game.G
import game.Player
import game.ResourceType
import game.entities.BaseEntity
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel

class SidePanel : JPanel() {
    private val miniMap = MiniMap()
    private val curPlayer = JLabel()
    private val playerResource = JTable()
    private val entityDescription = EntityDescription()
    private val buildListPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }
    private val buildList = JScrollPane(buildListPanel)

    fun init() {
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
        add(playerResource)

        add(entityDescription)

        buildList.maximumSize = Dimension(512, 165)
        buildList.isVisible = false
        add(buildList)
    }

    fun setupForPlayer(player: Player) {
        curPlayer.text = player.name
        curPlayer.foreground = player.color

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

    fun setBuildList(buildMenu: CreateMenu?) {
        buildListPanel.removeAll()
        if (buildMenu == null) {
            buildList.isVisible = false
        } else {
            buildListPanel.add(buildMenu)
            buildList.isVisible = true
        }
        buildListPanel.validate()
        buildListPanel.repaint()
        validate()
    }
}