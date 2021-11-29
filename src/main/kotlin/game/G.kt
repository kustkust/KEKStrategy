package game

import gameinterface.MainWindow
import utilite.Vector
import graphics.AnimationManager
import game.entities.Barracks
import game.entities.MeleeUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

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

    val animationManager = AnimationManager()

    var selectedCellType = Cell.Type.Water

    var state = State.Menu

    /**
     * Список игроков
     */
    private lateinit var players: Array<Player>

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
     * Костыль, другие методы могут добавить сюда метод отрисовки чего либо, но
     * отрисовано оно будет лишь раз, так как после каждой отрисовки список отчищается.
     * Наверное стоит убрать
     */
    val drawTask = mutableListOf<(Graphics) -> Unit>()

    init {
        startGame()
        animationManager.start()
    }

    fun startGame() {
        state = State.Menu
        curPlayerId = 0

        map = GameMap(100, 100)
        map.generateMapByTwoPoints(
            Vector(2, 2),
            4,
            Vector(95, 95),
            2,
            1
        )
        map.fogOfWar = true
        map.setAnimation()
        players = arrayOf(
            Player("1").apply {
                color = Color.RED
                addUnit(MeleeUnit(this, Vector(10, 10)))
                addBuild(PlayerBase(this, Vector(7, 7)))
                addBuild(Barracks(this, Vector(8, 7)))
            },
            Player("2").apply {
                color = Color.ORANGE
                addUnit(MeleeUnit(this, Vector(10, 11)))
                addBuild(PlayerBase(this, Vector(13, 13)))
                addBuild(Barracks(this, Vector(12, 13)))
            }
        )
        /*for (x in 3..15) {
            for (y in 7..15) {
                map[x, y].type = game.Cell.Type.Ground
            }
        }
        map[6,7].type = game.Cell.Type.Mountain*/
        curPlayer.newTurn()
    }

    fun checkWin() {
        if (players.all { it.isLoose || it == curPlayer }) {
            state = State.Win
        }
    }

    /**
     * Рисует игру
     */
    fun paint(g: Graphics) {
        when (state) {
            State.Play -> {
                paintGame(g)
            }
            State.Win -> {
                paintGame(g)
                val s = "game.Player ${curPlayer.name} win!"
                val w = g.fontMetrics.stringWidth(s)
                val h = g.fontMetrics.height
                g.color = curPlayer.color
                g.drawString(s, (win.innerSize.x - w) / 2, (win.innerSize.y + h) / 2)
            }
            State.EditMap -> {
                map.paint(g)
                g.color = Color.gray
                g.fillRect(0, 0, map.cs + 4, map.cs + 4)
                g.color = selectedCellType.color
                g.fillRect(2, 2, map.cs, map.cs)
                g.color = Color(g.color.red, g.color.green, g.color.blue, 128)
                g.color = Color.red
                g.drawString("${win.isLeftButtonDown}", 10,10)
            }
            else -> {
                //win.Panel.setVisible(false)
            }
        }
    }

    private fun paintGame(g: Graphics) {
        g.clearRect(0, 0, map.width * map.cs, map.height * map.cs)
        map.paint(g)
        curPlayer.paint(g)
        g.color = curPlayer.color
        g.drawString(
            "player:${curPlayer.name} ${curPlayer.resource.toString_()}",
            0,
            g.font.size
        )

        drawTask.forEach { it(g) }
        drawTask.clear()

    }

    /**
     * Обработка нажатий кнопок мыши
     */
    fun mouseClicked(ev: MouseEvent) {
        when (state) {
            State.Play -> {
                //val posInMapCord = utilite.getPos/map.cs
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
                if(win.isLeftButtonDown) {
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

    fun endTurn() {
        if (!curPlayer.endTurn()) {
            do {
                curPlayerId++
                curPlayerId %= players.size
            } while (curPlayer.isLoose)
            map.centerOn(
                curPlayer.selectedEntity ?:
                curPlayer.getEntitiesOf<PlayerBase>()[0]
            )
            curPlayer.newTurn()
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

    /**
     * Основное окно в котором происходит вся отрисовка
     */
    lateinit var win: MainWindow

    @Serializable
    class Simple(val s: String)

    @Serializable
    abstract class Parent

    @Serializable
    @SerialName("Sample")
    data class Sample(val s: String) : Parent()

    @Serializable
    data class SampleTwo(val s: String) : Parent()

    @Serializable
    class Test {
        var frames: List<Test1> = emptyList()
    }

    @Serializable
    class Test1 {
        var filename: String = ""
    }

    /**
     * Нужен для запуска программы, вызывается в первую очередь
     */
    @JvmStatic
    fun main(a: Array<String>) {
        /*val s = File("""./src/main/resources/graphics/test2/animation.Sprite-0001.json""").
        bufferedReader().readText()
        val json = Json{
            ignoreUnknownKeys = true
        }
        val j:JsonObject = json.decodeFromString(s)
        val t: Test = json.decodeFromString(s)
        println(t.frames[0].filename)
        println(j["frames"]?.jsonArray?.utilite.get(0)?.jsonObject?.utilite.get("filename")?.toString())
        readLine()*/
        win = MainWindow()
    }
}