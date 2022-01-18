package gameinterface

import game.G
import game.map.ObservableStatus
import utility.Vector
import utility.drawRect
import utility.matrixForEachIndexed
import utility.pos
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.swing.JPanel

class MiniMap : JPanel() {
    private val scale = 4

    val shadow = BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB).apply {
        val g = graphics
        g.color = Color(0, 0, 0, 0)
        g.fillRect(0, 0, scale, scale)
        g.color = Color.black
        for (x in 0 until scale) {
            for (y in 0 until scale) {
                if ((x + y) % 2 == 0) {
                    g.drawLine(x, y, x, y)
                }
            }
        }
    }

    private val cellSize
        get() = Vector(size.width / scale, size.height / scale)

    private val translation: Vector
        get() {
            val p = G.map.cellTranslation + (G.map.winSizeInCells - cellSize) / 2
            if (p.x + cellSize.x > G.map.size.x)
                p.x = G.map.size.x - cellSize.x
            if (p.x < 0)
                p.x = 0
            if (p.y + cellSize.y > G.map.size.y)
                p.y = G.map.size.y - cellSize.y
            if (p.y < 0)
                p.y = 0
            return p
        }

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) =
                G.map.centerOn(translation + e.pos / scale)
        })
    }

    override fun paintComponent(g: Graphics) {
        val p = translation
        val s = cellSize
        if (p.x + s.x > G.map.size.x)
            s.x = G.map.size.x - p.x
        if (p.y + s.y > G.map.size.y)
            s.y = G.map.size.y - p.y

        g.color = Color.black
        g.fillRect(0, 0, size.width, size.height)

        G.map.cells.matrixForEachIndexed(p, s) { x, y, c ->
            if (G.curPlayer.observableArea[x][y] != ObservableStatus.NotInvestigated) {
                g.color =
                    if (c.unit != null) c.unit?.owner?.color
                    else if (c.build != null) c.build?.owner?.color
                    else c.type.color
                g.fillRect((x - p.x) * scale, (y - p.y) * scale, scale, scale)
                if (G.curPlayer.observableArea[x][y] == ObservableStatus.Investigated) {
                    g.drawImage(shadow, (x - p.x) * scale, (y - p.y) * scale, null)
                }
            }
        }
        g.color = Color.red
        g.drawRect(
            (G.map.cellTranslation - p) * scale,
            G.map.winSizeInCells * scale + Vector.UpLeft
        )
        g.color = Color.gray
        g.drawRect(
            Vector(),
            s * scale + Vector.UpLeft
        )
    }
}
