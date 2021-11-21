import java.awt.Color

class Cell(val pos: Vector, var type: Type = Type.Water) {
    var unit: BaseUnit? = null
    var build: BaseBuild? = null
    val pixelBounds get() = Rect(pos * G.map.cs, Vector(G.map.cs, G.map.cs))

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
}