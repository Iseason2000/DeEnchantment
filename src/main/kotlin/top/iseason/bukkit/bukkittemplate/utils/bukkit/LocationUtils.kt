package top.iseason.bukkit.bukkittemplate.utils.bukkit

import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

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
 * 根据坐标yaw和pith值获取Z方向的单位向量
 * @return Z方向的单位向量,与 getDirection() 方法一致
 *
 */
fun Location.getNormalZ(): Vector {
    val vector = Vector()
    val rotX = yaw.toDouble()
    val rotY = pitch.toDouble()
    // row = 0
    vector.y = -sin(Math.toRadians(rotY))
    val xz = cos(Math.toRadians(rotY))
    vector.x = -xz * sin(Math.toRadians(rotX))
    vector.z = xz * cos(Math.toRadians(rotX))
    return vector
}

// 以自身为原点和相对坐标系获取世界坐标
fun Location.getRelativeByCoordinate(
    coordinate: Array<Vector>, //坐标轴的单位向量
    x: Double,
    y: Double,
    z: Double
): Location {
    return clone().apply {
        add(coordinate[0].clone().multiply(x))
        add(coordinate[1].clone().multiply(y))
        add(coordinate[2].clone().multiply(z))
    }
}