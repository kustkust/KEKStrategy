package gameinterface

import game.G
import game.map.GameMap
import utility.C
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

class GameMenu(winWidth: Int, winHeight: Int) {
    //во имя главного меню
    val menuPanel = ImagePanel("${C.Paths.menu}/KekStratMenuPic.png")
    //private val menuLabel = JLabel("KEKStrategy")

    //#660800 dark red
    //#9a0c00 red
    //#9e9e9e gray
    //#5f5d5d dark gray
    private var pVsPButton = GameButton("Player vs Player")
    private var pVsEButton = GameButton("Player vs PC")
    private var editMapButton = GameButton("Edit Map")
    private var exitButton = GameButton("Exit")
    private var muteButton = GameButton()

    //во имя окна выбора карты
    val mapChoosePanel = ImagePanel("${C.Paths.menu}/KekStratMapChoosePic.png")

    // private val mapChooseLabel = JLabel("Choose your map")
    private val mapChooseButtons = listOf(
        GameButton(null, true),
        GameButton(null, true),
        GameButton(null, true),
        GameButton(null, true),
        GameButton(null, true)
    )
    var mapDescriptions = listOf(
        "Description here", //1 map
        "Description here", //2 map
        "Description here", //3 map
        "Description here", //4 map
        "Description here"  //5 map
    )

    var muteButtonImg = listOf(
        ImageIcon("${C.Paths.menu}/Mute1.png"), // RedOn
        ImageIcon("${C.Paths.menu}/Mute2.png"), //1 DarkRedON
        ImageIcon("${C.Paths.menu}/Mute3.png"), //2 GrayOn
        ImageIcon("${C.Paths.menu}/Mute4.png"), //3 RedOff
        ImageIcon("${C.Paths.menu}/Mute5.png"),  //4 DarkRedOff
        ImageIcon("${C.Paths.menu}/Mute6.png"),  //4 GrayOff
    )

    //во имя меню паузы
    val pausePanel = JPanel()
    private val pauseLabel = JLabel()
    private var toMainMenuButton = GameButton("Main Menu", false)
    private var continueButton = GameButton("Continue", false)

    //во имя победы(меню победы)
    //val winLabel = JLabel()

    init {
        pack(winWidth, winHeight)
    }

