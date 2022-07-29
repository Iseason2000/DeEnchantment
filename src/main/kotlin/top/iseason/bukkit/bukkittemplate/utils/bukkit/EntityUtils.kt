package top.iseason.bukkit.bukkittemplate.utils.bukkit


import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * 给予有物品栏的对象物品,如果是实体且放不下将会放置到实体脚下
 * @param itemStacks 待输入的物品
 *
 */
fun InventoryHolder.giveItems(itemStacks: Array<ItemStack>) {
    giveItems(*itemStacks)
}

/**
 * 给予有物品栏的对象物品,如果是实体且放不下将会放置到实体脚下
 * @param itemStacks 待输入的物品
 *
 */
fun InventoryHolder.giveItems(itemStacks: Collection<ItemStack>) {
    giveItems(itemStacks.toTypedArray())
}

/**
 * 给予有物品栏的对象物品,如果是实体且放不下将会放置到实体脚下
 * @param itemStacks 待输入的物品
 *
 */
@JvmName("giveItemsVararg")
fun InventoryHolder.giveItems(vararg itemStacks: ItemStack) {
    val addItems = inventory.addItem(*itemStacks).values
    if (this !is Entity) return
    for (addItem in addItems) {
        if (addItem == null) continue
        (world.spawnEntity(location, EntityType.DROPPED_ITEM) as Item).itemStack = addItem
    }
}