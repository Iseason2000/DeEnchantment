package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir

/**
 * 可点击的Slot
 */
abstract class ClickSlot(
    /**
     * 按钮对应的默认图标
     */
    open val rawItemStack: ItemStack? = null,
    /**
     * 位于容器中的位置
     */
    override val index: Int = 0

) : BaseSlot {

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

    /**
     * 依托的Inventory
     */
    override var baseInventory: Inventory? = null

    /**
     * 点击之前
     */
    var onClick: (ClickSlot.(InventoryClickEvent) -> Unit)? = null

    /**
     * 点击之后
     */
    var onClicked: (ClickSlot.(InventoryClickEvent) -> Unit)? = null

}

/**
 * 点击之前
 */
fun <T : ClickSlot> T.onClicked(action: (ClickSlot.(InventoryClickEvent) -> Unit)? = null): T {
    this.onClicked = action
    return this
}

/**
 * 点击之后
 */
fun <T : ClickSlot> T.onClick(action: (ClickSlot.(InventoryClickEvent) -> Unit)? = null): T {
    this.onClick = action
    return this
}

/**
 * 设置依托的UI
 */
fun Collection<ClickSlot>.setUI(ui: BaseUI) {
    ui.addSlots(*this.toTypedArray())
}

