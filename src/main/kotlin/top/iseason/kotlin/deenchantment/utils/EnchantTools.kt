package top.iseason.kotlin.deenchantment.utils

import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EnchantingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.kotlin.deenchantment.DeEnchantmentWrapper
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import java.security.SecureRandom

object EnchantTools {
    private val RANDOM = SecureRandom.getInstance("SHA1PRNG", "SUN")
    fun setDeEnchantLore(itemMeta: ItemMeta) {
        if (itemMeta is EnchantmentStorageMeta)
            setLoreWithEnchants(itemMeta, itemMeta.storedEnchants)
        else
            setLoreWithEnchants(itemMeta, itemMeta.enchants)
    }

    fun addEnchantments(target: ItemStack, en2: Map<Enchantment, Int>): Int {
        var cost = 0
        val itemMeta = target.itemMeta!!
        val enchantments: Map<Enchantment, Int> =
            if (itemMeta is EnchantmentStorageMeta)
                itemMeta.storedEnchants
            else
                target.enchantments
        val en1 = enchantments.toMutableMap()
        if (en2.isNullOrEmpty()) return 0
        for ((e2, l2) in en2) {
            if (target.type != Material.ENCHANTED_BOOK && !e2.canEnchantItem(target)) continue
            var isConflict = false
            for ((e1, _) in en1) {
                if (e1 != e2 && e2.conflictsWith(e1)) {
                    isConflict = true
                    break
                }
            }
            if (isConflict) continue
            var level = l2
            if (en1.containsKey(e2)) {
                val nl2 = en1[e2]!!
                level = when {
                    nl2 == level -> level + 1
                    nl2 > level -> nl2
                    else -> level
                }
            }
            en1[e2] = level
            cost += level
        }
        addEnchants(itemMeta, en1)
        setDeEnchantLore(itemMeta)
        target.itemMeta = itemMeta
        return cost
    }

    private fun getDeEnchantByEnchant(enchant: Enchantment): Enchantment? {
        val keyName = "de_${enchant.key.key}"
        return DeEnchantment.getByKeyName(keyName)
    }

    private fun clearEnchantLore(itemMeta: ItemMeta) {
        if (!itemMeta.hasLore()) return
        var loreList = itemMeta.lore!!
        val deEnchantmentsList = ConfigManager.getDeEnchantmentsNameList()
        for (name in deEnchantmentsList) {
            loreList = loreList.filterNot {
                it.contains(Regex("$name \\w+?"))
            }
        }
        itemMeta.lore = loreList
    }

    fun translateEnchantsByChance(itemStack: ItemStack) {
        val enchantOrStoredEnchant = getEnchantOrStoredEnchant(itemStack)
        if (enchantOrStoredEnchant.isNullOrEmpty()) return
        val enchantByChance = translateEnchantByChance(enchantOrStoredEnchant)
        clearEnchants(itemStack)
        val itemMeta = itemStack.itemMeta!!
        addEnchants(itemMeta, enchantByChance.toMutableMap())
        setDeEnchantLore(itemMeta)
        itemStack.itemMeta = itemMeta
    }

    fun clearEnchants(itemStack: ItemStack) {
        val itemMeta = itemStack.itemMeta!!
        if (itemMeta is EnchantmentStorageMeta) {
            val storedEnchants = itemMeta.storedEnchants
            for ((storedEnchant, _) in storedEnchants)
                itemMeta.removeStoredEnchant(storedEnchant)
        } else {
            val enchants = itemMeta.enchants
            for ((en, _) in enchants)
                itemMeta.removeEnchant(en)
        }
        itemStack.itemMeta = itemMeta
    }

    fun getEnchantOrStoredEnchant(itemStack: ItemStack): Map<Enchantment, Int> {
        val itemMeta = itemStack.itemMeta!!
        return if (itemMeta is EnchantmentStorageMeta) {
            itemMeta.storedEnchants
        } else
            itemMeta.enchants
    }

    class LoreSetter(
        private val enchantingInventory: EnchantingInventory,
        private val enchants: Map<Enchantment, Int>
    ) : BukkitRunnable() {
        override fun run() {
            val itemStack = enchantingInventory.item?.clone() ?: return
            clearEnchants(itemStack)
            val itemMeta = itemStack.itemMeta!!
            addEnchants(itemMeta, enchants.toMutableMap())
            setDeEnchantLore(itemMeta)
            itemStack.itemMeta = itemMeta
            enchantingInventory.item = itemStack
        }
    }

    fun translateEnchantByChance(enchantment: Map<Enchantment, Int>): Map<Enchantment, Int> {
        val enchants = enchantment.toMutableMap()
        val removeMap = mutableMapOf<Enchantment, Int>()
        val addSet = mutableSetOf<Enchantment>()
        for ((en, level) in enchants) {
            val deEnchantByEnchant = getDeEnchantByEnchant(en)
            val enchantmentChance = ConfigManager.getEnchantmentChance("De_${en.key.key}".toUpperCase()) ?: 0.0
            val random = RANDOM.nextDouble()
            if (deEnchantByEnchant != null && random < enchantmentChance) {//概率计算
                removeMap[en] = level
                addSet.add(deEnchantByEnchant)
            }
        }
        val iterator = addSet.iterator()
        for ((en, l) in removeMap) {
            enchants.remove(en)
            enchants[iterator.next()] = l
        }
        return enchants
    }

    private fun setLoreWithEnchants(itemMeta: ItemMeta, enchants: Map<Enchantment, Int>) {
        clearEnchantLore(itemMeta)
        if (enchants.isEmpty()) return
        val loreList = itemMeta.lore ?: mutableListOf<String>()
        for ((enchant, level) in enchants) {
            if (enchant !is DeEnchantmentWrapper) continue
            val enchantmentName = ConfigManager.getEnchantmentName(enchant.name)!!
            val wholeName = "$enchantmentName ${Tools.intToRome(level)}"
            loreList.add(0, wholeName)
        }
        itemMeta.lore = loreList
    }


    fun addEnchants(itemMeta: ItemMeta, ens: MutableMap<Enchantment, Int>) {
        if (ens.isNullOrEmpty()) return
        if (itemMeta is EnchantmentStorageMeta) {
            for ((en, le) in ens)
                itemMeta.addStoredEnchant(en, le, true)
        } else {
            for ((en, le) in ens)
                itemMeta.addEnchant(en, le, true)
        }
    }

    fun getRepairCost(item: ItemStack): Int {
        return if (NBTEditor.contains(item, "RepairCost")) {
            NBTEditor.getInt(item, "RepairCost")
        } else 0
    }

    fun setRepairCost(item: ItemStack, cost: Int): ItemStack {
        return NBTEditor.set(item, cost, "RepairCost")
    }

    fun getLevelCount(entity: LivingEntity, deEnchantment: Enchantment): Int {
        val equipments = entity.equipment?.armorContents ?: return 0
        var levelCount = 0
        for (equipment in equipments)
            levelCount += equipment?.enchantments?.get(deEnchantment) ?: continue
        return levelCount
    }

}