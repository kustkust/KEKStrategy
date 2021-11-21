class Rect(
    override var pos: Vector = Vector(),
    override var size: Vector = Vector()
) : IRect {
    constructor(x: Int, y: Int, width:Int, height: Int) :
            this(Vector(x,y), Vector(width,height))
    override var l
        get() = pos.x
        set(v) {
            pos.x = v
        }
    override var r
        get() = pos.x + size.x
        set(v) {
            size.x = v - pos.x
        }
    override var t
        get() = pos.y
        set(v) {
            pos.y = v
        }
    override var b
        get() = pos.y + size.y
        set(v) {
            size.y = v - pos.y
        }
    override var lt: Vector
        get() = pos
        set(v) {
            pos = v
        }
    override var lb: Vector
        get() = Vector(l, b)
        set(v) {
            l = v.x; b = v.y
        }
    override var rt: Vector
        get() = Vector(r, t)
        set(v) {
            r = v.x; t = v.y
        }
    override var rb: Vector
        get() = pos + size
        set(v) {
            r = v.x; b = v.y
        }
}