package utility

class Event1<T1> {
    private val observers = mutableSetOf<(T1) -> Unit>()

    operator fun plusAssign(observer: (T1) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T1) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value: T1) =
        observers.forEach { it(value) }
    fun clear() = observers.clear()
}

class Event2<T1, T2> {
    private val observers = mutableSetOf<(T1, T2) -> Unit>()

    operator fun plusAssign(observer: (T1, T2) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T1, T2) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value1: T1, value2: T2) =
        observers.forEach { it(value1, value2) }
    fun clear() = observers.clear()
}

class Event3<T1, T2, T3> {
    private val observers = mutableSetOf<(T1, T2, T3) -> Unit>()

    operator fun plusAssign(observer: (T1, T2, T3) -> Unit) {
        observers.add(observer)
    }

    operator fun minusAssign(observer: (T1, T2, T3) -> Unit) {
        observers.remove(observer)
    }

    operator fun invoke(value1: T1, value2: T2, value3: T3) =
        observers.forEach { it(value1, value2, value3) }
    fun clear() = observers.clear()
}