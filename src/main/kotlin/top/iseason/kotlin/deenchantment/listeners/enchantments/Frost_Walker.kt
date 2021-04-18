package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.player.PlayerMoveEvent
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.BlockTimer
import kotlin.math.sqrt


class Frost_Walker : Listener {
    private val fakeBlock = HashSet<Block>()

    @EventHandler//检测移动
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val level = player.equipment?.boots?.enchantments?.get(DeEnchantment.DE_frost_walker) ?: return
        if (level == 0) return
        val to = event.to?.clone() ?: return
        to.y = to.y - 1
        val round = getRound(to, level + 1)
        for (block in round) {
            if (block.type != Material.LAVA) continue
            if (player.eyeLocation.block.type == Material.LAVA ||
                player.location.block.type == Material.LAVA
            ) continue
            fakeBlock.add(block)
            BlockTimer(block, fakeBlock).runTaskLater(ConfigManager.getPlugin(), 160)
        }
    }

    @EventHandler//防止破坏
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val block = event.block
        if (!fakeBlock.contains(block)) return
        event.isDropItems = false
        block.setType(Material.LAVA, true)
        event.isCancelled = true
    }

    @EventHandler//防止生物破坏
    fun onEntityChangeBlockEvent(event: EntityChangeBlockEvent) {
        if (event.isCancelled) return
        if (fakeBlock.contains(event.block)) event.isCancelled = true
    }

    //    @EventHandler//防止烫伤
//    fun onEntityDamageEvent(event : EntityDamageEvent){
//        if(event.isCancelled)return
//        if(event.cause != EntityDamageEvent.DamageCause.HOT_FLOOR)return
//        val entity = event.entity
//        if(entity !is Player)return
//        val level = entity.equipment?.boots?.enchantments?.get(DeEnchantment.DE_frost_walker) ?:return
//        if(level == 0)return
//        event.isCancelled = true
//
//    }
//    @EventHandler//防止推动
//    fun onBlockPistonExtendEvent(event : BlockPistonExtendEvent){
//        if(event.isCancelled)return
//        val blocks = event.blocks
//        for (block in blocks) {
//            if(fakeBlock.contains(block)) {
//                event.isCancelled = true
//                return
//            }
//        }
//    }
//    @EventHandler//防止拉动
//    fun onBlockPistonRetractEvent(event : BlockPistonRetractEvent){
//        if(event.isCancelled)return
//        val blocks = event.blocks
//        for (block in blocks) {
//            if(fakeBlock.contains(block)) {
//                event.isCancelled = true
//                return
//            }
//        }
//    }
    private fun getRound(center: Location, radius: Int): ArrayList<Block> {
        val sphere = ArrayList<Block>()
        for (X in -radius until radius + 1)
            for (Z in -radius until radius + 1) if (sqrt((X * X + Z * Z).toDouble()) <= radius) {
                val block = center.world!!
                    .getBlockAt(X + center.blockX, center.blockY, Z + center.blockZ)
                sphere.add(block)
            }
        return sphere
    }

}