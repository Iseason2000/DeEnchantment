package top.iseason.bukkit.deenchantment.utils


import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.hooks.EcoEnchantHook
import top.iseason.bukkit.deenchantment.listeners.enchantments.Binding_Curse
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.other.NumberUtils.toRoman
import top.iseason.bukkittemplate.utils.other.RandomUtils

object EnchantTools {
    val EN_NAMES: NamespacedKey = NamespacedKey(DeEnchantment.javaPlugin, "deenchantment_name")
    val EN_DESCRIPTIONS: NamespacedKey = NamespacedKey(DeEnchantment.javaPlugin, "deenchantment_description")

    /**
     * 向物品加入负魔
     * @return 花费消耗
     */
    fun addEnchantments(
        target: ItemStack,
        en2: Map<Enchantment, Int>,
        removes: MutableSet<Enchantment>,
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
                    //冲突的应该删除
                    if (e2 == e1) continue
                    if (e2.conflictsWith(e1) || e1.conflictsWith(e2)) {
                        removes.add(e2)
                        return@forEach
                    }
                }
            }
//            //别的附魔不需要我处理
//            if (e2 !is DeEnchantmentWrapper) return@forEach
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
            //只计算负魔的经验，其他的附魔不归我管
            if (e2 is DeEnchantmentWrapper)
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
        val lore = itemMeta.lore ?: return
        val pdc = itemMeta.persistentDataContainer
        val names = pdc.get(EN_NAMES, PersistentDataType.TAG_CONTAINER)
        val descriptions = pdc.get(EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER)
        //删除灵魂绑定
        if (names?.keys?.contains(DeEnchantments.DE_binding_curse.key) == false) {
            pdc.remove(Binding_Curse.EN_BINDING)
        }
        names?.keys?.forEach {
            val str = names.get(it, PersistentDataType.STRING)
            lore.remove(str)
        }
        descriptions?.keys?.forEach {
            val str = descriptions.get(it, PersistentDataType.STRING)
            lore.remove(str)
        }
        pdc.remove(EN_NAMES)
        pdc.remove(EN_DESCRIPTIONS)
        itemMeta.lore = lore
    }

    /**
     * 计算物品负魔突变
     */
    fun translateEnchantsByChance(itemStack: ItemStack) {
        try {
            val enchantOrStoredEnchant = getEnchantOrStoredEnchant(itemStack)
            if (enchantOrStoredEnchant.isEmpty()) return
            val enchantByChance = translateEnchantByChance(enchantOrStoredEnchant).toMutableMap()
            clearEnchants(itemStack)
            itemStack.applyMeta {
                addEnchants(this, enchantByChance)
                updateLore(this)
            }
        } catch (_: Throwable) {
        }
    }

    fun clearEnchants(itemStack: ItemStack) {
        itemStack.applyMeta {
            if (this is EnchantmentStorageMeta) {
                val storedEnchants = storedEnchants
                for ((storedEnchant, _) in storedEnchants) {
                    removeStoredEnchant(storedEnchant)
                }
            } else {
                val enchants = enchants
                for ((en, _) in enchants)
                    removeEnchant(en)
            }
        }
    }

    fun getEnchantOrStoredEnchant(itemStack: ItemStack): Map<Enchantment, Int> {
        val itemMeta = itemStack.itemMeta!!
        return if (itemMeta is EnchantmentStorageMeta) {
            itemMeta.storedEnchants
        } else
            itemMeta.enchants
    }

    //将附魔以其概率转换为负魔
    fun translateEnchantByChance(enchantment: Map<Enchantment, Int>): Map<Enchantment, Int> {
        val enchants = enchantment.toMutableMap()
        val removeMap = mutableMapOf<Enchantment, Int>()
        val addSet = mutableSetOf<Enchantment>()
        for ((en, level) in enchants) {
            val de = getDeEnchantByEnchant(en) as? DeEnchantmentWrapper ?: continue//剔除非原版附魔
            //未启用的跳过
            if (!de.enable) continue
            if (RandomUtils.checkPercentage(de.chance * 100)) {
                continue
            }
            removeMap[en] = level
            addSet.add(de)
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
        if (itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) || EcoEnchantHook.hasHook) return
        val loreList = mutableListOf<String>()
        val pdc = itemMeta.persistentDataContainer
        val names = pdc.adapterContext.newPersistentDataContainer()
        val descriptions = pdc.adapterContext.newPersistentDataContainer()
        for ((enchant, level) in enchants) {
            if (enchant !is DeEnchantmentWrapper) continue
            val wholeName =
                if (enchant.maxLevel == 1 && level == 1) "${enchant.translateName}  " else "${enchant.translateName} ${
                    level.toRoman()
                }"
            loreList.add(wholeName)
            if (Config.allowDescription) {
                var description = enchant.description
                //特殊处理灵魂绑定
                if (enchant == DeEnchantments.DE_binding_curse) {
                    val get = pdc.get(Binding_Curse.EN_BINDING, PersistentDataType.STRING)
                    if (get != null) {
                        description = description.replace(Binding_Curse.placeHolder, get.split(";").last())
                    }
                }
                loreList.add(description)
                descriptions.set(enchant.key, PersistentDataType.STRING, description)
            }
            names.set(enchant.key, PersistentDataType.STRING, wholeName)
        }
        pdc.set(EN_NAMES, PersistentDataType.TAG_CONTAINER, names)
        pdc.set(EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER, descriptions)
        if (!itemMeta.hasLore()) {
            itemMeta.lore = loreList
        } else {
            val lore = itemMeta.lore!!
            val lorePosition = if (loreList.size <= Config.lorePosition) loreList.size else Config.lorePosition
            lore.addAll(lorePosition, loreList)
            itemMeta.lore = lore
        }

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