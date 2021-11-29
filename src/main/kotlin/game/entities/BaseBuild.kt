package game.entities

import game.Player
import utilite.Vector

abstract class BaseBuild(owner: Player, pos: Vector = Vector(0, 0)) :
    BaseEntity(owner, pos) {
    override fun selfCheck(from: BaseEntity?) {
        if (curHp <= 0) {
            owner.removeBuild(this)
        }
    }
}