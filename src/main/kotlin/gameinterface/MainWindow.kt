package gameinterface

import game.G
import utilite.Vector
import utilite.toVector
import java.awt.Dimension
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1
import javax.swing.JFrame
import javax.swing.JPanel

class MainWindow : JFrame() {

    var gameWidth = 800
    var gameHeight = 600

    val mainPanel = JPanel()

    val menu = GameMenu(gameWidth, gameHeight)
    val gamePanel = GamePanel()

    val fm: FontMetrics
        get() =
            gamePanel.graphics.fontMetrics

    var isControlDown = false
    var isLeftButtonDown = false

    val innerSize
        get() = Vector(gamePanel.width, gamePanel.height)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isResizable = true

        mainPanel.preferredSize = Dimension(gameWidth, gameHeight)
        add(mainPanel)

        gamePanel.preferredSize = Dimension(gameWidth, gameHeight)
        gamePanel.isVisible = false
        mainPanel.add(gamePanel)

        mainPanel.add(menu.menuPanel)
        mainPanel.add(menu.mapChoosePanel)
        mainPanel.add(menu.pausePanel)
        glassPane = menu.pausePanel
        pack()

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyPressed(e)
                gamePanel.repaint()
                mainPanel.repaint()
            }

            override fun keyReleased(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyClicked(e)
                gamePanel.repaint()
                mainPanel.repaint()
            }
        })
        val mouseAdapter = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                G.mouseClicked(e)
                gamePanel.repaint()
                mainPanel.repaint()
            }

            override fun mousePressed(e: MouseEvent) {
                if (e.button == BUTTON1) {
                    isLeftButtonDown = true
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.button == BUTTON1) {
                    isLeftButtonDown = false
                }
            }

            private fun mouseMoved_(e: MouseEvent) {
                G.mouseMoved(e)
                gamePanel.repaint()
                mainPanel.repaint()
            }

            override fun mouseMoved(e: MouseEvent) = mouseMoved_(e)

            override fun mouseDragged(e: MouseEvent) = mouseMoved_(e)
        }
        mainPanel.addMouseListener(mouseAdapter)
        mainPanel.addMouseMotionListener(mouseAdapter)

        val dim = Toolkit.getDefaultToolkit().screenSize
        setLocation(dim.width / 2 - size.width / 2, dim.height / 2 - size.height / 2)
    }

    private var _mPos = Vector(0, 0)

    val mPos: Vector
        get() {
            val tmp = gamePanel.mousePosition
            if (tmp != null) {
                _mPos = tmp.toVector
            }
            return _mPos
        }

    class GamePanel : JPanel() {
        override fun paint(g: Graphics) {
            G.paint(g)
        }
    }
}