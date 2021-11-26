import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.text.StyleConstants
import javax.swing.text.StyleConstants.setBackground


class GameMenu {
    //во имя главного меню
    val MenuPanel = ImagePanel("/KEKStrategyGit/src/GraphicsRes/MenuPic.jpg")
    val MenuLabel = JLabel("KEKStrategy")
    var _1vs1Button = JButton("Player vs Player")
    var _1vsPCButton = JButton("Player vs PC")
    var actLis = MenuButtonActionListener()

    //во имя окна выбора карты
    val MapChoosePanel = ImagePanel("/KEKStrategyGit/src/GraphicsRes/MenuPic.jpg")
    val MapChooseLabel = JLabel("Choose your map")
    val MapChooseButtons = listOf(JButton(),JButton(),JButton(),JButton(), JButton())


    //во имя меню паузы
    val PausePanel = JPanel()
    val PauseLabel = JLabel("Pause")
    var ToMainMenuButton = JButton("Main Menu")
    var ContinueButton = JButton("Continue")

    fun FirstPaint(winWidth: Int, winHeight: Int){
        //Сборка панели главного меню
        MenuPanel.setLayout(null)
        MenuPanel.preferredSize = Dimension(winWidth,winHeight)
        MenuPanel.setOpaque(true)

        MenuLabel.setBounds(winWidth/3, winHeight/3 - 100, winWidth/2, winHeight/10)
        MenuLabel.setFont(Font(MenuLabel.getFont().getName(), Font.ITALIC, 40))

        _1vs1Button.setBounds(winWidth/3, winHeight/3, winWidth/3, winHeight/10)
        _1vsPCButton.setBounds(winWidth/3, winHeight/3 + 100, winWidth/3, winHeight/10)
        _1vs1Button.setActionCommand("pvp")
        _1vsPCButton.setActionCommand("pve")
        _1vs1Button.addActionListener(actLis)
        _1vsPCButton.addActionListener(actLis)

        MenuPanel.add(MenuLabel)
        MenuPanel.add(_1vs1Button)
        MenuPanel.add(_1vsPCButton)

        //Сборка панели выбора карты

        MapChoosePanel.setLayout(null)
        MapChoosePanel.preferredSize = Dimension(winWidth,winHeight)
        MapChoosePanel.setOpaque(true)

        MapChooseLabel.setBounds(winWidth/3, winHeight/3 - 100, winWidth/2, winHeight/10)
        MapChooseLabel.setFont(Font(MenuLabel.getFont().getName(), Font.ITALIC, 30))

        for ((index,MapButton) in MapChooseButtons.withIndex()) {
            MapButton.setBounds(winWidth/10 + 100*index, winHeight/3, winWidth/10, winHeight/10)
            MapButton.addActionListener(actLis)
            MapButton.setActionCommand(index.toString())

            MapChoosePanel.add(MapButton)
        }

        MapChoosePanel.add(MapChooseLabel)

        //Сборка панели меню паузы
        PausePanel.setLayout(null)
        PausePanel.preferredSize = Dimension(winWidth,winHeight)
        PausePanel.setOpaque(true)
        PausePanel.setBackground(Color(255, 255, 255, 30))

        PauseLabel.setBounds(winWidth/3, winHeight/3 - 100, winWidth/2, winHeight/10)
        PauseLabel.setFont(Font(MenuLabel.getFont().getName(), Font.ITALIC, 30))
        PauseLabel.setForeground(Color.blue)

        ToMainMenuButton.setBounds(winWidth/3, winHeight/3, winWidth/3, winHeight/10)
        ContinueButton.setBounds(winWidth/3, winHeight/3 + 100, winWidth/3, winHeight/10)
        ToMainMenuButton.setActionCommand("continue")
        ContinueButton.setActionCommand("ExitToMainMenu")
        ToMainMenuButton.addActionListener(actLis)
        ContinueButton.addActionListener(actLis)

        PausePanel.add(PauseLabel)
        PausePanel.add(ToMainMenuButton)
        PausePanel.add(ContinueButton)

        StandartVisible()
    }
    fun StandartVisible(){
        G.menu.MenuPanel.setVisible(true)
        G.menu.MapChoosePanel.setVisible(false)
        G.menu.PausePanel.setVisible(false)
    }
    fun keyCliked(ev: KeyEvent) {
        when (ev.keyCode) {
            KeyEvent.VK_ESCAPE -> {
                    G.menu.PausePanel.setVisible(true)
                    println("kek")
                    println(G.menu.PausePanel.isVisible)
            }
        }
    }
}
class MenuButtonActionListener : ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        when(e.getActionCommand()){
            "pvp" -> {
                G.menu.MenuPanel.setVisible(false)
                G.menu.MapChoosePanel.setVisible(true)
            }
            "pve" ->{

            }
            "continue" ->{
                G.menu.PausePanel.setVisible(false)
            }
            "ExitToMainMenu" ->{
                G.state = G.State.Menu
                G.menu.StandartVisible()
            }
            else -> {
                G.state = G.State.Play
                //G.mapNum = e.getActionCommand().toInt() Отправка номера выбаной карты
                G.menu.MapChoosePanel.setVisible(false)
                G.win.Panel.setVisible(true)
            }
        }
    }
}