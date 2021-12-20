package gameinterface

import game.G
import utility.Vector
import utility.toVector
import java.awt.Dimension
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1
import javax.swing.*

class MainWindow : JFrame() {

    var gameWidth = 1024
    var gameHeight = 768

    val mainPanel = JPanel()

    val menu = GameMenu(gameWidth, gameHeight)
    val gamePanel = JPanel()

    val gameRenderPanel = GamePanel()
    val gameInterfacePanel = SidePanel()

    val fm: FontMetrics
        get() =
            gamePanel.graphics.fontMetrics

    var isControlDown = false
    var isLeftButtonDown = false

    val innerSize
        get() = Vector(gameRenderPanel.width, gameRenderPanel.height)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        isResizable = true

        mainPanel.layout = null
        mainPanel.preferredSize = Dimension(gameWidth, gameHeight)
        mainPanel.isOpaque = false
        add(mainPanel)

        gamePanel.preferredSize = Dimension(gameWidth, gameHeight)
        gamePanel.setBounds(0, 0, gameWidth, gameHeight)
        gamePanel.isVisible = false
        gamePanel.layout = null

        val gameInterfacePanelWidth = 128
        gameRenderPanel.setBounds(0, 0, gameWidth - gameInterfacePanelWidth, gameHeight)
        gamePanel.add(gameRenderPanel)
        gameInterfacePanel.setBounds(gameWidth - 128, 0, gameInterfacePanelWidth, gameHeight)
        gamePanel.add(gameInterfacePanel)
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
            }

            override fun keyReleased(e: KeyEvent) {
                isControlDown = e.isControlDown
                G.keyClicked(e)
            }
        })
        val mouseAdapter = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                G.mouseClicked(e)
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
            }

            override fun mouseMoved(e: MouseEvent) = mouseMoved_(e)

            override fun mouseDragged(e: MouseEvent) = mouseMoved_(e)
        }
        mainPanel.addMouseListener(mouseAdapter)
        mainPanel.addMouseMotionListener(mouseAdapter)

        val dim = Toolkit.getDefaultToolkit().screenSize
        setLocation(dim.width / 2 - size.width / 2, dim.height / 2 - size.height / 2)
    }

    var mPos: Vector = Vector(0, 0)
        private set
        get() {
            val tmp = gamePanel.mousePosition
            if (tmp != null) {
                field = tmp.toVector
            }
            return field
        }

    class GamePanel : JPanel() {
        override fun paint(g: Graphics) {
            G.paint(g)
        }
    }
}