package top.iseason.bukkit.deenchantment.hooks

import io.github.thebusybiscuit.slimefun4.api.events.AsyncMachineOperationFinishEvent
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter
import me.mrCookieSlime.Slimefun.api.BlockStorage
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object SlimeFun4Hook : Listener {
    var hasHook = Bukkit.getPluginManager().getPlugin("Slimefun") != null

    init {
        if (hasHook) info("&a检测到&6 Slimefun")
    }

    @EventHandler(ignoreCancelled = true)
    fun onAutoEnchantEvent(event: AsyncMachineOperationFinishEvent) {
        val owner = event.processor?.owner ?: return
        if (owner is AutoDisenchanter) {
            submit(async = true) {
                val inv: BlockMenu = BlockStorage.getInventory(event.position.block) ?: return@submit
                for (outputSlot in owner.outputSlots) {
                    inv.getItemInSlot(outputSlot)?.applyMeta {
                        EnchantTools.updateLore(this)
                    }
                }
            }
        } else if (owner is AutoEnchanter) {
            submit(async = true) {
                val inv: BlockMenu = BlockStorage.getInventory(event.position.block) ?: return@submit
                for (outputSlot in owner.outputSlots) {
                    inv.getItemInSlot(outputSlot)?.applyMeta {
                        EnchantTools.updateLore(this)
                    }
                }
            }
        }
    }

    fun register() {
        if (!hasHook) return
        DeEnchantment.registerListeners(this)
    }
}