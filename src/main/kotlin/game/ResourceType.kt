package game

typealias Cost = Map<ResourceType, Int>

fun Cost.costToString(): String {
    var resStr = ""
    forEach {
        resStr += it.key.name + ":" + it.value.toString() + "\n"
    }
    return resStr
}

fun makeCost(vararg cost: Pair<ResourceType, Int>) = mapOf(*cost)

enum class ResourceType {
    Gold,
    Tree,
    Stone,
}