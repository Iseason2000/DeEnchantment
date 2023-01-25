package top.iseason.bukkittemplate.ui.slot

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import top.iseason.bukkittemplate.utils.bukkit.EntityUtils.giveItems
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.other.submit

/**
 * 物品槽，可以输入和输出物品
 * @param index 位置索引
 */
open class IOSlot(
    index: Int,
    //没有物品时的占位符
    placeholder: ItemStack?
) : ClickSlot(placeholder, index) {

    var placeholder: ItemStack?
        get() = rawItemStack
        set(value) {
            rawItemStack = value
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
            return if (item != null && !item.checkAir() && !item.isSimilar(placeholder)) item
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
     * 输入时异步调用，只对 onInput 有效
     */
    var inputAsync = false

    /**
     * 当输出物品时调用
     */
    var onOutput: IOSlot.(ItemStack) -> Unit = {}

    /**
     * 输出时异步调用，只对 onOutput 有效
     */
    var outputAsync = false

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


    override fun clone(index: Int): IOSlot = IOSlot(index, placeholder).also {
        it.input = input
        it.output = output
        it.onClick = onClick
        it.onClicked = onClicked
        it.onInput = onInput
        it.onOutput = onOutput
        it.asyncClick = asyncClick
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
fun <T : IOSlot> T.onInput(async: Boolean = false, onInput: ClickSlot.(ItemStack) -> Unit): T {
    this.inputAsync = async
    this.onInput = onInput
    return this
}

/**
 * 输出物品后调用 onOutput
 */
fun <T : IOSlot> T.onOutput(async: Boolean = false, onOutput: ClickSlot.(ItemStack) -> Unit): T {
    this.outputAsync = async
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

