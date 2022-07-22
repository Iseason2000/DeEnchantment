package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.debug.debug
import top.iseason.bukkit.bukkittemplate.utils.WeakCoolDown
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.bukkit.subtract
import top.iseason.bukkit.bukkittemplate.utils.submit

/**
 * 负责所有UI的监听动作
 */
object UIListener : Listener {

    //    private val playerClickTime = mutableMapOf<HumanEntity, Long>()
    private val clickCoolDown = WeakCoolDown<HumanEntity>()

    /**
     * 在插件注销时关闭所有UI
     */
    fun onDisable() {
        Bukkit.getOnlinePlayers().forEach {
            val baseUI = BaseUI.fromInventory(it.openInventory.topInventory) ?: return
            baseUI.onClose(null)
            baseUI.ejectItems(it)
            it.closeInventory()
        }
    }

    fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, TemplatePlugin.getPlugin())
    }

    @EventHandler
    fun onInventoryOpenEvent(event: InventoryOpenEvent) {
        val baseUI = BaseUI.fromInventory(event.inventory) ?: return
        baseUI.onOpen(event)
    }

    @EventHandler
    fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        val baseUI = BaseUI.fromInventory(event.inventory) ?: return
        baseUI.onClose(event)
        baseUI.ejectItems(event.player)
        clickCoolDown.remove(event.player)
    }

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        val inventory = event.inventory
        val baseUI = BaseUI.fromInventory(inventory) ?: return
        val whoClicked = event.whoClicked
        if (clickCoolDown.check(whoClicked, baseUI.clickDelay)) {
            event.isCancelled = true
            return
        }
        var isCancelled = false
        val rawSlot = event.rawSlot
        //点击上下锁，优先级大于slot
        val clickedClickSlot: ClickSlot? = baseUI.getSlot(rawSlot) as? ClickSlot
        if (clickedClickSlot != null) {
            clickedClickSlot.onClick?.let { it(clickedClickSlot, event) }
            submit {
                clickedClickSlot.onClicked?.let { it(clickedClickSlot, event) }
            }
        }
        /**
         * 上下锁
         */
        if (rawSlot in 0 until inventory.size && baseUI.lockOnTop) {
            if (clickedClickSlot !is IOSlot) isCancelled = true
            debug("ui ${baseUI::class.simpleName} lockup, slot $rawSlot")
        } else if (rawSlot > 0 && baseUI.lockOnBottom) {
            isCancelled = true
            debug("ui ${baseUI::class.simpleName} lockdown, slot $rawSlot")
        }
        event.isCancelled = isCancelled
        event.ioEvent()
        baseUI.onClick(event)
        submit {
            if (whoClicked is Player) whoClicked.updateInventory()
            baseUI.onClicked(event)
        }
    }

    @EventHandler
    fun onInventoryDragEvent(event: InventoryDragEvent) {
        event.ioEvent()
    }

}

/**
 * 处理事件的输入\输出动作
 */
fun InventoryDragEvent.ioEvent() {
    val baseUI = BaseUI.fromInventory(inventory) ?: return
    var tempItem: ItemStack? = null
    for ((index, newItem) in newItems) {
        val ioSlot = baseUI.getSlot(index) ?: continue
        if (ioSlot is IOSlot && ioSlot.input(ioSlot, newItem)) {
            val itemStack = ioSlot.itemStack
            if (itemStack != null) {
                ioSlot.itemStack = newItem.apply {
                    amount -= itemStack.amount
                }
            } else {
                ioSlot.itemStack = newItem
            }
            submit {
                ioSlot.onInput(ioSlot, newItem)
            }
        } else {
            if (tempItem == null) tempItem = newItem.clone()
            else tempItem.merge(newItem)
            val item = view.getItem(index)?.clone()
            submit {
                view.setItem(index, item)
            }
        }
    }
    if (cursor == null) cursor = tempItem
    else cursor!!.merge(tempItem)
}

/**
 * 处理事件的输入\输出动作
 */
