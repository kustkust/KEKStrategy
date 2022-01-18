package game

import utility.Vector

enum class Direction(val letter: String) {
    Up("U") {
        override val offset: Vector = Vector(0, -1)
    },
    Down("D") {
        override val offset: Vector = Vector(0, 1)
    },
    Left("L") {
        override val offset: Vector = Vector(-1, 0)
    },
    Right("R") {
        override val offset: Vector = Vector(1, 0)
    };

    abstract val offset: Vector

    val oposite: Direction
        get() = when (this) {
            Up -> Down
            Down -> Up
            Left -> Right
            Right -> Left
        }
}

