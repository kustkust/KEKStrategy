import java.awt.Color

class Cell(var type: Type = Type.Water) {
    var unit: BaseUnit? = null
    var build: BaseBuild? = null

    enum class Type {
        Water {
            override var movePointCost: Int = 2
            override var color: Color = Color(0, 0, 255)
        },
        Ground {
            override var movePointCost: Int = 1
            override var color: Color = Color(0, 255, 0)
        },
        Forest{
            override var movePointCost: Int = 2
            override var color: Color = Color(0, 128, 0)

        },
        Mountain {
            override var movePointCost: Int = 3
            override var color: Color = Color(128, 128, 128)
        };

        abstract var movePointCost: Int
        abstract var color: Color
    }
}