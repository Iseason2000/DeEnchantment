package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import java.util.*


object Binding_Curse : BaseEnchant(DeEnchantments.DE_binding_curse) {
    //灵魂绑定
    private val protectionMap = mutableMapOf<UUID, MutableMap<Int, ItemStack>>()

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val player = event.entity
        val drops = event.drops
        if (drops.isEmpty()) return//空背包 是否死亡不掉落也是空的？
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