package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.other.submit
import java.util.*

object Soul_Speed : BaseEnchant(DeEnchantments.DE_soul_speed) {
    @Key
    @Comment("", "加速百分比等级乘数")
    var speedRate = 0.15

    @Key
    @Comment("", "检索频率, 单位tick")
    var period: Long = 10

    @Key
    @Comment("", "加速的材质")
    var blocks = listOf(Material.DIRT.name, Material.GRASS_BLOCK.name)

    private var types = HashSet<Material>()
    private val uuid = UUID.fromString("1ff16a9b-3b50-4ed4-b9f6-9e83418e8c1f")
    private val playerMap = HashMap<Player, SpeedUpper>()

    override fun onLoaded(section: ConfigurationSection) {
        super.onLoaded(section)
        types = blocks.mapNotNull { Material.matchMaterial(it) }.toHashSet()
    }

    @EventHandler(ignoreCancelled = true)
    fun onDePlayerEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val player = event.player
        val deLevel = event.getDeLevel()
        if (deLevel == 0 || !checkPermission(event.player)) {
            val speedUpper = playerMap[player] ?: return
            speedUpper.cancel()
            playerMap.remove(player)
            return
        }
        val attributeModifier = AttributeModifier(
            uuid,
            "De_Soul_Speed",
            deLevel * speedRate,
            AttributeModifier.Operation.ADD_SCALAR
        )
        val speedUpper = playerMap.computeIfAbsent(player) {
            SpeedUpper(player, attributeModifier).also { runnable ->
                submit(period = period, async = true, task = runnable)
            }
        }
        speedUpper.setModifier(attributeModifier)
    }

    private class SpeedUpper(val player: Player, private var modifier: AttributeModifier) : BukkitRunnable() {
        private var attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!
        private var hasSpeed = false
        override fun cancel() {
            if (isCancelled) return
            super.cancel()
            attribute.removeModifier(modifier)
        }

        override fun run() {
            if (!player.isOnline) {
                cancel()
                playerMap.remove(player)
            }
            val location = player.location
            location.y -= 0.5
            val type = location.block.type
            //忽略空气的影响
            if (type.checkAir()) return
            if (types.contains(type)) {
                if (hasSpeed) return
                hasSpeed = true
                attribute.removeModifier(modifier)
                attribute.addModifier(modifier)
            } else {
                hasSpeed = false
                attribute.removeModifier(modifier)
            }
        }

        fun setModifier(modifier: AttributeModifier) {
            attribute.removeModifier(this.modifier)
            this.modifier = modifier
            attribute.addModifier(modifier)
        }
    }

}
