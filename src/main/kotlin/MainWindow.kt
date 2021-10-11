import java.awt.Graphics
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.JPanel

class MainWindow : JFrame() {
    object Panel : JPanel() {
        override fun paint(g: Graphics) {
            G.paint(g)
        }
    }

    var isControlDown= false

    init {
        setSize(600, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
        isResizable = true

        add(Panel)

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
                //TODO("Not yet implemented")
            }

            override fun keyPressed(e: KeyEvent) {
                isControlDown = e.isControlDown
            }

            override fun keyReleased(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyClicked(e)
                Panel.revalidate()
                //оптимизировать отрисовку
                Panel.repaint()
            }
        })
        Panel.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                G.mouseClicked(e)
                //Panel.revalidate()
                Panel.repaint()
            }

            override fun mousePressed(e: MouseEvent?) {
                //TODO("Not yet implemented")
            }

            override fun mouseReleased(e: MouseEvent?) {
                //TODO("Not yet implemented")
            }

            override fun mouseEntered(e: MouseEvent?) {
                //TODO("Not yet implemented")
            }

            override fun mouseExited(e: MouseEvent?) {
                //TODO("Not yet implemented")
            }

        })
        Panel.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {
                //TODO("Not yet implemented")
            }

            override fun mouseMoved(e: MouseEvent) {
                G.mouseMoved(e)
                Panel.repaint()
            }

        })
    }

    private var _mPos = Vector(0, 0)

    val mPos: Vector
        get() {
            val tmp = Panel.mousePosition
            if (tmp != null) {
                _mPos = tmp.toVector
            }
            return _mPos
        }
}