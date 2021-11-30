package gameinterface

import game.G
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.swing.*



class GameMenu(winWidth: Int, winHeight: Int) {


    //во имя главного меню
    val menuPanel = ImagePanel("./src/main/resources/graphics/menuImages/KekStratMenuPic.png")
    //private val menuLabel = JLabel("KEKStrategy")

    //#660800 dark red
    //#9a0c00 red
    //#9e9e9e gray
    //#5f5d5d dark gray
    private var pVsPButton = GameButton("Player vs Player")
    private var pVsEButton = GameButton("Player vs PC")
    private var editMapButton = GameButton("Edit Map")
    private var exitButton = GameButton("Exit")
    private var MuteButton = GameButton()

    //во имя окна выбора карты
    val mapChoosePanel = ImagePanel("./src/main/resources/graphics/menuImages/KekStratMapChoosePic.png")

    // private val mapChooseLabel = JLabel("Choose your map")
    private val mapChooseButtons = listOf(
        GameButton(null, true),
        GameButton(null, true),
        GameButton(null, true),
        GameButton(null, true),
        GameButton(null, true)
    )
    var MapDescriptions = listOf(
        "Description here", //1 map
        "Description here", //2 map
        "Description here", //3 map
        "Description here", //4 map
        "Description here"  //5 map
    )

    var MuteButtonImg = listOf(
        ImageIcon("src/main/resources/graphics/menuImages/Mute1.png"), // RedOn
        ImageIcon("src/main/resources/graphics/menuImages/Mute2.png"), //1 DarkRedON
        ImageIcon("src/main/resources/graphics/menuImages/Mute3.png"), //2 GrayOn
        ImageIcon("src/main/resources/graphics/menuImages/Mute4.png"), //3 RedOff
        ImageIcon("src/main/resources/graphics/menuImages/Mute5.png"),  //4 DarkRedOff
        ImageIcon("src/main/resources/graphics/menuImages/Mute6.png"),  //4 GrayOff
    )

    //во имя меню паузы
    val pausePanel = JPanel()
    private val pauseLabel = JLabel("<html><font color=\"#9a0c00 \">Pause")
    private var toMainMenuButton = GameButton("Main Menu", false)
    private var continueButton = GameButton("Continue", false)

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

        pVsPButton.setBounds(winWidth / 13, winHeight / 3 + 50, winWidth / 3, winHeight / 10)
        pVsEButton.setBounds(winWidth / 13, winHeight / 3 + winHeight / 8 + 50, winWidth / 3, winHeight / 10)
        editMapButton.setBounds(winWidth / 13, winHeight / 3 + 2 * winHeight / 8 + 50, winWidth / 3, winHeight / 10)
        exitButton.setBounds(winWidth / 13, winHeight / 3 + 3 * winHeight / 8 + 50, winWidth / 3, winHeight / 10)
        MuteButton.setBounds(winWidth - 70, winHeight - 70, winWidth / 16, winHeight / 12)

