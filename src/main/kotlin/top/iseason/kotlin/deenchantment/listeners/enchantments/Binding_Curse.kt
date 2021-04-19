package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import java.util.*


class Binding_Curse : Listener {
    //灵魂绑定
    private val protectionMap = mutableMapOf<UUID, MutableMap<ItemStack, Int>>()

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val player = event.entity
        val drops = event.drops
        if (drops.isEmpty()) return//死亡不掉落
        val contents = player.inventory.contents
        val bindings = mutableMapOf<ItemStack, Int>()
        for (slot in contents.indices) {
            val itemStack = contents[slot] ?: continue
            val enchantments = itemStack.enchantments
            if (enchantments.isEmpty()) continue
            if (!enchantments.containsKey(DeEnchantment.DE_binding_curse)) continue
            bindings[itemStack] = slot
        }
        if (bindings.isEmpty()) return
        protectionMap[player.uniqueId] = bindings
        event.drops.removeAll(bindings.keys)
    }

    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val bindings = protectionMap[uuid] ?: return
        val inventory = player.inventory
        for ((item, slot) in bindings) {
            if (inventory.getItem(slot) != null) continue
            inventory.setItem(slot, item)
        }
        protectionMap.remove(uuid)

    }
}