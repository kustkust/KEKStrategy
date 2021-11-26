import java.awt.Dimension
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.event.*
import javax.swing.JFrame
import javax.swing.JLayeredPane
import javax.swing.JPanel

class MainWindow : JFrame() {

    val MainPanel = JPanel()
    val Panel = GamePanel()
    val Layers = JLayeredPane()
    var wText = javax.swing.JTextArea("width:")
    var hText = javax.swing.JTextArea("height:")
    var wField = javax.swing.JTextField()
    var hField = javax.swing.JTextField()

    val fm get() =
        Panel.graphics.fontMetrics

    var isControlDown = false

    val innerSize
        get() = Vector(Panel.width, Panel.height)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isResizable = true
        Panel.preferredSize = Dimension(600, 600)
        MainPanel.preferredSize = Dimension(600, 600)
        add(MainPanel)
        MainPanel.add(Panel)
        pack()

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
                //TO DO("Not yet implemented")
            }

            override fun keyPressed(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyPressed(e)
                Panel.repaint()
                MainPanel.repaint()
            }

            override fun keyReleased(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyClicked(e)
                Panel.repaint()
                MainPanel.repaint()
            }
        })
        MainPanel.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                G.mouseClicked(e)
                Panel.repaint()
                MainPanel.repaint()
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
        MainPanel.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {
                //TO DO("Not yet implemented")
            }

            override fun mouseMoved(e: MouseEvent) {
                G.mouseMoved(e)
                Panel.repaint()
                MainPanel.repaint()
            }

        })

        val dim = Toolkit.getDefaultToolkit().screenSize
        setLocation(dim.width / 2 - size.width / 2, dim.height / 2 - size.height / 2)
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
class GamePanel : JPanel() {
    override fun paint(g: Graphics) {
        G.paint(g)
    }
}