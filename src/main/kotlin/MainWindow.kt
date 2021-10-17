import java.awt.Dimension
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
    var wText = javax.swing.JTextArea("width:")
    var hText = javax.swing.JTextArea("height:")
    var wField = javax.swing.JTextField()
    var hField = javax.swing.JTextField()


    var isControlDown= false

    val innerSize
        get() = Vector(Panel.width, Panel.height)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        isVisible = true
        isResizable = true
        Panel.preferredSize = Dimension(600,600)
        add(Panel)
        //Panel.isVisible = false

        pack()

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
                //TO DO("Not yet implemented")
            }

            override fun keyPressed(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyPressed(e)
                Panel.repaint()
            }

            override fun keyReleased(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyClicked(e)
                Panel.repaint()
            }
        })
        Panel.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                G.mouseClicked(e)
                Panel.repaint()
            }

            override fun mousePressed(e: MouseEvent?) {
                //TO DO("Not yet implemented")
            }

            override fun mouseReleased(e: MouseEvent?) {
                //TO DO("Not yet implemented")
            }

            override fun mouseEntered(e: MouseEvent?) {
                //TO DO("Not yet implemented")
            }

            override fun mouseExited(e: MouseEvent?) {
                //TO DO("Not yet implemented")
            }

        })
        Panel.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {
                //TO DO("Not yet implemented")
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