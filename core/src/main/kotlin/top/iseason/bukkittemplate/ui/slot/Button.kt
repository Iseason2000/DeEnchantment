package top.iseason.bukkittemplate.ui.slot

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * 可点击的按钮
 * @param index 位于容器中的位置
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class Button(
    override var rawItemStack: ItemStack?,
    index: Int = 0,
) : ClickSlot(rawItemStack, index) {
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

    override fun clone(index: Int): Button = Button(rawItemStack, index).also {
        it.baseInventory = baseInventory
        it.onClick = onClick
        it.onClicked = onClicked
        it.asyncClick = asyncClick
    }

}