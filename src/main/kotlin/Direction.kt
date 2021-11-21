enum class Direction {
    Up {
        override val offset: Vector = Vector(0, -1)
    },
    Down {
        override val offset: Vector = Vector(0, 1)
    },
    Left {
        override val offset: Vector = Vector(-1, 0)
    },
    Right {
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

