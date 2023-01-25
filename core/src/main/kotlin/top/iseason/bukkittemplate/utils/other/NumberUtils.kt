@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package top.iseason.bukkittemplate.utils.other

import kotlin.math.abs

/**
 * 数字工具
 */
object NumberUtils {
    private val romanMap: MutableMap<Char, Int> = hashMapOf(
        'I' to 1,
        'V' to 5,
        'X' to 10,
        'L' to 50,
        'D' to 500,
        'M' to 1000,
    )

    /**
     * 整数转罗马数字,最大到3999
     */
    fun Int.toRoman(): String {
        if (this > 3999) return toString()
        val num = abs(this)
        val m = arrayOf("", "M", "MM", "MMM")
        val c = arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
        val x = arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
        val i = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")
        val sign = if (this >= 0) "" else "- "
        return sign + m[num / 1000] + c[num % 1000 / 100] + x[num % 100 / 10] + i[num % 10]
    }

    /**
     * 罗马数字转整数
     */
    fun romanToInt(s: String): Int {
        val temp = s.uppercase()
        val n = s.length
        var num = romanMap[temp[n - 1]]!!
        for (i in n - 2 downTo 0) {
            if (romanMap[temp[i]]!! >= romanMap[temp[i + 1]]!!) {
                num += romanMap[temp[i]]!!
            } else {
                num -= romanMap[temp[i]]!!
            }
        }
        return num
    }
}
