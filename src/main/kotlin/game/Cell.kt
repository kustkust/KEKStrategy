package game

import game.entities.BaseBuild
import game.entities.BaseUnit
import graphics.Animation
import kotlinx.serialization.Transient
import utility.*
import java.awt.Color
import java.awt.Graphics

//@Serializable
class Cell(val pos: Vector, type_: Type = Type.Water) {
    var type = type_
        set(value) {
            field = value
            setAnimation()
            setupNeiAnimation()
        }

    @Transient
    var unit: BaseUnit? = null

    @Transient
    var build: BaseBuild? = null
    val pixelBounds get() = Rect(pos * G.map.cs, Vector(G.map.cs, G.map.cs))

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
    }

    enum class Type {
        Water {
            override var movePointCost: Int = 2
            override var color: Color = Color(0, 0, 255)
        },
        Ground {
            override var movePointCost: Int = 1
            override var color: Color = Color(0, 255, 0)
        },
        Forest {
            override var movePointCost: Int = 2
            override var color: Color = Color(0, 128, 0)

        },
        Hills {
            override var movePointCost: Int = 3
            override var color: Color = Color(64, 128, 64)
        },
        Mountain {
            override var movePointCost: Int = 3
            override var color: Color = Color(128, 128, 128)
        };

        abstract var movePointCost: Int
        abstract var color: Color
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