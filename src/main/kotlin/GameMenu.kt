import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.Border


class GameMenu(winWidth: Int, winHeight: Int) {
    //во имя главного меню
    val menuPanel = ImagePanel("./src/main/kotlin/resource/graphics/KekStratMenuPic.png")
    //private val menuLabel = JLabel("KEKStrategy")

    //#660800 dark red
    //#9a0c00 red
    //#9e9e9e gray
    //#5f5d5d dark gray
    private var pVsPButton = GameButton("<html><h2>Player vs Player", true)
    private var pVsEButton = GameButton("<html><h2>Player vs PC", true)
    private var ExitButton = GameButton("<html><h2>Exit", true)

    //во имя окна выбора карты
    val mapChoosePanel = ImagePanel("./src/main/kotlin/resource/graphics/KekStratMapChoosePic.png")
   // private val mapChooseLabel = JLabel("Choose your map")
    private val mapChooseButtons = listOf(GameButton(null, true), GameButton(null, true), GameButton(null, true), GameButton(null, true), GameButton(null, true))

    //во имя меню паузы
    val pausePanel = JPanel()
    private val pauseLabel = JLabel("<html><font color=\"#9a0c00 \">Pause")
    private var toMainMenuButton = GameButton("<html><h2>Main Menu", false)
    private var continueButton = GameButton("<html><h2>Continue", false)

    init {
        pack(winWidth, winHeight)
    }

    //В девичестве FirstPaint (чем тоби FirstPaint не устроил, нормальное же название)
    private fun pack(winWidth: Int, winHeight: Int) {
        //Сборка панели главного меню
        menuPanel.layout = null
        menuPanel.preferredSize = Dimension(winWidth, winHeight)
        menuPanel.isOpaque = true

       // menuLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
       // menuLabel.font = Font(menuLabel.font.name, Font.ITALIC, 40)

        pVsPButton.setBounds(winWidth / 13, winHeight / 2, winWidth / 3, winHeight / 10)
        pVsEButton.setBounds(winWidth / 13, winHeight / 2 + winHeight / 8, winWidth / 3, winHeight / 10)
        ExitButton.setBounds(winWidth / 13, winHeight / 2 + 2*winHeight / 8, winWidth / 3, winHeight / 10)

        pVsPButton.actionCommand = "pvp"
        pVsEButton.actionCommand = "pve"
        ExitButton.actionCommand = "Exit"

        pVsPButton.isFocusable = false
        pVsEButton.isFocusable = false
        ExitButton.isFocusable = false


        //pVsPButton.background = Color.GRAY


        val actLis = ActionListener { e ->
            when (e.actionCommand) {
                //Кнопачки главного меню
                "pvp" -> {
                    menuPanel.isVisible = false
                    mapChoosePanel.isVisible = true
                }
                "pve" -> {

                }
                "Exit" -> {
                    G.win.dispose()
                }
                //Кнопачки меню паузы
                "continue" -> {
                    G.state = G.State.Play
                    pausePanel.isVisible = false
                }
                "ExitToMainMenu" -> {
                    G.state = G.State.Menu
                    G.win.gamePanel.isVisible = false
                    standardVisible()
                    G.startGame()
                }
                //Кнопачки меню выбора карт
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
        ExitButton.addActionListener(actLis)

      //  menuPanel.add(menuLabel)
        menuPanel.add(pVsPButton)
        menuPanel.add(pVsEButton)
        menuPanel.add(ExitButton)

        //Сборка панели выбора карты
        mapChoosePanel.layout = null
        mapChoosePanel.preferredSize = Dimension(winWidth, winHeight)
        mapChoosePanel.isOpaque = true

     //   mapChooseLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
     //   mapChooseLabel.font = Font(menuLabel.font.name, Font.ITALIC, 30)

        for ((index, mapButton) in mapChooseButtons.withIndex()) {
            mapButton.setBounds(winWidth / 50 + winWidth / 5 * index, winHeight / 2, winWidth / 6, winHeight / 6)
            mapButton.addActionListener(actLis)
            mapButton.isFocusable = false
            mapButton.actionCommand = index.toString()

            mapChoosePanel.add(mapButton)
        }

      //  mapChoosePanel.add(mapChooseLabel)

        //Сборка панели меню паузы
        pausePanel.layout = null
        pausePanel.preferredSize = Dimension(winWidth, winHeight)
        pausePanel.isOpaque = true
        pausePanel.background = Color(255, 255, 255, 30)

        pauseLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
        pauseLabel.font = Font(pauseLabel.font.name, Font.ITALIC, 30)
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
                G.state = G.State.Menu
            }
        }
    }
}

private class RoundedBorder internal constructor(private val radius: Int) : Border {
    override fun getBorderInsets(c: Component?): Insets {
        return Insets(radius + 1, radius + 1, radius + 2, radius)
    }

    override fun isBorderOpaque(): Boolean {
        return true
    }

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }
}