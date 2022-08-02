package top.iseason.bukkit.bukkittemplate.utils.bukkit

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


/**
 * 修改ItemMeta
 */
inline fun <T : ItemStack> T.applyMeta(block: ItemMeta.() -> Unit): T {
    val itemMeta = itemMeta ?: return this
    block(itemMeta)
    this.itemMeta = itemMeta
    return this
}

/**
 * 减少物品数量，如果小于0则物品变为空气
 */
fun ItemStack.subtract(count: Int) {
    val i = amount - count
    if (i <= 0) type = Material.AIR
    else amount = i
}

/**
 * 增加物品数量，返回溢出的数量
 */
fun ItemStack.add(count: Int): Int {
    val i = amount + count
    return if (i >= maxStackSize) {
        amount = maxStackSize
        i - maxStackSize
    } else {
        amount = i
        0
    }
}

/**
 * 检查材质是否是空气
 */
fun Material.checkAir(): Boolean = when (this.name) {
    "AIR",
    "VOID_AIR",
    "CAVE_AIR",
    "LEGACY_AIR" -> true

    else -> false
}

object ItemUtils {

    /**
     * 物品转化为字节
     */
    fun toByteArray(item: ItemStack): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use { it.writeObject(item) }
        return outputStream.toByteArray()
    }

    /**
     * 字节转换为ItemStack
     */
    fun fromByteArray(bytes: ByteArray): ItemStack {
        BukkitObjectInputStream(ByteArrayInputStream(bytes)).use { return it.readObject() as ItemStack }
    }
}