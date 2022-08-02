package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
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
    private val playerMap = HashMap<Player, BukkitRunnable>()

    override val onLoaded: FileConfiguration.() -> Unit
        get() = {
            super.onLoaded(this)
            types = blocks.mapNotNull { Material.matchMaterial(it) }.toHashSet()
        }

    @EventHandler(ignoreCancelled = true)
    fun onDePlayerEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val player = event.player
        val deLevel = event.getDeLevel()
        playerMap[player]?.cancel()
        playerMap.remove(player)
        if (deLevel == 0) {
            return
        }
        val speedUpper = SpeedUpper(
            player, AttributeModifier(
                uuid,
                "De_Soul_Speed",
                deLevel * speedRate,
                AttributeModifier.Operation.ADD_SCALAR
            )
        )
        playerMap[player] = speedUpper
        submit(period = period, async = true, task = speedUpper)
    }

    class SpeedUpper(val player: Player, private val modifier: AttributeModifier) : BukkitRunnable() {
        private val attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!
        private var hasSpeed = false
        override fun cancel() {
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
            val attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) ?: return
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
    }

}
