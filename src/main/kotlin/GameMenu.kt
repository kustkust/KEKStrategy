import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel


class GameMenu(winWidth: Int, winHeight: Int) {
    //во имя главного меню
    val menuPanel = ImagePanel("/KEKStrategyGit/src/GraphicsRes/MenuPic.jpg")
    private val menuLabel = JLabel("KEKStrategy")
    private var pVsPButton = JButton("Player vs Player")
    private var pVsEButton = JButton("Player vs PC")

    //во имя окна выбора карты
    val mapChoosePanel = ImagePanel("/KEKStrategyGit/src/GraphicsRes/MenuPic.jpg")
    private val mapChooseLabel = JLabel("Choose your map")
    private val mapChooseButtons = listOf(JButton(), JButton(), JButton(), JButton(), JButton())

    //во имя меню паузы
    val pausePanel = JPanel()
    private val pauseLabel = JLabel("Pause")
    private var toMainMenuButton = JButton("Main Menu")
    private var continueButton = JButton("Continue")

    init {
        pack(winWidth, winHeight)
    }

    //В девичестве FirstPaint
    private fun pack(winWidth: Int, winHeight: Int) {
        //Сборка панели главного меню
        menuPanel.layout = null
        menuPanel.preferredSize = Dimension(winWidth, winHeight)
        menuPanel.isOpaque = true

        menuLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
        menuLabel.font = Font(menuLabel.font.name, Font.ITALIC, 40)

        pVsPButton.setBounds(winWidth / 3, winHeight / 3, winWidth / 3, winHeight / 10)
        pVsEButton.setBounds(winWidth / 3, winHeight / 3 + 100, winWidth / 3, winHeight / 10)
        pVsPButton.actionCommand = "pvp"
        pVsEButton.actionCommand = "pve"
        pVsPButton.isFocusable = false
        pVsEButton.isFocusable = false

        val actLis = ActionListener { e ->
            when (e.actionCommand) {
                "pvp" -> {
                    menuPanel.isVisible = false
                    mapChoosePanel.isVisible = true
                }
                "pve" -> {

                }
                "continue" -> {
                    pausePanel.isVisible = false
                    G.win.gamePanel.isVisible = true
                }
                "ExitToMainMenu" -> {
                    G.state = G.State.Menu
                    standardVisible()
                }
                else -> {
                    G.state = G.State.Play
                    //G.mapNum = e.getActionCommand().toInt() Отправка номера выбранной карты
                    mapChoosePanel.isVisible = false
                    G.win.gamePanel.isVisible = true
                }
            }
        }

        pVsPButton.addActionListener(actLis)
        pVsEButton.addActionListener(actLis)

        menuPanel.add(menuLabel)
        menuPanel.add(pVsPButton)
        menuPanel.add(pVsEButton)

        //Сборка панели выбора карты
        mapChoosePanel.layout = null
        mapChoosePanel.preferredSize = Dimension(winWidth, winHeight)
        mapChoosePanel.isOpaque = true

        mapChooseLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
        mapChooseLabel.font = Font(menuLabel.font.name, Font.ITALIC, 30)

        for ((index, mapButton) in mapChooseButtons.withIndex()) {
            mapButton.setBounds(winWidth / 10 + 100 * index, winHeight / 3, winWidth / 10, winHeight / 10)
            mapButton.addActionListener(actLis)
            mapButton.isFocusable = false
            mapButton.actionCommand = index.toString()

            mapChoosePanel.add(mapButton)
        }

        mapChoosePanel.add(mapChooseLabel)

        //Сборка панели меню паузы
        pausePanel.layout = null
        pausePanel.preferredSize = Dimension(winWidth, winHeight)
        pausePanel.isOpaque = true
        pausePanel.background = Color(255, 255, 255, 30)

        pauseLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
        pauseLabel.font = Font(menuLabel.font.name, Font.ITALIC, 30)
        pauseLabel.foreground = Color.blue

        toMainMenuButton.setBounds(winWidth / 3, winHeight / 3, winWidth / 3, winHeight / 10)
        continueButton.setBounds(winWidth / 3, winHeight / 3 + 100, winWidth / 3, winHeight / 10)
        toMainMenuButton.actionCommand = "ExitToMainMenu"
        continueButton.actionCommand = "continue"
        toMainMenuButton.addActionListener(actLis)
        continueButton.addActionListener(actLis)
        toMainMenuButton.isFocusable = false
        continueButton.isFocusable = false

        pausePanel.add(pauseLabel)
        pausePanel.add(toMainMenuButton)
        pausePanel.add(continueButton)

        standardVisible()
    }

    private fun standardVisible() {
        menuPanel.isVisible = true
        mapChoosePanel.isVisible = false
        pausePanel.isVisible = false
    }

    fun keyClicked(ev: KeyEvent) {
        when (ev.keyCode) {
            KeyEvent.VK_ESCAPE -> {
                pausePanel.isVisible = true
                G.win.gamePanel.isVisible = false
            }
        }
    }
}
