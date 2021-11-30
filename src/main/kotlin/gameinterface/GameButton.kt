package gameinterface

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import javax.swing.JButton

internal class GameButton(text: String? = null, val isRounded: Boolean = true, var r: Int = 50) : JButton(text) {
    override fun paintComponent(g: Graphics) {
        if (getModel().isArmed) {
            g.color = Color(102, 8, 0) //Цвет фона при нажатой кнопке
            foreground = Color(95, 95, 93) //Цвет надписи при нажатой кнопке
        } else if (getModel().isRollover) {
            g.color = Color(95, 95, 93) //Цвет фона
            foreground = Color(102, 8, 0) //Цвет надписи
        } else {
            g.color = Color(158, 158, 158) //Цвет фона
            foreground = Color(154, 12, 0) //Цвет надписи
        }

        if (isRounded) {
            g.fillRoundRect(0, 0, size.width - 1, size.height - 1, r, r)
        } else {
            g.fillRect(0, 0, size.width - 1, size.height - 1)
        }
        super.paintComponent(g)
    }

    override fun paintBorder(g: Graphics) {
        g.color = foreground
        if (isRounded) {
            g.drawRoundRect(0, 0, size.width - 1, size.height - 1, r, r)
        } else {
            g.drawRoundRect(0, 0, size.width - 1, size.height - 1, 0, 0)
        }
    }

    init {
        val size = preferredSize
        size.height = Math.max(size.width, size.height)
        size.width = size.height
        preferredSize = size
        isContentAreaFilled = false
        isFocusable = false
        font = Font(
            "Serif",
            Font.PLAIN,
            25
        )
    }
}