package gameinterface

import game.entities.BaseFactory
import utility.Event
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.ListSelectionEvent


class GameList: JScrollPane() {
    var color = Color.white
        set(value) {
            field = value
            repaint()
        }
    private val innerList = JList<BaseFactory>()
    val selected: BaseFactory?
        get() = innerList.selectedValue
    val onSelected = Event<ListSelectionEvent>()
    fun setFactories(l: List<BaseFactory>) {
        val tmp = DefaultListModel<BaseFactory>()
        l.forEachIndexed { i, bf ->
            tmp.add(i, bf)
        }
        innerList.model = tmp
    }

    init {
        setViewportView(innerList)

        innerList.cellRenderer = MyCellRenderer()
        innerList.addListSelectionListener {onSelected(it)}
        minimumSize = Dimension(100,100)
    }

    private class MyCellRender1(var color: Color) : JPanel(), ListCellRenderer<BaseFactory> {
        override fun getListCellRendererComponent(
            list: JList<out BaseFactory>,
            value: BaseFactory,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            setBounds(0,0,100,64)
            /*value.getPreview(color).paint(graphics, Vector(0,0))
            graphics.drawMultiString(value.cost.costToString(), 64,0)
            if (isSelected) {
                graphics.drawRect(0,0,100,64)
            }*/
            return this
        }
    }

    internal class MyCellRenderer : JLabel(), ListCellRenderer<Any?> {
        init {
            isOpaque = true
        }

        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            if(graphics == null) println("kek")
            text = value.toString()
            background = if (isSelected) Color.red else Color.white
            foreground = if (isSelected) Color.white else Color.black
            return this
        }
    }
}