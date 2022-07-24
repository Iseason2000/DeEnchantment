package top.iseason.bukkit.deenchantment.utils


import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EnchantingInventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.settings.Config

object EnchantTools {
    private val EN_NAMES: NamespacedKey = NamespacedKey(DeEnchantment.javaPlugin, "deenchantment_name")
    private val EN_DESCRIPTIONS: NamespacedKey = NamespacedKey(DeEnchantment.javaPlugin, "deenchantment_description")

    /**
     * 向物品加入负魔
     * @return 花费消耗
     */
    fun addEnchantments(
        target: ItemStack,
        en2: Map<DeEnchantmentWrapper, Int>,
        ignoreConflicts: Boolean
    ): Int {
        var cost = 0
        val itemMeta = target.itemMeta ?: return 0
        //已有的附魔
        val enchantments: MutableMap<Enchantment, Int> =
            (if (itemMeta is EnchantmentStorageMeta)
                itemMeta.storedEnchants
            else
                target.enchantments).toMutableMap()
        if (en2.isEmpty()) return 0
        en2.forEach { (e2, l2) ->
            //冲突判断
            if (!ignoreConflicts) {
                if (!e2.canEnchantItem(target)) return@forEach
                for ((e1, _) in enchantments) {
                    if (e2.conflictsWith(e1)) {
                        return@forEach
                    }
                }
            }
            //等级合并计算
            var level = l2
            if (enchantments.containsKey(e2)) {
                val nl2 = enchantments[e2]!!
                level = when {
                    nl2 > level -> nl2
                    nl2 == level -> level + 1
                    else -> level
                }
            }
            //最高等级限制
            if (!ignoreConflicts && !Config.levelUnlimited && level > e2.maxLevel) {
                level = e2.maxLevel
            }
            enchantments[e2] = level
            cost += level
        }
        addEnchants(itemMeta, enchantments)
        updateLore(itemMeta)
        target.itemMeta = itemMeta
        return cost
    }

    private fun getDeEnchantByEnchant(enchant: Enchantment): Enchantment? {
        val keyName = "de_${enchant.key.key}"
        return DeEnchantments.getByKeyName(keyName)
    }

    /**
     * 清楚负魔的lore
     */
    fun clearEnchantLore(itemMeta: ItemMeta) {
        if (!itemMeta.hasLore()) return
        val loreSet = itemMeta.lore?.toHashSet() ?: return
        val pdc = itemMeta.persistentDataContainer
        val names = pdc.get(EN_NAMES, PersistentDataType.TAG_CONTAINER)
        val descriptions = pdc.get(EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER)
        names?.keys?.forEach {
            val str = names.get(it, PersistentDataType.STRING)
            loreSet.remove(str)
        }
        descriptions?.keys?.forEach {
            val str = descriptions.get(it, PersistentDataType.STRING)
            loreSet.remove(str)
        }
        pdc.remove(EN_NAMES)
        pdc.remove(EN_DESCRIPTIONS)
        itemMeta.lore = loreSet.toMutableList()
    }

    fun translateEnchantsByChance(itemStack: ItemStack) {
        val enchantOrStoredEnchant = getEnchantOrStoredEnchant(itemStack)
        if (enchantOrStoredEnchant.isEmpty()) return
        val enchantByChance = translateEnchantByChance(enchantOrStoredEnchant).toMutableMap()
        clearEnchants(itemStack)
        val itemMeta = itemStack.itemMeta ?: return
        addEnchants(itemMeta, enchantByChance)
        updateLore(itemMeta)
        itemStack.itemMeta = itemMeta
    }

    fun clearEnchants(itemStack: ItemStack) {
        val itemMeta = itemStack.itemMeta ?: return
        if (itemMeta is EnchantmentStorageMeta) {
            val storedEnchants = itemMeta.storedEnchants
            for ((storedEnchant, _) in storedEnchants) {
                itemMeta.removeStoredEnchant(storedEnchant)
            }
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
            updateLore(itemMeta)
            itemStack.itemMeta = itemMeta
            enchantingInventory.item = itemStack
        }
    }

    fun translateEnchantByChance(enchantment: Map<Enchantment, Int>): Map<Enchantment, Int> {
        val enchants = enchantment.toMutableMap()
        val removeMap = mutableMapOf<Enchantment, Int>()
        val addSet = mutableSetOf<Enchantment>()
        for ((en, level) in enchants) {
            val deEnchantByEnchant = getDeEnchantByEnchant(en) as? DeEnchantmentWrapper ?: continue//剔除非原版附魔
            val random = Tools.getRandomDouble()
            if (random < deEnchantByEnchant.chance) {//概率计算
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

    /**
     * 根据负魔设置lore
     */
    fun updateLore(itemMeta: ItemMeta) {
        clearEnchantLore(itemMeta)
        val enchants = if (itemMeta is EnchantmentStorageMeta) itemMeta.storedEnchants else itemMeta.enchants
        if (enchants.isEmpty()) return
        if (itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return
        val loreList = itemMeta.lore ?: mutableListOf<String>()
        val pdc = itemMeta.persistentDataContainer
        val names = pdc.adapterContext.newPersistentDataContainer()
        val description = pdc.adapterContext.newPersistentDataContainer()
        for ((enchant, level) in enchants) {
            if (enchant !is DeEnchantmentWrapper) continue
            if (Config.allowDescription) {
                loreList.add(0, enchant.description)
                description.set(enchant.key, PersistentDataType.STRING, enchant.description)
            }
            val wholeName = "${enchant.translateName} ${Tools.intToRome(level)}"
            loreList.add(0, "${enchant.translateName} ${Tools.intToRome(level)}")
            names.set(enchant.key, PersistentDataType.STRING, wholeName)
        }
        pdc.set(EN_NAMES, PersistentDataType.TAG_CONTAINER, names)
        pdc.set(EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER, description)
        itemMeta.lore = loreList
    }

    //向物品Meta添加负魔
    fun addEnchants(itemMeta: ItemMeta, ens: MutableMap<Enchantment, Int>) {
        if (ens.isEmpty()) return
        if (itemMeta is EnchantmentStorageMeta) {
            for ((en, le) in ens)
                itemMeta.addStoredEnchant(en, le, true)
        } else {
            for ((en, le) in ens)
                itemMeta.addEnchant(en, le, true)
        }
    }

    //获取身上装备某种负魔的等级之和
    fun getLevelCount(entity: LivingEntity, deEnchantment: Enchantment): Int {
        val equipments = entity.equipment?.armorContents ?: return 0
        var levelCount = 0
        for (equipment in equipments)
            levelCount += equipment?.enchantments?.get(deEnchantment) ?: continue
        return levelCount
    }

}