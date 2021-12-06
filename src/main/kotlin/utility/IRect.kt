package utility

interface IRect {
    operator fun contains(p: Vector) =
        l <= p.x && t <= p.y && r >= p.x && b >= p.y

    var pos: Vector
    var x: Int
    var y: Int
    var size: Vector
    var w: Int
    var h: Int
    var l: Int
    var r: Int
    var t: Int
    var b: Int
    var lt: Vector
    var lb: Vector
    var rt: Vector
    var rb: Vector
}