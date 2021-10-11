import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

object G {
    /**
     * Игровая карта
     */
    var map = GameMap()

    init {
        for (x in 3..15) {
            for (y in 7..15) {
                map[x, y].type = Cell.Type.Ground
            }
        }
        map[6,7].type = Cell.Type.Mountain
    }

    enum class State {
        Play,
        Win,
    }
    // чекнуть без этого создание картины
    /**
     * Изображение, которое выводится на экран
     */
    private val img = BufferedImage(
        map.width * map.cs,
        map.height * map.cs,
        BufferedImage.TYPE_INT_RGB
    )

    /**
     * Список игроков
     */
    var players: Array<Player> = arrayOf(
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

    /**
     * Номер текущего игрока
     */
    var curPlayerId = 0

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

    /**
     * Рисует игру
     */
    fun paint(g: Graphics) {
        val ig = img.graphics
        ig.clearRect(0, 0, img.width, img.height)
        map.paint(ig)
        ig.color = curPlayer.color
        var resStr = ""
        curPlayer.resource.forEach {
            resStr += it.key.name + ":" + it.value.toString() + " "
        }
        ig.drawString(
            "player:${curPlayer.name} $resStr",
            0,
            ig.font.size
        )

        drawTask.forEach { it(img.graphics) }
        drawTask.clear()

        g.drawImage(img, 0, 0, null)
    }

    /**
     * Обработка нажатий кнопок мыши
     */
    fun mouseClicked(ev: MouseEvent) {
        //val posInMapCord = pos/map.cs
        curPlayer.mouseClicked(ev)
    }

    /**
     * Обработка движений мыши
     */
    fun mouseMoved(ev: MouseEvent) {
        map.mouseMoved(ev)
        curPlayer.mouseMoved(ev)
    }

    /**
     * Обработка нажатий клавиш на клавиатуре
     */
    fun keyClicked(ev: KeyEvent) {
        when (ev.keyCode) {
            KeyEvent.VK_SPACE -> {
                curPlayer.endTurn()
                curPlayerId++
                curPlayerId %= players.size
                curPlayer.newTurn()
            }
        }
        curPlayer.keyClicked(ev)
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
        /*val m1 = makeMatrix(2,2){_, _ -> Cell.Type.Mountain}
        val m2 = m1.matrixClone()
        m2[0][0]=Cell.Type.Ground
        println(m1[0][0])
        println(m2[0][0])
        readLine()*/
        win = MainWindow()
    }
}