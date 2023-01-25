package top.iseason.bukkittemplate.ui.container

import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import top.iseason.bukkittemplate.ui.slot.BaseSlot
import top.iseason.bukkittemplate.ui.slot.IOSlot
import top.iseason.bukkittemplate.ui.slot.merge
import top.iseason.bukkittemplate.utils.other.submit


abstract class BaseUI(
    val size: Int,
    /**
     * 点击间隔，防止卡顿
     */
    open var clickDelay: Long = 100L
) : InventoryHolder, Pageable {
    private var baseInventory: Inventory? = null

    val hasBuilt: Boolean
        get() = baseInventory != null

    final override fun getInventory(): Inventory = baseInventory!!

    abstract fun buildInventory(): Inventory

    /**
     * 储存各种槽位
     */
    var slots: Array<BaseSlot?> = arrayOfNulls(size)

    /**
     * 当玩家打开界面时，分为上下2个Inventory，是否锁住上部分
     */
    var lockOnTop: Boolean = false

    /**
     * 当玩家打开界面时，分为上下2个Inventory，是否锁住下部分
     */
    var lockOnBottom: Boolean = false

    /**
     * 点击时触发，可以取消
     */
    open var onClick: (InventoryClickEvent.() -> Unit)? = null

    /**
     * 点击后触发，不可取消
     */
    open var onClicked: (InventoryClickEvent.() -> Unit)? = null

    /**
     * 关闭时触发
     */
    open var onClose: (InventoryCloseEvent?.() -> Unit)? = null

    /**
     * 打开时触发
     */
    open var onOpen: (InventoryOpenEvent.() -> Unit)? = null

    /**
     * 点击是否异步，只对 onClicked 有效
     */
    var async: Boolean = false

    /**
     * 获取正在看这个UI的实体
     */
    fun getViewers(): MutableList<HumanEntity> = inventory.viewers

    /**
     * 设置多个槽
     */
    fun addSlots(vararg baseSlots: BaseSlot) {
        for (slot in baseSlots) {
            addSlot(slot)
        }
    }

    /**
     * 设置一个槽
     */
    private fun addSlot(baseSlot: BaseSlot) {
        val index = baseSlot.index
        require(index in 0 until size) { "Slot index was out of bounds(${index}), max index is $size" }
        this.slots[index] = baseSlot
    }

    /**
     * 将一个Slot拷贝多份到其他格子,可变参数版
     */
    fun addMultiSlots(baseSlot: BaseSlot, vararg others: Int): List<BaseSlot> {
        val mutableListOf = mutableListOf(baseSlot)
        addSlot(baseSlot)
        for (other in others) {
            val clone = baseSlot.clone(other)
            addSlot(clone)
            mutableListOf.add(clone)
        }
        return mutableListOf
    }

    /**
     * 将一个Slot拷贝多份到其他格子
     */
    fun addMultiSlots(baseSlot: BaseSlot, others: Iterable<Int>): List<BaseSlot> {
        val mutableListOf = mutableListOf(baseSlot)
        addSlot(baseSlot)
        for (other in others) {
            val clone = baseSlot.clone(other)
            addSlot(clone)
            mutableListOf.add(clone)
        }
        return mutableListOf
    }

    /**
     * 将UI实例化为游戏Inventory
     */
    fun build(): Inventory {
        baseInventory = buildInventory()
        for (slot in slots) {
            if (slot == null) continue
            //清除原有的ItemStack
            slot.baseInventory = inventory
            if (slot is IOSlot) {
                inventory.setItem(slot.index, slot.placeholder)
            } else {
                inventory.setItem(slot.index, slot.itemStack)
            }
        }
        return inventory
    }

    /**
     * 将Slot 填满Inventory
     */
    fun setBackGround(baseSlot: BaseSlot): List<BaseSlot> = addMultiSlots(baseSlot, 0 until size)

    /**
     * 获取某一槽
     */
    fun getSlot(index: Int): BaseSlot? = slots.getOrNull(index)

    /**
     * 用于重置界面到初始状态
     */
    abstract fun reset()

    /**
     * 重置所有槽位
     */
    fun resetSlots() {
        for (slot in slots) {
            slot?.reset()
        }
    }

    /**
     * 将可输出的物品发给玩家
     */
    fun ejectItems(player: HumanEntity) {
        //弹出未拿出的物品
        for (slot in slots) {
            if (slot !is IOSlot) continue
            slot.eject(player)
        }
    }

    /**
     * 向所有满足条件的可输入端口输入物品
     * @param itemStack 待填充的物品
     * @return 剩余的物品
     */
    fun inputItem(itemStack: ItemStack?): ItemStack? {
        if (lockOnTop) return itemStack
        var temp: ItemStack? = itemStack
        for (slot in slots) {
            if (slot == null || slot !is IOSlot) continue
            if (temp == null) break
            val invItem = inventory.getItem(slot.index)
            //不是空的 不是占位符且不相似物品一定不可入
            if (invItem != null && invItem != slot.placeholder && !temp.isSimilar(invItem)) continue
            if (!slot.input(slot, temp)) continue
            //可以直接放进
            if (invItem == null || invItem == slot.placeholder) {
                slot.itemStack = temp
                val tempInput = temp.clone()
                temp = null
                submit {
                    slot.onInput(slot, tempInput)
                }
                break
            }
            val temp2 = invItem.merge(temp)
            //不兼容原有的物品
            if (temp == temp2) continue
            val tempInput = if (temp2 == null) {
                //说明没有余量
                temp.clone()
            } else {
                //说明有余量
                temp.clone().apply { amount -= temp2.amount }
            }
            temp = temp2
            submit {
                slot.onInput(slot, tempInput)
            }
        }
        return temp
    }

    /**
     * 将UI中的按钮安装到UI上
     */
    fun <T : BaseSlot> T.setup(): T {
        this@BaseUI.addSlot(this)
        return this
    }

    abstract fun clone(): BaseUI
    override var container: UIContainer? = null

    companion object {
        /**
         * 从 Inventory 获取 UI 对象
         */
        fun fromInventory(inventory: Inventory) = inventory.holder as? BaseUI
    }
}

/**
 * 设置点击时的动作
 */
fun <T : BaseUI> T.onClick(action: InventoryClickEvent.() -> Unit): T {
    this.onClick = action
    return this
}

/**
 * 设置点击后的动作
 */
fun <T : BaseUI> T.onClicked(async: Boolean = false, action: InventoryClickEvent.() -> Unit): T {
    this.async = async
    this.onClicked = action
    return this
}
