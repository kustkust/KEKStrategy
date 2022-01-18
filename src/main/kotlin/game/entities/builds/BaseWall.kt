package game.entities.builds

import game.G
import game.Player
import utility.Vector

abstract class BaseWall(owner_: Player, pos_: Vector) : BaseBuild(owner_, pos_) {
    init {
        setupAnimation()
        setupNeiAnimation(pos)
    }

    override var height = 1

    abstract fun setupAnimation()

    companion object {
        fun setupNeiAnimation(pos: Vector) {
            val set = { d: Vector ->
                val b = G.map.getCell(pos + d)?.build
                if (b is BaseWall) {
                    b.setupAnimation()
                }
            }
            set(Vector.Up)
            set(Vector.Down)
            set(Vector.Left)
            set(Vector.Right)
        }
    }
}