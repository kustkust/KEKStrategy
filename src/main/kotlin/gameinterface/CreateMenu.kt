package gameinterface

import game.G
import game.Player
import game.costToString
import game.entities.BaseFactory
import utility.Rect
import utility.Vector
import utility.drawMultiString
import utility.pos
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import kotlin.math.min

class CreateMenu(
    private val entitiesList: ArrayList<BaseFactory>,
    var owner: Player,
    val onSelected: (CreateMenu) -> Unit = {},
) : JPanel() {
    var selectedIndex = -1
    val selected get() = if (selectedIndex == -1) null else entitiesList[selectedIndex]

    private val rowHeight = G.map.cs
    private val bounds = Rect(
        0, 0,
        rowHeight * 2, rowHeight * entitiesList.size,
    )

    init {
        preferredSize = Dimension(bounds.w, bounds.h)
        isFocusable = false
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(ev: MouseEvent) {
                if (ev.pos in bounds) {
                    val tmp = (ev.y - bounds.pos.y) / rowHeight % entitiesList.size
                    if (entitiesList[tmp].isOpen(owner)) {
                        selectedIndex = tmp
                        repaint()
                    }
                }
                onSelected(this@CreateMenu)
            }
        })
    }

    fun unselect() {
        selectedIndex = -1
    }

    override fun paintComponent(g: Graphics) {
        val p = Vector()
        val s = bounds.size + Vector(3, 3)

        //g.clearRect(0,0,size.width, size.height)

        g.color = Color.GRAY
        g.fillRect(0, 0, size.width, size.height)

        g.color = owner.color
        entitiesList.forEach {
            it.getPreview(owner.color).paint(g, p)
            if (!it.isOpen(owner)) {
                g.drawImage(G.map.shadow, p.x, p.y, null)
            }
            g.color = if (owner.canPay(it.cost)) Color.black else Color.red
            g.drawMultiString(
                it.cost.costToString(),
                p.x + rowHeight,
                p.y + g.fontMetrics.height
            )
            p.y += rowHeight
        }
        if (selectedIndex != -1) {
            g.color = Color.black
            g.drawRect(
                bounds.pos.x, bounds.pos.y + selectedIndex * rowHeight,
                bounds.size.x, rowHeight
            )
        }
    }

    fun keyClicked(ev: KeyEvent) {
        if (ev.keyCode in KeyEvent.VK_1..min(KeyEvent.VK_9, entitiesList.size + KeyEvent.VK_1 - 1) &&
            entitiesList[ev.keyCode - KeyEvent.VK_1].isOpen(owner)
        ) {
            selectedIndex = ev.keyCode - KeyEvent.VK_1
        }
    }
}