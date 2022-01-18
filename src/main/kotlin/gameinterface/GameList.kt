package gameinterface

import game.G
import game.Player
import game.costToMultiRowString
import game.entities.BaseFactory
import utility.C
import utility.Event1
import utility.Vector
import utility.drawMultiString
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.*
import javax.swing.event.ListSelectionEvent


class GameList : JScrollPane() {
    var color = Color.white
        set(value) {
            field = value
            repaint()
        }
    val player: Player? get() = G.curPlayer
    private val innerList =JList<BaseFactory>()
    val selected: BaseFactory?
        get() = innerList.selectedValue
    val onSelected = Event1<ListSelectionEvent>()
    fun setFactories(l: List<BaseFactory>) {
        val tmp = DefaultListModel<BaseFactory>()
        l.forEachIndexed { i, bf ->
            tmp.add(i, bf)
        }
        innerList.model = tmp
    }
    var isItemsSelectable = true

    fun unselect() = innerList.clearSelection()

    val iconSize get() = C.cs / 2

    init {
        isFocusable = false
        getVerticalScrollBar().unitIncrement = 32
        border = null
        preferredSize = Dimension(128, C.cs * 3)
        //minimumSize = preferredSize
        background = null
        setViewportView(innerList)
        innerList.background = Color.gray
        innerList.addListSelectionListener {
            onSelected(it)
            if (!isItemsSelectable) {
                unselect()
            }
        }
        innerList.isFocusable = false
        innerList.cellRenderer = object : JPanel(), ListCellRenderer<BaseFactory> {
            lateinit var value: BaseFactory
            var isSelected = false
            var cellHasFocus = false

            init {
                isFocusable = false
                isOpaque = true
                preferredSize = Dimension(110, C.cs / 2)
                //minimumSize = preferredSize
            }

            override fun getListCellRendererComponent(
                list: JList<out BaseFactory>,
                value: BaseFactory,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                this.value = value
                this.isSelected = isSelected
                this.cellHasFocus = cellHasFocus
                return this
            }

            override fun paint(g: Graphics) {
                value.getPreview(color, 1).paint(g, Vector.Zero)
                player?.let { player ->
                    if (!value.isOpen(player)) {
                        g.drawImage(
                            G.map.shadow,
                            0, 0, iconSize, iconSize,
                            0, 0, iconSize, iconSize,
                            null
                        )
                    }
                    g.color = if(player.canPay(value.cost)) Color.black else Color.red
                    g.drawMultiString(value.cost.costToMultiRowString(), iconSize, 0)
                }
            }
        }
    }
}