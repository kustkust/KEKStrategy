package gameinterface

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager

internal class VerticalLayout(var xSpace: Int = 0, var ySpace: Int = 0) : LayoutManager {
    override fun addLayoutComponent(name: String?, comp: Component?) {}
    override fun removeLayoutComponent(comp: Component?) {}

    override fun minimumLayoutSize(c: Container): Dimension {
        return calculateBestSize(c)
    }

    override fun preferredLayoutSize(c: Container): Dimension {
        return calculateBestSize(c)
    }

    override fun layoutContainer(container: Container) {
        var currentY = ySpace
        container.components.forEach {
            if (it.isVisible) {
                val pref: Dimension = it.preferredSize
                it.setBounds(xSpace, currentY, pref.width, pref.height)
                currentY += ySpace
                currentY += pref.height
            }
        }
    }

    private fun calculateBestSize(c: Container) =
        Dimension(c.components.maxOf { it.width } + xSpace, c.components.sumOf { it.height + ySpace })
}