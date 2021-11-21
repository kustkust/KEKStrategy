interface IRect {
    operator fun contains(p: Vector) =
        l <= p.x && t <= p.y && r >= p.x && b >= p.y

    var pos: Vector
    var size: Vector
    var l: Int
    var r: Int
    var t: Int
    var b: Int
    var lt: Vector
    var lb: Vector
    var rt: Vector
    var rb: Vector
}