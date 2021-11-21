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
        rowHeight, rowHeight * 2,
        rowHeight * 5, rowHeight * entitiesList.size,
    )

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
            val gr = g.create(
                p.x, p.y,
                G.win.width, G.win.height
            )
            it.paintPreview(gr)
            if (!it.isOpen(owner)) {
                gr.drawImage(G.map.shadow, 0, 0, null)
            }
            gr.drawString(it.cost.toString_(), rowHeight, gr.fontMetrics.height)
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
            val tmp  = (ev.y - bounds.pos.y) / rowHeight % entitiesList.size
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