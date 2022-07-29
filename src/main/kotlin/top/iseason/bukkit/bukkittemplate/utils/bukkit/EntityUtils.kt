package top.iseason.bukkit.bukkittemplate.utils.bukkit


import org.bukkit.Material
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

/**
 * 扣除某种物品数量
 * @return true 数量足够
 */
fun InventoryHolder.takeItem(itemStack: ItemStack, amount: Int): Boolean {
    if (countItem(itemStack) < amount) return false
    var count = amount
    val contents = inventory.contents
    for (index in contents.indices) {
        val item = contents[index]
        if (!itemStack.isSimilar(item)) continue
        val am = item.amount
        if (count >= am) {
            count -= am
            inventory.clear(index)
        } else {
            item.amount -= am
            break
        }
    }
    return true
}

/**
 * 扣除某种材质的物品数量
 * @return true 数量足够
 */
fun InventoryHolder.takeItem(material: Material, amount: Int): Boolean {
    if (countItem(material) < amount) return false
    var count = amount
    val contents = inventory.contents
    for (index in contents.indices) {
        val item = contents[index] ?: continue
        if (material != item.type) continue
        val am = item.amount
        if (count >= am) {
            count -= am
            inventory.clear(index)
        } else {
            item.amount -= count
            break
        }
    }
    return true
}

/**
 * 统计某种物品的数量
 */
fun InventoryHolder.countItem(itemStack: ItemStack): Int {
    var count = 0
    this.inventory.contents.forEach {
        if (it == null) return@forEach
        if (!it.isSimilar(itemStack)) return@forEach
        count += it.amount
    }
    return count
}

/**
 * 统计某种材质的物品的数量
 */
fun InventoryHolder.countItem(material: Material): Int {
    var count = 0
    this.inventory.contents.forEach {
        if (it == null) return@forEach
        if (it.type != material) return@forEach
        count += it.amount
    }
    return count
}