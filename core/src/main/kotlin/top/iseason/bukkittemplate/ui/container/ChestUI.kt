package top.iseason.bukkittemplate.ui.container

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

/**
 * 箱子界面
 * @param title 标题
 * @param row 行数
 */
open class ChestUI(
    var title: String = "Chest UI",
    row: Int = 6,
    override var clickDelay: Long = 200L
) : BaseUI(row * 9) {

    override fun reset() {
        resetSlots()
    }

    override fun buildInventory(): Inventory = Bukkit.createInventory(this, super.size, title)

    override fun clone(): BaseUI {
        val chestUI = ChestUI(this.title, this.size / 9, this.clickDelay)
        slots.forEachIndexed { index, baseSlot ->
            chestUI.slots[index] = baseSlot?.clone(index)
        }
        return chestUI.also {
            it.lockOnBottom = lockOnBottom
            it.lockOnTop = lockOnTop
            it.async = async
            it.onClick = onClick
            it.onClicked = onClicked
            it.onOpen = onOpen
            it.onClose = onClose
        }
    }

}