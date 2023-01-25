@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package top.iseason.bukkittemplate.utils.bukkit

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

/**
 * bukkit的位置相关工具
 */
object LocationUtils {
    /**
     * 根据坐标yaw和pith值获取X方向的单位向量
     * @return X方向的单位向量
     */
    fun Location.getNormalX(): Vector {
        val vector = Vector()
        val rotX = yaw.toDouble()
        //row =0 , pitch = 0
        vector.x = cos(Math.toRadians(rotX))
        vector.z = sin(Math.toRadians(rotX))
        return vector
    }

    /**
     * 根据坐标yaw和pith值获取Y方向的单位向量
     * @return Y方向的单位向量
     */
    fun Location.getNormalY(): Vector = direction.getCrossProduct(getNormalX())

    /**
     * 根据坐标yaw和pith值获取Z方向的单位向量
     * @return Z方向的单位向量,与 getDirection() 方法一致
     */
    fun Location.getNormalZ(): Vector = direction

    /**
     *   由相对坐标获取世界坐标
     */
    fun Location.getRelativeByCoordinate(
        x: Double,
        y: Double,
        z: Double
    ): Location {
        return clone().apply {
            add(getNormalX().multiply(x))
            add(getNormalY().multiply(y))
            add(getNormalZ().multiply(z))
        }
    }
}