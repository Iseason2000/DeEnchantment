package top.iseason.bukkit.bukkittemplate.utils

import kotlin.math.abs

/**
 * 整数转罗马数字
 */
fun Int.toRoman(): String {
    val num = abs(this)
    val m = arrayOf("", "M", "MM", "MMM")
    val c = arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
    val x = arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
    val i = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")
    val sign = if (this >= 0) "" else "- "
    return sign + m[num / 1000] + c[num % 1000 / 100] + x[num % 100 / 10] + i[num % 10]
}