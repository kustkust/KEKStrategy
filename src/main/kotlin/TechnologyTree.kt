import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.lang.Integer.max

class TechnologyTree(val owner: Player) {
    class Technology(
        val name: String,
        val cost: Cost,
        var isOpen: Boolean = false,
        val effect: (Player) -> Unit,
        val bounds: Rect = Rect(Vector(), Vector(80, 40))
    ) : IRect by bounds {
        /**
         * Нужна для итерирования по дереву технологий
         */
        var visit: Boolean = false

        /**
         * Технологии, требуемые для открытия данной технологии
         */
        lateinit var requiredTech: List<Technology>

        /**
         * Технологии, которые открывает данная технология
         */
        var unlockableTech = listOf<Technology>()

        /**
         * Уровень технологии (для отрисовки)
         */
        var order = -1

        /**
         * Можно ли изучить сейчас эту технологию
         */
        val isOpenable get() = requiredTech.all { it.isOpen } && !isOpen

        fun open(owner: Player) {
            if (isOpenable && owner.pay(cost)) {
                isOpen = true
                effect(owner)
            }
        }

        fun paint(g: Graphics) {
            g.color = if (isOpen) Color.GREEN else if (isOpenable) Color.YELLOW else Color.GRAY
            g.fillRect(pos, size)
            g.color = Color.black
            g.drawMultiString(name + "\n" + cost.toString_(), pos)
        }
    }

    fun mouseClicked(ev: MouseEvent) {
        when (ev.button) {
            MouseEvent.BUTTON1 ->
                for ((_, t) in technologies)
                    if (ev.pos in t.bounds)
                        t.open(owner)
        }
    }

    private fun baseIterate(
        techs: List<Technology>,
        pred: (Technology) -> Boolean,
        fn: (Technology) -> Unit
    ) {
        techs.forEach {
            if (pred(it)) {
                fn(it)
                baseIterate(it.unlockableTech, pred, fn)
            }
        }
    }

    fun iterate(fn: (Technology) -> Unit) {
        baseIterate(startTechnologies, {
            if (!it.visit) {
                it.visit = true; true
            } else false
        }, fn)
        baseIterate(startTechnologies, { it.visit }, { it.visit = false })
    }

    operator fun get(name: String) = technologies[name]

    val bgRect = Rect()

    val startTechnologies = mutableListOf<Technology>()
    val technologies = mutableMapOf<String, Technology>()

    init {
        addTech(
            "BaseTech",
            mutableMapOf(),
            true
        )
        addTech(
            "MineTech",
            mutableMapOf(ResourceType.Gold to 20),
            false,
            listOf(technologies.getValue("BaseTech"))
        )
        addTech(
            "SawWill",
            mutableMapOf(ResourceType.Gold to 20),
            false,
            listOf(technologies.getValue("BaseTech"))
        )
        addTech(
            "MeleeUnit",
            mutableMapOf(ResourceType.Gold to 20),
            false,
            listOf(technologies.getValue("MineTech"))
        )
        place()
    }

    private var maxOrder = 0

    private fun addTech(
        name: String,
        cost: Cost,
        isOpen: Boolean,
        requiredTech: List<Technology> = listOf(),
        effect: (Player) -> Unit = {},
    ) = addTech(Technology(name, cost, isOpen, effect), requiredTech)

    private fun addTech(newTech: Technology, requiredTech: List<Technology> = listOf()) {
        technologies[newTech.name] = newTech
        if (requiredTech.isEmpty()) {
            startTechnologies += newTech
            newTech.order = 0
        } else {
            newTech.requiredTech = requiredTech
            newTech.order = requiredTech.maxOf { it.order } + 1
            maxOrder = max(maxOrder, newTech.order)
            requiredTech.forEach { it.unlockableTech += newTech }
        }
    }

    /**
     * Видимо не нужна
     */
    fun orderTree() {
        var order = 0
        var technologies: List<Technology> = startTechnologies
        technologies.forEach { it.order = 0 }
        while (technologies.isNotEmpty()) {
            val nextTech = arrayListOf<Technology>()
            technologies.forEach { t ->
                t.unlockableTech.forEach { nextT ->
                    if (nextT.order == -1 && nextT.requiredTech.all { it.order == order }) {
                        nextT.order = order + 1
                        maxOrder = max(maxOrder, nextT.order)
                        nextTech += nextT
                    }
                }
            }
            technologies = nextTech
            order++
        }
    }

    /**
     * Расставляет технологии для отрисовки
     */
    private fun place() {
        val drawP = Vector(10, 40)
        for (i in (0..maxOrder)) {
            var maxWidth = 60
            for ((_, t) in technologies) {
                if (t.order == i) {
                    t.pos = drawP.copy()
                    drawP.y += t.size.y + 10
                    if (maxWidth < t.size.x) maxWidth = t.size.x
                    if (bgRect.r < t.bounds.r) bgRect.r = t.bounds.r
                    if (bgRect.b < t.bounds.b) bgRect.b = t.bounds.b
                }
            }
            drawP.x += maxWidth + 10
            drawP.y = 40
        }
        bgRect.rb += Vector(10, 10)
    }

    fun paint(g: Graphics) {
        g.color = Color(128, 128, 128, 128)
        g.fillRect(bgRect)
        iterate { tech ->
            tech.paint(g)
            val from = Vector(tech.pos.x + tech.size.x, tech.pos.y + tech.size.y / 2)
            tech.unlockableTech.forEach {
                val to = Vector(it.pos.x, it.pos.y + it.size.y / 2)
                g.color = Color.white
                g.drawLine(from, to)
            }
        }
    }
}