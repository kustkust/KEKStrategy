abstract class BaseBuild(pos: Vector = Vector(0, 0)) : BaseEntity(pos) {
    override fun selfCheck() {
        if (curHp <= 0) {
            owner.removeBuild(this)
        }
    }
}