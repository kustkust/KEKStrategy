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
        map = GameMap(1000, 1000)
        map.generateMapByTwoPoints(Vector(2, 2), 4, Vector(95, 95), 2, 1)
        map.fogOfWar = true
        players = arrayOf(
            Player("1").apply {
                color = Color.RED
                addUnit(MeleeUnit(Vector(10, 10)))
                addBuild(PlayerBaseBuild(Vector(7, 7)))
                addBuild(Barracks(Vector(8, 7)))
            },
            Player("2").apply {
                color = Color.ORANGE
                addUnit(MeleeUnit(Vector(10, 11)))
                addBuild(PlayerBaseBuild(Vector(13, 13)))
                addBuild(Barracks(Vector(12, 13)))
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

    /**
     * Рисует игру
     */
    fun paint(g: Graphics) {
        when (state) {
            State.Play -> {
                //val ig = img.graphics
                g.clearRect(0, 0, map.width * map.cs, map.height * map.cs)
                map.paint(g)
                curPlayer.paint(g)
                g.color = curPlayer.color
                var resStr = ""
                curPlayer.resource.forEach {
                    resStr += it.key.name + ":" + it.value.toString() + " "
                }
                g.drawString(
                    "player:${curPlayer.name} $resStr",
                    0,
                    g.font.size
                )

                drawTask.forEach { it(g) }
                drawTask.clear()

                //g.drawImage(img, 0, 0, null)
            }
            else -> {

            }
        }
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
                    KeyEvent.VK_SPACE -> {
                        curPlayer.endTurn()
                        curPlayerId++
                        curPlayerId %= players.size
                        curPlayer.newTurn()
                    }
                }
                curPlayer.keyClicked(ev)
                map.keyClicked(ev)
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