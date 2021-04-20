package top.iseason.kotlin.deenchantment.utils

import java.security.SecureRandom

object Tools {
    private val RANDOM = SecureRandom.getInstance("SHA1PRNG", "SUN")
    fun getRandomDouble() = RANDOM.nextDouble()
    fun intToRome(number: Int): String {
        var num = number
        var rNumber = ""
        val aArray = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val rArray = arrayOf(
            "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X",
            "IX", "V", "IV", "I"
        )
        if (num < 1 || num >= 40) {
            return num.toString() //不处理太大与负数
        }
        for (i in aArray.indices) {
            while (num >= aArray[i]) {
                rNumber += rArray[i]
                num -= aArray[i]
            }
        }
        return rNumber
    }


}