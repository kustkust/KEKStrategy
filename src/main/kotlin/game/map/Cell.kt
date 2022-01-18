package game.map

import game.G
import game.Player
import game.entities.builds.BaseBuild
import game.entities.units.BaseUnit
import graphics.Animation
import kotlinx.serialization.Transient
import utility.*
import java.awt.Color
import java.awt.Graphics
import kotlin.math.max

//@Serializable
class Cell(val pos: Vector, type_: Type = Type.Water) {
    var noise = 0.0

    var type = type_
        set(value) {
            field = value
            setAnimation()
            setupNeiAnimation()
        }

    val movePointCost: Int
        get() = type.movePointCost

    val height: Int
        get() = type.height

    val visibleHeight: Int
        get() = max(type.height + max(unit?.height ?: 0, build?.height ?: 0), type.visibleHeight)

    @Transient
    var unit: BaseUnit? = null

    @Transient
    var build: BaseBuild? = null
    val pixelBounds get() = Rect(pos * C.cs, Vector(C.cs, C.cs))

    val buildsShadow = mutableMapOf<Player, Animation>()
    val unitsShadow = mutableMapOf<Player, Animation>()

    var animation: Animation
        get() = animationUL
        set(value) {
            animationUL = value
        }

    @Transient
    lateinit var animationUL: Animation

    @Transient
    lateinit var animationUR: Animation

    @Transient
    lateinit var animationDL: Animation

    @Transient
    lateinit var animationDR: Animation

    fun setAnimation() {
        when (type) {
            Type.Ground -> {
                animationUL = G.animationManager.getAnimation("${type.name}UL")
                animationUR = G.animationManager.getAnimation("${type.name}UR")
                animationDL = G.animationManager.getAnimation("${type.name}DL")
                animationDR = G.animationManager.getAnimation("${type.name}DR")
            }
            Type.Mountain -> {
                animation = G.animationManager.getAnimation(type.name)
            }
            else -> animation = G.animationManager.getAnimation(type.name)
        }
        setupAnimation()
    }

    fun setupAnimation() {
        when (type) {
            Type.Ground -> {
                val d = CellDir.values().associateWith {
                    (G.map.getCell(pos + it.dir)?.type == Type.Water)
                }
                animationUL.curTagName = d.uls
                animationUR.curTagName = d.urs
                animationDL.curTagName = d.dls
                animationDR.curTagName = d.drs
            }
            Type.Mountain -> {
                animation.curTagName =
                    if (G.map.downOf(pos)?.type == Type.Mountain) "M"
                    else "MD"
            }
            else -> animation = G.animationManager.getAnimation(type.name)
        }
    }

    fun setupNeiAnimation() {
        val set = { d: Vector -> G.map.getCell(pos + d)?.setupAnimation() }
        set(Vector.Up)
        set(Vector.Down)
        set(Vector.Left)
        set(Vector.Right)
        set(Vector.UpLeft)
        set(Vector.UpRight)
        set(Vector.DownLeft)
        set(Vector.DownRight)
    }

    fun paint(g: Graphics) {
        val p = G.map.getTranslatedForDraw(pos)
        when (type) {
            Type.Ground -> {
                animationUL.paint(g, p)
                animationUR.paint(g, p)
                animationDL.paint(g, p)
                animationDR.paint(g, p)
            }
            Type.Mountain -> {
                animation.paint(g, p)
            }
            else -> animation.paint(g, p)
        }
        if (G.map.showPerlin) {
            val c = (255 * noise).toInt()
            g.color = Color(c, c, c)
            g.fillRect(p, Vector(C.cs, C.cs))
            g.color = Color.red
            g.drawString(noise.toString(), p.x + 5, p.y + 10)
        }
    }

    fun visibleBy(player: Player) {
        if (build == null) {
            buildsShadow.remove(player)
        } else if (!player.own(build)) {
            buildsShadow[player] = build!!.animation.copy()
            buildsShadow.getValue(player).apply {
                run = false
                curFrameInd = 0
            }
        }
        if (unit == null) {
            unitsShadow.remove(player)
        } else if (!player.own(unit)) {
            unitsShadow[player] = unit!!.animation.copy()
            unitsShadow.getValue(player).apply {
                run = false
                curFrameInd = 0
            }
        }
    }

    /**
     * Тип клетки
     * @param movePointCost базовая стоимость перемещения на клетку
     * @param height высота на которой находится юнит
     * @param visibleHeight высота окружения на клетке, должна быть больше чем [height]
     * @param color цвет клетки, используется на миникарте
     */
    enum class Type(
        val movePointCost: Int,
        val height: Int,
        val visibleHeight: Int,
        val color: Color,
        val chance: Double = 1.0
    ) {
        Water(2, 0, 0, Color(0, 191, 255), 2.0),
        Ground(1, 1, 1, Color(0, 255, 0)),
        Forest(2, 1, 2, Color(0, 192, 0)),
        Hills(3, 2, 2, Color(0, 128, 0)),
        Mountain(3, 3, 3, Color(128, 128, 128));

        val size
            get() = values().size

        operator fun plus(i: Int): Type {
            val tmp = when {
                ordinal + i >= size -> size - 1
                ordinal + i < 0 -> 0
                else -> ordinal + i
            }
            return values().first { it.ordinal == tmp }
        }

        operator fun minus(i: Int) = this.plus(-i)
    }

    enum class CellDir(val dir: Vector, val lit: Char) {
        Up(Vector(0, -1), 'U'),
        Down(Vector(0, 1), 'D'),
        Left(Vector(-1, 0), 'L'),
        Right(Vector(1, 0), 'R'),
        UpLeft(Vector(-1, -1), 'C'),
        UpRight(Vector(1, -1), 'C'),
        DownLeft(Vector(-1, 1), 'C'),
        DownRight(Vector(1, 1), 'C');
    }
}