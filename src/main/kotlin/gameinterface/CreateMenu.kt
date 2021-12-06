package gameinterface

import game.G
import game.Player
import game.entities.BaseFactory
import game.toString_
import graphics.Animation
import utility.*
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import kotlin.math.min

class CreateMenu(private val entitiesList: ArrayList<BaseFactory>, var owner: Player) {
    var selectedIndex = -1
    val selected get() = if (selectedIndex == -1) null else entitiesList[selectedIndex]

    private val rowHeight = G.map.cs
    private val bounds = Rect(
        rowHeight, rowHeight * 7,
        rowHeight * 5, rowHeight * entitiesList.size,
    )

    private val animationPreviewCash = mutableMapOf<String, Animation>()

    fun unselect() {
        selectedIndex = -1
    }

    fun paint(g: Graphics) {
        val p = bounds.pos.copy()
        val s = bounds.size + Vector(3, 3)

        g.color = Color.GRAY
        g.fillRect(p - Vector(2, 2), s)

        g.color = Color.black
        g.drawRect(p - Vector(2, 2), s)

        g.color = owner.color
        entitiesList.forEach {
            it.getPreview(owner.color).paint(g, p)
            if (!it.isOpen(owner)) {
                g.drawImage(G.map.shadow, p.x, p.y, null)
            }
            g.color = if(owner.canPay(it.cost)) Color.black else Color.red
            g.drawString(it.cost.toString_(), p.x + rowHeight, p.y + g.fontMetrics.height)
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

    fun mouseClicked(ev: MouseEvent) {
        if (ev.pos in bounds) {
            val tmp = (ev.y - bounds.pos.y) / rowHeight % entitiesList.size
            if (entitiesList[tmp].isOpen(owner)) {
                selectedIndex = tmp
            }
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