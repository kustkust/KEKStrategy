package game

import utility.joinToString

typealias Cost = Map<ResourceType, Int>

fun Cost.costToMultiRowString() = joinToString(
    separator = "\n",
    keyValueSeparator = ":",
)

fun Cost.costToString() = joinToString(
    separator = " ",
    keyValueSeparator = ":",
)

fun makeCost(vararg cost: Pair<ResourceType, Int>) = mapOf(*cost)

enum class ResourceType {
    Gold,
    Tree,
    Stone,
}