package game

import game.entities.builds.PlayerBase
import game.map.Cell
import game.map.GameMap
import gameinterface.MainWindow
import graphics.AnimationManager
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utility.C
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import kotlin.math.round

object G {
    /**
     * Игровая карта
     */
    lateinit var map: GameMap

    enum class State {
        EditMap,
        Menu,
        Play,
        Win,
    }

    var state = State.Menu

    val animationManager = AnimationManager()

    private var selectedCellType = Cell.Type.Water

    /**
     * Список игроков
     */
    lateinit var players: Array<Player>

    /**
     * Номер текущего игрока
     */
    private var curPlayerId = 0

    /**
     * Текущий игрок
     */
    val curPlayer
        get() = players[curPlayerId]

    /**
     * Основное окно в котором происходит вся отрисовка
     */
    lateinit var win: MainWindow

    var selectedMapId = -1

    /*Игровая логика*/

    var tmp: BufferedImage? = null

    fun startGame() {
        curPlayerId = 0

        if (selectedMapId == -1) {
            map = GameMap()
            map.generateMap(40, 20)
        } else {
            val s = File("${C.Paths.maps}/$selectedMapId.json").bufferedReader().readText()
            map = Json.decodeFromString(s)
            map.initPLayers()
        }
        animationManager.start()
        curPlayer.newTurn()
        state = State.Play

        win.gameInterfacePanel.init()
        win.gameInterfacePanel.setupForPlayer(curPlayer)
    }

    private fun endTurn() {
        if (!curPlayer.endTurn()) {
            do {
                curPlayerId++
                curPlayerId %= players.size
            } while (curPlayer.isLoose)
            map.centerOn(curPlayer.selectedEntity ?: curPlayer.getEntitiesOf<PlayerBase>()[0])
            curPlayer.newTurn()
            win.gameInterfacePanel.setupForPlayer(curPlayer)
        }
    }

    fun checkWin() {
        if (players.all { it.isLoose || it == curPlayer }) {
            state = State.Win
        }
    }

    /*Отрисовка*/

    /**
     * Рисует игру
     */
    fun paint(g: Graphics) {
        when (state) {
            State.Play -> {
                paintGame(g)
                //g.drawImage(tmp,0,0,null)
            }
            State.Win -> {
                win.menu.showWinner(curPlayer.name, curPlayer.color)
            }
            State.EditMap -> {
                map.paint(g)
                g.color = Color.gray
                g.fillRect(0, 0, map.cs + 4, map.cs + 4)
                g.color = selectedCellType.color
                g.fillRect(2, 2, map.cs, map.cs)
                g.color = Color(g.color.red, g.color.green, g.color.blue, 128)
                g.color = Color.red
                g.drawString("${win.isLeftButtonDown}", 10, 10)
            }
            else -> {

            }
        }
    }

    private fun paintGame(g: Graphics) {
        g.clearRect(0, 0, map.width * map.cs, map.height * map.cs)
        map.paint(g)
        curPlayer.paint(g)
        g.color = Color.black
        g.drawString(
            "${round(1000f / if (animationManager.delta == 0L) 1 else animationManager.delta).toInt()}fps",
            win.innerSize.x - 50,
            g.font.size
        )
    }

    /*Управление*/

    fun mousePressed(ev: MouseEvent) {

    }

    /**
     * Обработка нажатий кнопок мыши
     */
    fun mouseClicked(ev: MouseEvent) {
        when (state) {
            State.Play -> {
                curPlayer.mouseClicked(ev)
            }
            State.EditMap -> {
                map.selectedCell.type = selectedCellType
            }
            else -> {

            }
        }
    }

    /**
     * Обработка движений мыши
     */
    fun mouseMoved(ev: MouseEvent) {
        when (state) {
            State.Play -> {
                map.mouseMoved(ev)
                curPlayer.mouseMoved(ev)
            }
            State.EditMap -> {
                map.mouseMoved(ev)
                if (win.isLeftButtonDown) {
                    map.selectedCell.type = selectedCellType
                }
            }
            else -> {

            }
        }
    }

    /**
     * Обработка нажатий клавиш на клавиатуре
     */
    fun keyClicked(ev: KeyEvent) {
        when (state) {
            State.Play -> {
                when (ev.keyCode) {
                    KeyEvent.VK_SPACE -> endTurn()
                }
                curPlayer.keyClicked(ev)
                map.keyClicked(ev)
                win.menu.keyClicked(ev)
            }
            State.Win -> {
                when (ev.keyCode) {
                    KeyEvent.VK_SPACE -> startGame()
                }
            }
            State.EditMap -> {
                if (ev.keyCode - KeyEvent.VK_1 in 0 until Cell.Type.values().size) {
                    selectedCellType = Cell.Type.values()[ev.keyCode - KeyEvent.VK_1]
                }
            }
            else -> {

            }
        }
    }

    fun keyPressed(ev: KeyEvent) {
        when (state) {
            State.Play -> {
                map.keyPressed(ev)
            }
            State.EditMap -> {
                map.keyPressed(ev)
            }
            else -> {

            }
        }
    }

    /*Музыка*/

    enum class MusicState {
        On,
        Off
    }

    var musicState = MusicState.On

    private val menuMusic = AudioSystem.getAudioInputStream(File("${C.Paths.music}/Adding-the-Sun.wav"))
    private val gameMusic = AudioSystem.getAudioInputStream(File("${C.Paths.music}/Pleasant-Porridge.wav"))
    private val menuMusicClip = AudioSystem.getClip()
    private val gameMusicClip = AudioSystem.getClip()

    fun playMusic() {
        menuMusicClip.stop()
        gameMusicClip.stop()
        if (musicState == MusicState.On) {
            if (state == State.Menu) {
                menuMusicClip.framePosition = 0
                menuMusicClip.start()
                menuMusicClip.loop(Clip.LOOP_CONTINUOUSLY)
            } else {
                gameMusicClip.framePosition = 0
                gameMusicClip.start()
                menuMusicClip.loop(Clip.LOOP_CONTINUOUSLY)
            }
        }
    }

    /**
     * Нужен для запуска программы, вызывается в первую очередь
     */
    @JvmStatic
    fun main(a: Array<String>) {
        win = MainWindow()

        menuMusicClip.open(menuMusic)
        gameMusicClip.open(gameMusic)
        playMusic()
    }
}