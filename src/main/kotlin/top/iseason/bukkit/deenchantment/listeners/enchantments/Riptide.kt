package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.deenchantment.events.call
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//焰流
object Riptide : BaseEnchant(DeEnchantments.DE_riptide) {
    @Key
    @Comment("", "速度等级乘数")
    var speedRate = 1.0

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        val trident = event.entity as? Trident ?: return
        val shooter = trident.shooter as? Player ?: return
        val level = trident.item.getDeLevel()
        if (level <= 0) return
        event.isCancelled = true
        val fireTicks = shooter.fireTicks
        if (fireTicks <= 0) return
        var item = trident.item
        //设置速度
        val direction = shooter.location.direction.multiply(level * speedRate)
        shooter.velocity = direction
        //扣耐久
        if (shooter.gameMode == GameMode.CREATIVE) return
        val itemInMainHand = shooter.inventory.getItem(EquipmentSlot.HAND)
        val itemInOffHand = shooter.inventory.getItem(EquipmentSlot.OFF_HAND)
        var isMainHand = false
        if (item == itemInMainHand) {
            item = itemInMainHand
            isMainHand = true
        } else if (item == itemInOffHand) {
            item = itemInOffHand
        }
        item.applyMeta {
            val m = this
            if (m !is Damageable) return@applyMeta
            val playerItemDamageEvent = PlayerItemDamageEvent(shooter, item, 1).call()
            if (playerItemDamageEvent.isCancelled) return@applyMeta
            m.damage += 1
            //损坏
            if (m.damage >= item.type.maxDurability) {
                if (isMainHand)
                    shooter.inventory.setItemInMainHand(null)
                else shooter.inventory.setItemInOffHand(null)
                shooter.playSound(shooter.eyeLocation, Sound.ENTITY_ITEM_BREAK, 1F, 1F)
                PlayerItemBreakEvent(shooter, item).call()
                return
            }
        }
    }

}