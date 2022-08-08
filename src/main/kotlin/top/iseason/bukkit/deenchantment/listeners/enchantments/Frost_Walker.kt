package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.player.PlayerMoveEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.BlockTimer
import java.util.*
import kotlin.math.sqrt

//熔岩行者
object Frost_Walker : BaseEnchant(DeEnchantments.DE_frost_walker) {
    @Key
    @Comment("", "半径等级乘数，最终半径 = radiusRate * level +1")
    var radiusRate = 1
    val fakeBlock = HashSet<Block>()

    fun clear() {
        for (block in fakeBlock) {
            block.setType(Material.LAVA, true)
        }
        fakeBlock.clear()
    }

    private val hasLevel = HashMap<UUID, Int>()

    @EventHandler(ignoreCancelled = true)
    fun onEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        if (!checkPermission(event.player as? Player)) return
        val deEnchantLevel = event.getDeLevel()
        val uniqueId = event.player.uniqueId
        if (deEnchantLevel > 0) {
            hasLevel[uniqueId] = deEnchantLevel
        } else {
            hasLevel.remove(uniqueId)
        }
    }

    @EventHandler//检测移动
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        if (player.gameMode == GameMode.SPECTATOR) return
        val uniqueId = player.uniqueId
        val level = hasLevel[uniqueId] ?: return
        val to = event.to?.clone() ?: return
        //脚下
        to.y = to.y - 1
        val round = getRound(to, level * radiusRate + 1)
        for (block in round) {
            if (block.type != Material.LAVA) continue
            if (player.eyeLocation.block.type == Material.LAVA ||
                player.location.block.type == Material.LAVA
            ) continue
            if ((block.blockData as Levelled).level != 0) continue
            fakeBlock.add(block)
            submit(delay = 160, task = BlockTimer(block))
        }
    }

    //防止破坏
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val block = event.block
        if (!fakeBlock.contains(block)) return
        event.isDropItems = false
        block.setType(Material.LAVA, true)
        event.isCancelled = true
    }

    //防止生物破坏
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityChangeBlockEvent(event: EntityChangeBlockEvent) {
        if (event.isCancelled) return
        val block = event.block
        if (!fakeBlock.contains(block)) return
        event.isCancelled = true //取消破坏
        block.setType(Material.LAVA, true)//变回岩浆
    }

    //    @EventHandler//防止烫伤
//    fun onEntityDamageEvent(event : EntityDamageEvent){
//        if(event.isCancelled)return
//        if(event.cause != EntityDamageEvent.DamageCause.HOT_FLOOR)return
//        val entity = event.entity
//        if(entity !is Player)return
//        val level = entity.equipment?.boots?.enchantments?.get(DeEnchantments.DE_frost_walker) ?:return
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