        editMapButton.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                G.state = G.State.EditMap
                G.map.fogOfWar = false
                //game.G.mapNum = e.getActionCommand().toInt() Отправка номера выбранной карты
                menuPanel.isVisible = false
                mapChoosePanel.isVisible = false
                G.win.gamePanel.isVisible = true
            }
        })

        pVsPButton.actionCommand = "pvp"
        pVsEButton.actionCommand = "pve"
        exitButton.actionCommand = "exit"
      //  MuteButton.actionCommand = "mute"

        MuteButton.icon = MuteButtonImg[0]
        //Изменение иконки кнопки мута музыки в зависимости от состояния
        MuteButton.addMouseListener(object: MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                if(G.musicState == G.MusicState.on) {
                    MuteButton.icon = MuteButtonImg[1]
                }else{
                    MuteButton.icon = MuteButtonImg[4]
                }
                super.mouseEntered(e)
            }
            override fun mouseExited(e: MouseEvent?) {
                if(G.musicState == G.MusicState.on) {
                    MuteButton.icon = MuteButtonImg[0]
                }else{
                    MuteButton.icon = MuteButtonImg[3]
                }
                super.mouseExited(e)
            }

            override fun mousePressed(e: MouseEvent?) {
                if(G.musicState == G.MusicState.on) {
                    MuteButton.icon = MuteButtonImg[2]
                }else{
                    MuteButton.icon = MuteButtonImg[5]
                }
                super.mousePressed(e)
            }
            override fun mouseReleased(e: MouseEvent?) {
                if(G.musicState == G.MusicState.on) {
                    MuteButton.icon = MuteButtonImg[0]
                }else{
                    MuteButton.icon = MuteButtonImg[3]
                }
                super.mouseReleased(e)
            }
            override fun mouseClicked(e: MouseEvent?) {
                if(G.musicState == G.MusicState.on) {
                    G.musicState = G.MusicState.off
                    MuteButton.icon = MuteButtonImg[4]
                }else{
                    G.musicState = G.MusicState.on
                    MuteButton.icon = MuteButtonImg[1]
                }
                G.PlayMusic()
                super.mouseClicked(e)
            }
        })


        val actLis = ActionListener { e ->
            when (e.actionCommand) {
                //Кнопачки главного меню
                "pvp" -> {
                    menuPanel.isVisible = false
                    mapChoosePanel.isVisible = true
                }
                "pve" -> {

                }
                "exit" -> {
                    G.win.dispose()
                }
                //Кнопачки меню паузы
                "continue" -> {
                    pausePanel.isVisible = false
                }
                "ExitToMainMenu" -> {
                    G.state = G.State.Menu
                    G.win.gamePanel.isVisible = false
                    standardVisible()
                    G.startGame()
                    G.PlayMusic()
                }
                //Кнопачки меню выбора карт
                else -> {
                    G.state = G.State.Play
                    //game.G.mapNum = e.getActionCommand().toInt() Отправка номера выбранной карты
                    mapChoosePanel.isVisible = false
                    G.win.gamePanel.isVisible = true
                    G.PlayMusic()
                }
            }
        }

        pVsPButton.addActionListener(actLis)
        pVsEButton.addActionListener(actLis)
        exitButton.addActionListener(actLis)
       // MuteButton.addActionListener(actLis)

        //  menuPanel.add(menuLabel)
        menuPanel.add(pVsPButton)
        menuPanel.add(pVsEButton)
        menuPanel.add(editMapButton)
        menuPanel.add(exitButton)
        menuPanel.add(MuteButton)

        //Сборка панели выбора карты
        mapChoosePanel.layout = null
        mapChoosePanel.preferredSize = Dimension(winWidth, winHeight)
        mapChoosePanel.isOpaque = true

        val mapText = GameLabel()

        mapText.setBounds(winWidth / 50 + winWidth / 6, winHeight / 2 + winHeight / 5 - 10, winWidth / 2 + winWidth / 8, winHeight/2 - 130)
        mapText.isVisible = false
        mapChoosePanel.add(mapText)


        for ((index, mapButton) in mapChooseButtons.withIndex()) {
            mapButton.setBounds(winWidth / 50 + winWidth / 5 * index, winHeight / 2, winWidth / 6, winHeight / 6)
            mapButton.addActionListener(actLis)



            mapButton.addMouseListener(object: MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    mapText.isVisible = true
                    mapText.text = MapDescriptions[index]
                    super.mouseEntered(e)
                }
                override fun mouseExited(e: MouseEvent?) {
                    mapText.isVisible = false
                    super.mouseEntered(e)
                }
            })

            mapButton.actionCommand = index.toString()

            mapChoosePanel.add(mapButton)
        }


       /* val mapName = JLabel()
        val mapText = JTextPane()
        val startButton = GameButton("<html><font color=\"#9a0c00 \">Start Game")
        val mapList = JList<String>()*/
/*

        mapName.setBounds(winWidth / 2 + 30, winHeight / 2, winWidth / 2 - 30, 30)
        mapChoosePanel.add(mapName)

        mapText.setBounds(winWidth / 2 + 30, winHeight / 2 + 30, winWidth / 2 - 30, winHeight/2 - 130)
        mapText.isEditable = false
        mapChoosePanel.add(mapText)

        startButton.setBounds(30, winHeight - 60, 400, 30)
        startButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                //game.G.mapNum = e.getActionCommand().toInt() Отправка номера выбранной карты
                G.state = G.State.Play
                mapChoosePanel.isVisible = false
                G.win.gamePanel.isVisible = true
            }
        })
        startButton.isEnabled = false
        mapChoosePanel.add(startButton)

        mapList.setBounds(30, winHeight / 2 + 30, winWidth / 2 - 30, winHeight / 2 - 130)
        mapList.isFocusable = false
        mapList.addListSelectionListener {
            mapName.text = "Map ${mapList.selectedIndex + 1}"
            mapText.text = "Description Map ${mapList.selectedIndex + 1}"
            startButton.isEnabled = true
        }
        val maps = DefaultListModel<String>()
        for (i in 1..7) {
            maps.add(i - 1, "map $i")
        }
        mapList.model = maps
        mapChoosePanel.add(mapList)
*/

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


        pausePanel.add(pauseLabel)
        pausePanel.add(toMainMenuButton)
        pausePanel.add(continueButton)
        //pausePanel.add(MuteButton)

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
            }
        }
    }
}

