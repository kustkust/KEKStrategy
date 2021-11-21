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
        Menu,
        Play,
        Win,
    }

    var state = State.Play

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
    }

    fun startGame() {
        state = State.Play
        map = GameMap(100, 100)
        map.generateMapByTwoPoints(Vector(2, 2), 4, Vector(95, 95), 2, 1)
        map.fogOfWar = true
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
                map[x, y].type = Cell.Type.Ground
            }
        }
        map[6,7].type = Cell.Type.Mountain*/
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
                val s = "Player ${curPlayer.name} win!"
                val w = g.fontMetrics.stringWidth(s)
                val h = g.fontMetrics.height
                g.color = curPlayer.color
                g.drawString(s, (win.innerSize.x - w) / 2, (win.innerSize.y + h) / 2)
            }
            else -> {

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
                //val posInMapCord = pos/map.cs
                curPlayer.mouseClicked(ev)
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
            }
            State.Win -> {
                when (ev.keyCode) {
                    KeyEvent.VK_SPACE -> startGame()
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
            map.centerOn(curPlayer.selectedEntity ?: curPlayer.getEntitiesOf<PlayerBase>()[0])
            curPlayer.newTurn()
        }
    }

    fun keyPressed(ev: KeyEvent) {
        when (state) {
            State.Play -> {
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

    /**
     * Нужен для запуска программы, вызывается в первую очередь
     */
    @JvmStatic
    fun main(a: Array<String>) {
        win = MainWindow()
    }
}