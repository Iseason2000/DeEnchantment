package top.iseason.kotlin.deenchantment.utils

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable

class BlockTimer(val block: Block, val blockSet: HashSet<Block>) : BukkitRunnable() {
    init {
        block.setType(Material.OBSIDIAN, false)
    }

    override fun run() {
        block.setType(Material.LAVA, true)
        blockSet.remove(block)
    }

}