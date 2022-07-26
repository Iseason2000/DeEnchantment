package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import java.util.*


object Binding_Curse : BaseEnchant(DeEnchantments.DE_binding_curse) {
    private val EN_BINDING: NamespacedKey = NamespacedKey(DeEnchantment.javaPlugin, "deenchantment_binding")

    //绑定玩家
    @EventHandler(ignoreCancelled = true)
    fun onItemDamage(event: PlayerItemDamageEvent) {
        val item = event.item
        if (!item.containsEnchantment(enchant)) return
        item.applyMeta {
            val pdc = persistentDataContainer
            val hasBind = pdc.get(EN_BINDING, PersistentDataType.STRING)
            //已经绑定
            if (hasBind != null) return@applyMeta
            val player = event.player
            pdc.set(EN_BINDING, PersistentDataType.STRING, player.uniqueId.toString())
            val descriptions =
                pdc.get(EnchantTools.EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER) ?: return@applyMeta
            val description = descriptions.get(enchant.key, PersistentDataType.STRING) ?: return@applyMeta
            val replace = description.replace("玩家", player.name)
            descriptions.set(enchant.key, PersistentDataType.STRING, replace)
            val lo = lore ?: return@applyMeta
            val indexOf = lo.indexOf(description)
            if (indexOf < 0) return@applyMeta
            lo[indexOf] = replace
            lore = lo
        }
    }

    //灵魂绑定
    private val protectionMap = mutableMapOf<UUID, MutableMap<Int, ItemStack>>()

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val player = event.entity
        val drops = event.drops
        if (drops.isEmpty()) return
        val contents = player.inventory.contents
        //位置不可能重复，故而在前
        val bindings = mutableMapOf<Int, ItemStack>()
        for (slot in contents.indices) {
            val itemStack = contents[slot] ?: continue
            val enchantments = itemStack.enchantments
            if (enchantments.isEmpty()) continue
            if (!enchantments.containsKey(DeEnchantments.DE_binding_curse)) continue
            bindings[slot] = itemStack
        }
        if (bindings.isEmpty()) return
        protectionMap[player.uniqueId] = bindings
        event.drops.removeAll(bindings.values.toSet())
    }

    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val bindings = protectionMap[uuid] ?: return
        val inventory = player.inventory
        for ((slot, item) in bindings) {
            if (inventory.getItem(slot) != null) continue//由其他插件设置或者物品不掉落
            inventory.setItem(slot, item)
        }
        protectionMap.remove(uuid)

    }
}