    private fun pack(winWidth: Int, winHeight: Int) {
        //Сборка панели главного меню
        menuPanel.layout = null
        menuPanel.preferredSize = Dimension(winWidth, winHeight)
        menuPanel.setBounds(0, 0, winWidth, winHeight)
        menuPanel.isOpaque = true

        // menuLabel.setBounds(winWidth / 3, winHeight / 3 - 100, winWidth / 2, winHeight / 10)
        // menuLabel.font = Font(menuLabel.font.name, Font.ITALIC, 40)

        pVsPButton.setBounds(winWidth / 13, winHeight / 3 + 50, winWidth / 3, winHeight / 10)
        pVsEButton.setBounds(winWidth / 13, winHeight / 3 + winHeight / 8 + 50, winWidth / 3, winHeight / 10)
        editMapButton.setBounds(winWidth / 13, winHeight / 3 + 2 * winHeight / 8 + 50, winWidth / 3, winHeight / 10)
        exitButton.setBounds(winWidth / 13, winHeight / 3 + 3 * winHeight / 8 + 50, winWidth / 3, winHeight / 10)
        muteButton.setBounds(winWidth - 70, winHeight - 70, winWidth / 16, winHeight / 12)

        editMapButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                G.state = G.State.EditMap
                G.map = GameMap()
                G.map.fogOfWar = false
                G.map.generateMap(20, 20)
                G.animationManager.start()
                menuPanel.isVisible = false
                mapChoosePanel.isVisible = false
                G.win.gamePanel.isVisible = true
            }
        })

        pVsPButton.actionCommand = "pvp"
        pVsEButton.actionCommand = "pve"
        exitButton.actionCommand = "exit"
        //  MuteButton.actionCommand = "mute"

        muteButton.icon = muteButtonImg[0]
        //Изменение иконки кнопки мута музыки в зависимости от состояния
        muteButton.addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                if (G.musicState == G.MusicState.On) {
                    muteButton.icon = muteButtonImg[1]
                } else {
                    muteButton.icon = muteButtonImg[4]
                }
                super.mouseEntered(e)
            }

            override fun mouseExited(e: MouseEvent?) {
                if (G.musicState == G.MusicState.On) {
                    muteButton.icon = muteButtonImg[0]
                } else {
                    muteButton.icon = muteButtonImg[3]
                }
                super.mouseExited(e)
            }

            override fun mousePressed(e: MouseEvent?) {
                if (G.musicState == G.MusicState.On) {
                    muteButton.icon = muteButtonImg[2]
                } else {
                    muteButton.icon = muteButtonImg[5]
                }
                super.mousePressed(e)
            }

            override fun mouseReleased(e: MouseEvent?) {
                if (G.musicState == G.MusicState.On) {
                    muteButton.icon = muteButtonImg[0]
                } else {
                    muteButton.icon = muteButtonImg[3]
                }
                super.mouseReleased(e)
            }

            override fun mouseClicked(e: MouseEvent?) {
                if (G.musicState == G.MusicState.On) {
                    G.musicState = G.MusicState.Off
                    muteButton.icon = muteButtonImg[4]
                } else {
                    G.musicState = G.MusicState.On
                    muteButton.icon = muteButtonImg[1]
                }
                G.playMusic()
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
                    //G.startGame()
                    G.playMusic()
                }
                //Кнопачки меню выбора карт
                else -> {
                    //G.selectedMapId = e.actionCommand.toInt()
                    G.startGame()
                    mapChoosePanel.isVisible = false
                    G.win.gamePanel.isVisible = true
                    G.playMusic()
                }
            }
        }

        pVsPButton.addActionListener(actLis)
        pVsEButton.addActionListener(actLis)
        exitButton.addActionListener(actLis)

        menuPanel.add(pVsPButton)
        menuPanel.add(pVsEButton)
        menuPanel.add(editMapButton)
        menuPanel.add(exitButton)
        menuPanel.add(muteButton)

        //Сборка панели выбора карты
        mapChoosePanel.layout = null
        mapChoosePanel.preferredSize = Dimension(winWidth, winHeight)
        mapChoosePanel.setBounds(0, 0, winWidth, winHeight)
        mapChoosePanel.isOpaque = true

        val mapText = GameLabel()

        mapText.setBounds(
            winWidth / 50 + winWidth / 6,
            winHeight / 2 + winHeight / 5 - 10,
            winWidth / 2 + winWidth / 8,
            winHeight / 2 - 130
        )
        mapText.isVisible = false
        mapChoosePanel.add(mapText)

        for ((index, mapButton) in mapChooseButtons.withIndex()) {
            mapButton.setBounds(winWidth / 50 + winWidth / 5 * index, winHeight / 2, winWidth / 6, winHeight / 6)
            mapButton.addActionListener(actLis)

            mapButton.addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    mapText.isVisible = true
                    mapText.text = mapDescriptions[index]
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
        pausePanel.setBounds(0, 0, winWidth, winHeight)
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

        //Куём победу(меню победы)


        standardVisible()
    }

    private fun standardVisible() {
        menuPanel.isVisible = true
        mapChoosePanel.isVisible = false
        pausePanel.isVisible = false

        pauseLabel.text = "<html><font color=\"#9a0c00 \">Pause"
        continueButton.isVisible = true
    }

    fun showWinner(curPlID: String, curPlCol: Color) {
        pauseLabel.foreground = curPlCol
        pauseLabel.text = "Player $curPlID Win"
        continueButton.isVisible = false
        pausePanel.isVisible = true
    }

    fun keyClicked(ev: KeyEvent) {
        when (ev.keyCode) {
            KeyEvent.VK_ESCAPE -> {
                pausePanel.isVisible = true
            }
        }
    }
}