fun InventoryClickEvent.ioEvent() {
    val baseUI = BaseUI.fromInventory(inventory) ?: return
    val inputItem: Pair<Int, ItemStack>?
    val outputItem: Pair<Int, ItemStack>?
    when (action) {
        // 检测输入的物品
        PLACE_ALL,
        PLACE_ONE,
        PLACE_SOME -> {
            if (rawSlot in 0 until inventory.size) {
                //处理占位符
                inputItem = if (cursor?.type?.checkAir() == true) null
                else Pair(rawSlot, cursor!!.clone()).apply {
                    if (action == PLACE_ONE) second.amount = 1
                    val currentItem = currentItem
                    if (action == PLACE_SOME && currentItem != null) {
                        second.amount = cursor!!.amount - currentItem.amount
                    }
                }
            } else inputItem = null
            outputItem = null
        }
        //检测输出
        PICKUP_ALL,
        PICKUP_SOME,
        PICKUP_HALF,
        PICKUP_ONE -> {
            if (rawSlot in 0 until inventory.size && currentItem != null) {
                val item = currentItem!!.clone().apply {
                    amount = when (action) {
                        PICKUP_HALF -> {
                            amount - amount / 2
                        }
                        PICKUP_ONE -> 1
                        PICKUP_SOME -> maxStackSize
                        else -> amount
                    }
                }
                outputItem = Pair(rawSlot, item)
            } else outputItem = null
            inputItem = null
        }
        DROP_ALL_SLOT -> {
            outputItem = Pair(rawSlot, currentItem!!.clone())
            inputItem = null
        }
        DROP_ONE_SLOT -> {
            outputItem = Pair(rawSlot, currentItem!!.clone().apply { amount = 1 })
            inputItem = null
        }
        COLLECT_TO_CURSOR -> {
            //双击收集特殊处理
            val cursor = cursor!!.clone()
            var amount = cursor.amount
            val maxStackSize = cursor.maxStackSize
            submit {
                //处理Slot
                if (!baseUI.lockOnTop) {
                    for (slot in baseUI.slots) {
                        if (slot == null) continue
                        if (slot !is IOSlot) continue
                        if (amount >= maxStackSize) break
                        val itemStack = slot.itemStack ?: continue
                        //不输出placeHolder
                        if (itemStack.isSimilar(slot.placeholder)) continue
                        if (!cursor.isSimilar(itemStack)) continue
                        val slotAmount = itemStack.amount
                        val need = maxStackSize - amount
                        val outputAmount = if (slotAmount <= need) slotAmount else need
                        val output = itemStack.clone().apply { this.amount = outputAmount }
                        if (slot.output(slot, output)) {
                            amount += outputAmount
                            slot.onOutput(slot, output)
                            itemStack.subtract(outputAmount)
                        }
                    }
                }
                //处理玩家背包
                if (!baseUI.lockOnBottom) {
                    val bottomInventory = view.bottomInventory
                    if (!baseUI.lockOnBottom)
                        for (itemStack in bottomInventory) {
                            if (amount >= maxStackSize) break
                            if (itemStack == null) continue
                            if (!cursor.isSimilar(itemStack)) continue
                            val slotAmount = itemStack.amount
                            val need = maxStackSize - amount
                            val outputAmount = if (slotAmount <= need) slotAmount else need
                            amount += outputAmount
                            itemStack.subtract(outputAmount)
                        }
                    view.cursor = cursor.apply { this.amount = amount }
                }
            }
            isCancelled = true
            return
        }

        //不只是输入，也有输出
        SWAP_WITH_CURSOR -> {
            if (rawSlot in 0 until inventory.size) {
                outputItem = Pair(rawSlot, currentItem!!.clone())
                inputItem = Pair(rawSlot, cursor!!.clone())
            } else {
                inputItem = null
                outputItem = null
            }
        }
        //Shift
        MOVE_TO_OTHER_INVENTORY -> {
            //输入
            if (rawSlot > inventory.size - 1) {
                val temp = currentItem
                currentItem = baseUI.inputItem(temp)
                if (currentItem == temp) isCancelled = true
                return
            } else {
                if (baseUI.lockOnBottom) {
                    isCancelled = true
                    return
                }
                //输出
                outputItem = Pair(rawSlot, currentItem!!.clone())
                inputItem = null
            }
        }
        HOTBAR_SWAP, HOTBAR_MOVE_AND_READD -> {
            if (rawSlot in 0 until inventory.size) {
                //处理占位符
                val bottomInventory = view.bottomInventory
                val item = bottomInventory.getItem(hotbarButton)
                inputItem = if (item != null)
                    Pair(rawSlot, item.clone())
                else null
                outputItem = if (currentItem != null) Pair(slot, currentItem!!.clone()) else null
            } else {
                inputItem = null
                outputItem = null
            }
        }
        else -> {
            inputItem = null
            outputItem = null
        }
    }
    var setCancel = false
// 存在交换的情况,仅处理是否取消
    if ((inputItem != null) && (outputItem != null)) {
        if (baseUI.lockOnTop || baseUI.lockOnBottom) {
            isCancelled = true
            return
        }
        //2个应该是同一个
        val slot = baseUI.getSlot(inputItem.first) as? IOSlot
//        val outputSlot = baseUI.getSlot(outputItem.first) as? IOSlot
        //有一个不通过就取消
        if (slot != null) {
            val isPlaceholder = outputItem.second.isSimilar(slot.placeholder)
            if (!isPlaceholder) {
                val output = slot.output(slot, outputItem.second)
                if (output) {
                    submit {
                        slot.onOutput(slot, inputItem.second)
                    }
                } else {
                    isCancelled = true
                    return
                }
            }
            val input = slot.input(slot, inputItem.second)
            if (input) {
                if (isPlaceholder) inventory.setItem(slot.index, null)
                submit { slot.onInput(slot, inputItem.second) }
            } else {
                isCancelled = true
                return
            }
        } else {
            isCancelled = true
        }
        return
    }
    // 仅输出
    if (outputItem != null) {
        if (outputItem.first in 0 until baseUI.inventory.size && baseUI.lockOnTop) {
            isCancelled = true
            return
        }
        val slot = baseUI.getSlot(outputItem.first) ?: return
        if (slot is IOSlot) {
            //不允许输出Placeholder
            if (outputItem.second.isSimilar(slot.placeholder)) {
                setCancel = true
                slot.itemStack = slot.placeholder
            } else if (slot.output(slot, outputItem.second)) {
                submit {
                    if (inventory.getItem(outputItem.first) == null)
                        inventory.setItem(outputItem.first, slot.placeholder)
                    slot.onOutput(slot, outputItem.second)
                    //设回PlaceHolder
                }
            } else setCancel = true
        } else setCancel = true
    }
    //仅输入
    if (inputItem != null) {
        if (inputItem.first in 0 until baseUI.inventory.size && baseUI.lockOnTop) {
            isCancelled = true
            return
        }
        val ioSlot = baseUI.getSlot(inputItem.first) as? IOSlot ?: return
        if (ioSlot.input(ioSlot, inputItem.second)) {
            //能够仅输入的必不存在placeHolder
//            if (baseUI.inventory.getItem(ioSlot.index)?.isSimilar(ioSlot.placeholder) == true)
//                ioSlot.itemStack = null
            submit {
                ioSlot.onInput(ioSlot, inputItem.second)
            }
        } else setCancel = true
    }
    if (setCancel)
        isCancelled = true

}
