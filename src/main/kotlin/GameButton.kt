import java.awt.Color
import java.awt.Graphics
import javax.swing.JButton


internal class GameButton(text: String?, isRounded: Boolean) : JButton(text) {
    var r = 50 //радиус закругления кнопки
    var k = isRounded
    override fun paintComponent(g: Graphics) {
        if (getModel().isArmed) {
            g.color = Color(102,8,0) //Цвет фона при нажатой кнопке
            foreground = Color(95,95,93) //Цвет надписи при нажатой кнопке
        } else if(getModel().isRollover){
            g.color = Color(95,95,93) //Цвет фона
            foreground = Color(102,8,0) //Цвет надписи
        }else{
            g.color = Color(158,158,158) //Цвет фона
            foreground = Color(154,12,0) //Цвет надписи
        }

        if(k){
            g.fillRoundRect(0, 0, size.width - 1, size.height - 1, r, r)
        }
        else{
            g.fillRoundRect(0, 0, size.width - 1, size.height - 1, 0, 0)
        }
        super.paintComponent(g)
    }


    override fun paintBorder(g: Graphics) {
        g.color = foreground
        if(k){
            g.drawRoundRect(0, 0, size.width - 1, size.height - 1, r, r)
        }
        else{
            g.drawRoundRect(0, 0, size.width - 1, size.height - 1, 0, 0)
        }
    }


    init {
        val size = preferredSize
        size.height = Math.max(size.width, size.height)
        size.width = size.height
        preferredSize = size
        isContentAreaFilled = false
    }
}