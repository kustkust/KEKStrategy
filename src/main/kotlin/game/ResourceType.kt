package game

typealias Cost = Map<ResourceType, Int>

fun Cost.toString_(): String {
    var resStr = ""
    forEach {
        resStr += it.key.name + ":" + it.value.toString() + " "
    }
    return resStr
}

enum class ResourceType {
    Gold,
    Tree,
    Stone,
}