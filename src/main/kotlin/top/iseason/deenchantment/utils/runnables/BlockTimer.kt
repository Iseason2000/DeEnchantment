package top.iseason.deenchantment.utils.runnables

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.deenchantment.listeners.enchantments.Frost_Walker

class BlockTimer(private val block: Block) : BukkitRunnable() {
    init {
        block.setType(Material.OBSIDIAN, false)
    }

    override fun run() {
        block.setType(Material.LAVA, true)
        Frost_Walker.fakeBlock.remove(block)
    }

}