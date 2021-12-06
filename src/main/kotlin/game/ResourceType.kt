package game

typealias Cost = Map<ResourceType, Int>

fun Cost.toString_(): String {
    var resStr = ""
    forEach {
        resStr += it.key.name + ":" + it.value.toString() + " "
    }
    return resStr
}

fun makeCost(vararg cost: Pair<ResourceType, Int>) = mapOf(*cost)

enum class ResourceType {
    Gold,
    Tree,
    Stone,
}