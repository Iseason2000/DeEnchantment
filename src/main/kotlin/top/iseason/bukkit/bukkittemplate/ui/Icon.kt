package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir

open class Icon(
    val rawItemStack: ItemStack,
    override val index: Int

) : BaseSlot {
    override var baseInventory: Inventory? = null

    /**
     * 与Inventory的ItemStack同步
     */
    override var itemStack: ItemStack?
        set(value) {
            baseInventory?.setItem(index, value)
        }
        get() {
            val item = baseInventory?.getItem(index)
            return if (item == null || item.type.checkAir()) rawItemStack
            else item
        }

    var itemMeta: ItemMeta
        get() =
            itemStack!!.itemMeta!!
        set(value) {
            itemStack!!.itemMeta = value
        }

    /**
     * 显示的图标
     */
    var material: Material
        get() =
            itemStack!!.type
        set(value) {
            itemStack!!.type = value
        }

    /**
     * 显示的名称
     */
    var displayName: String
        get() = itemMeta.displayName
        set(value) {
            itemMeta = itemMeta.apply { setDisplayName(value) }
        }

    /**
     * 显示的lore
     */
    var lore: List<String>
        get() = itemMeta.lore ?: emptyList()
        set(value) {
            itemMeta = itemMeta.apply { lore = value }
        }

    override fun reset() {
        itemStack = rawItemStack
    }

    override fun clone(index: Int): Icon = Icon(rawItemStack, index).also {
        it.itemStack = itemStack
        it.baseInventory = baseInventory
    }
}