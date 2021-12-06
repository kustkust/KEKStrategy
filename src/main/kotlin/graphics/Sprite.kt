package graphics

import utility.Rect
import utility.Vector
import java.awt.Graphics
import java.awt.Image

class Sprite(val source: Image, val bounds: Rect, var pos: Vector = Vector()) {
    fun paint(g: Graphics) = paint(g, pos)
    fun paint(g: Graphics, p: Vector) {
        g.drawImage(
            source,
            p.x, p.y, p.x + bounds.w, p.y + bounds.h,
            bounds.x, bounds.y, bounds.r, bounds.b,
            null,
        )
    }
}