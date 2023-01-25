/*
 * Description: 获取安全随机数，比普通随机更随机，但用时也会多一些
 * @Author: Iseason2000
 * @Date: 2022/1/18 下午2:23
 *
 */

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package top.iseason.bukkittemplate.utils.other

import java.security.SecureRandom
import kotlin.math.abs

/**
 * 安全随机数工具，比普通随机更随机，但用时也会多一些
 */
object RandomUtils {
    private val SECURE_RANDOM = SecureRandom()

    /**
     * 获取安全的随机小数(Double)，范围[start,end) 默认[0.0D,1.0D)
     */
    fun getDouble(start: Double = 0.0, end: Double = 1.0) = start + SECURE_RANDOM.nextDouble() * (end - start)

    /**
     * 获取安全的随机整数，范围[start,end] 默认[0,Int.MAX_VALUE)
     */
    fun getInteger(start: Int = 0, end: Int = Int.MAX_VALUE - 1) = start + SECURE_RANDOM.nextInt(end - start + 1)

    /**
     * 获取安全的随机布尔值
     */
    fun getBoolean() = SECURE_RANDOM.nextBoolean()

    /**
     * 获取安全的随机小数(Float)，范围[start,end) 默认[0.0F,1.0F)
     */
    fun getFloat(start: Float = 0F, end: Float = 1F) = start + SECURE_RANDOM.nextFloat() * (end - start)

    /**
     * 获取安全的随机长整型
     */
    fun getLong() = SECURE_RANDOM.nextLong()

    /**
     * 给定判断给定概率是否生效
     * @return 满足概率返回false,否之返回true
     */
    fun checkPercentage(percent: Int) = getDouble(end = 100.0) >= percent

    /**
     * 给定判断给定概率是否生效
     * @return 满足概率返回false,否之返回true
     */
    fun checkPercentage(percent: Double) = getDouble(end = 100.0) >= percent

    /**
     * 获取以0为对称轴，范围为(-1,1)的高斯分布
     */
    fun getGaussian() = SECURE_RANDOM.nextGaussian()

    /**
     * 范围区间内[start,end]的正态分布，中心轴为区间中心
     */
    fun getGaussian(start: Double, end: Double): Double {
        val gaussian = getGaussian()
        val odd = if (abs(gaussian) < 3) gaussian / 3.00 else 1.00
        val half = (start + end) / 2.00
        return odd * (end - half) + half
    }

    /**
     * 加权随机区间
     * @return 选中的权重序号
     */
    fun getWeighted(weights: Iterable<Double>): Int {
        val sum = weights.sum()
        val random = getDouble(0.0, sum)
        var temp = 0.0
        weights.forEachIndexed { index, i ->
            temp += i
            if (random <= temp) return index
        }
        return 0
    }

    //根据等级计算时运随机后的倍率
    fun calculateFortune(level: Int): Int {
        if (level <= 0) throw IllegalArgumentException("level can not <= 0")
        var temp = 2.0 / (level + 2)
        val otherRate = (1 - temp) / level
        val random = SECURE_RANDOM.nextDouble()
        for (n in 1..level + 1) {
            if (random <= temp) return n
            temp += otherRate
        }
        return 1
    }
}
