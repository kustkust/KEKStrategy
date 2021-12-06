package utility// стырено с https://gist.github.com/MarcinMoskala/4726b588d2bdad98d04d9ba879cb33ce#file-sortedarraylist-kt

class SortedArrayList<T>(
    capacity: Int,
    private val comp: Comparator<T>
) {

    private val list = ArrayList<T>(capacity)

    val size: Int
        get() = list.size

    fun add(element: T) = findIndex(element)
        .let { index -> list.add(if (index < 0) -(index + 1) else index, element) }

    fun remove(element: T) = findIndex(element)
        .let { index -> if (index >= 0) list.removeAt(index) }

    fun removeAt(index: Int) = list.removeAt(index)

    fun get(index: Int): T = list[index]

    fun takeFirst(): T {
        val tmp = list.first()
        list.removeAt(0)
        return tmp
    }

    fun takeLast(): T {
        val tmp = list.last()
        list.removeAt(list.size - 1)
        return tmp
    }

    fun contains(element: T) = findIndex(element).let { index ->
        index >= 0 && element == list[index] || (findEquals(index + 1, element, 1) || findEquals(
            index - 1,
            element,
            -1
        ))
    }

    fun iterator(): Iterator<T> = list.iterator()

    fun isEmpty() = list.isEmpty()

    fun first() = list.first()

    private fun findIndex(element: T): Int = list.binarySearch(element, comp)

    private tailrec fun findEquals(index: Int, element: T, step: Int): Boolean = when {
        index !in 0 until size -> false
        comp.compare(element, list[index]) != 0 -> false
        list[index] == element -> true
        else -> findEquals(index + step, element, step)
    }
}