package game.entities.builds

import game.Player
import game.entities.BaseEntity
import utility.Vector

abstract class BaseBuild(owner_: Player, pos_: Vector) :
    BaseEntity(owner_, pos_) {

    init {
        owner.addBuild(this)
    }

    override fun selfCheck(from: BaseEntity?) {
        if (curHp <= 0) {
            owner.removeBuild(this)
        }
    }
}