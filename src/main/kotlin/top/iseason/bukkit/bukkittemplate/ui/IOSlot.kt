package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.bukkit.giveItems
import top.iseason.bukkit.bukkittemplate.utils.submit

/**
 * 物品槽，可以输入和输出物品
 * @param index 位置索引
 */
open class IOSlot(
    index: Int,
    //没有物品时的占位符
    var placeholder: ItemStack? = null
) : ClickSlot(placeholder, index) {

    init {
        //默认开启输出输入
        lockable(false)
    }

    /**
     * 与Inventory的ItemStack同步,null时显示占位符
     */
    override var itemStack: ItemStack?
        set(value) {
            if (value == null) baseInventory?.setItem(index, placeholder)
            else baseInventory?.setItem(index, value)
        }
        get() {
            val item = baseInventory?.getItem(index)
            return if (item != null && !item.type.checkAir() && !item.isSimilar(placeholder)) item
            else null
        }

    /**
     * 输入过滤器，this为当前槽,it为输入的物品
     * @return true 以允许输入
     */
    var input: IOSlot.(ItemStack) -> Boolean = { true }

    /**
     * 输出过滤器，this为当前槽,it为输出的部分
     * @return true 以允许输出
     */
    var output: IOSlot.(ItemStack) -> Boolean = { true }

    /**
     * 当输入物品时调用
     */
    var onInput: IOSlot.(ItemStack) -> Unit = {}

    /**
     * 当输出物品时调用
     */
    var onOutput: IOSlot.(ItemStack) -> Unit = {}

    /**
     * 将物品给某人
     */
    fun eject(humanEntity: HumanEntity) {
        val itemStack = itemStack
        if (itemStack != null && output(this, itemStack)) {
            humanEntity.giveItems(itemStack)
            submit {
                onOutput(this@IOSlot, itemStack)
            }
            reset()
        }
    }

    override fun reset() {
        itemStack = null
    }

    override fun clone(index: Int): IOSlot = IOSlot(index).also {
        it.itemStack = itemStack
        it.placeholder = placeholder
        it.input = input
        it.output = output
        it.onClick = onClick
        it.onClicked = onClicked
        it.onInput = onInput
        it.onOutput = onOutput
        it.baseInventory = baseInventory
    }
}

/**
 *  是否上锁,返回true上锁
 */
fun <T : IOSlot> T.lockable(lockable: Boolean): T {
    inputAble(!lockable)
    outputAble(!lockable)
    return this
}

/**
 * 输入过滤器，this为当前槽,it为输入的物品
 * @return true 以允许输入
 */
fun <T : IOSlot> T.inputFilter(filter: IOSlot.(ItemStack) -> Boolean): T {
    this.input = filter
    return this
}

/**
 * 输出过滤器，this为当前槽,it为输出的物品
 * @return true 以允许输出
 */
fun <T : IOSlot> T.outputFilter(filter: IOSlot.(ItemStack) -> Boolean): T {
    this.output = filter
    return this
}

/**
 * 是否允许输出物品
 */
fun <T : IOSlot> T.outputAble(outputAble: Boolean): T {
    this.output = { outputAble }
    return this
}

/**
 * 是否允许输入物品
 */
fun <T : IOSlot> T.inputAble(inputAble: Boolean): T {
    this.input = { inputAble }
    return this
}

/**
 * 输入物品后调用 onInput
 */
fun <T : IOSlot> T.onInput(onInput: ClickSlot.(ItemStack) -> Unit): T {
    this.onInput = onInput
    return this
}

/**
 * 输出物品后调用 onOutput
 */
fun <T : IOSlot> T.onOutput(onOutput: ClickSlot.(ItemStack) -> Unit): T {
    this.onOutput = onOutput
    return this
}

/**
 * 合并相同的ItemStack,
 * @return 合并剩余的ItemStack,无剩余返回null
 */
fun ItemStack.merge(itemStack: ItemStack?): ItemStack? {
    val item = itemStack?.clone() ?: return null
    return if (isSimilar(item)) {
        val total = item.amount + amount
        val i = total - maxStackSize
        if (i <= 0) {
            amount = total
            null
        } else {
            item.amount = i
            amount = maxStackSize
            item
        }
    } else item
}

