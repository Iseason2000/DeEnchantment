package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.bukkit.giveItems
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import java.util.*

object Binding_Curse : BaseEnchant(DeEnchantments.DE_binding_curse) {
    @Key
    @Comment("", "当有灵魂绑定的物品消耗耐久时将绑定为该玩家，并发送消息")
    var bindMessage = "&a您的装备已绑定您的灵魂"

    @Key
    @Comment("", "不能使用时发送的消息")
    var ownerMessage = "&c你不能使用绑定了别人灵魂的装备!"

    @Key
    @Comment("", "防止灵魂绑定被他人用于铁砧")
    var denyAnvil = true

    @Key
    @Comment("", "防止灵魂绑定被他人用于砂轮")
    var denyGrindStone = true

    @Key
    @Comment("", "描述中用于替换玩家名字的占位符")
    var placeHolder = "玩家"

    val EN_BINDING: NamespacedKey = NamespacedKey(DeEnchantment.javaPlugin, "deenchantment_binding")

    //绑定玩家
    @EventHandler(ignoreCancelled = true)
    fun onItemDamage(event: PlayerItemDamageEvent) {
        val item = event.item
        if (!item.containsEnchantment(enchant)) return
        item.applyMeta {
            val pdc = persistentDataContainer
            val hasBind = pdc.get(EN_BINDING, PersistentDataType.STRING)
            //已经绑定
            if (hasBind != null) return@applyMeta
            val player = event.player
            pdc.set(EN_BINDING, PersistentDataType.STRING, "${player.uniqueId};${player.name}")
            player.sendColorMessage(bindMessage)
            val descriptions =
                pdc.get(EnchantTools.EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER) ?: return@applyMeta
            val description = descriptions.get(enchant.key, PersistentDataType.STRING) ?: return@applyMeta
            val replace = description.replace(placeHolder, player.name)
            descriptions.set(enchant.key, PersistentDataType.STRING, replace)
            pdc.set(EnchantTools.EN_DESCRIPTIONS, PersistentDataType.TAG_CONTAINER, descriptions)
            val lo = lore ?: return@applyMeta
            val indexOf = lo.indexOf(description)
            if (indexOf < 0) return@applyMeta
            lo[indexOf] = replace
            lore = lo
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onItemUseBreak(event: BlockBreakEvent) {
        if (!checkMainHand(event.player)) return
        event.isCancelled = true
        //message
        event.player.sendColorMessage(ownerMessage)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onItemUseRight(event: PlayerInteractEvent) {
        if (!(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) return
        if (!checkMainHand(event.player) && !checkOffHand(event.player)) {
            return
        }
        event.isCancelled = true
        event.player.sendColorMessage(ownerMessage)
        //message
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onItemUseAttack(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        if (!checkMainHand(damager)) return
        event.isCancelled = true
        //message
        damager.sendColorMessage(ownerMessage)
    }

    @EventHandler(ignoreCancelled = true)
    fun onGrindStone(event: InventoryClickEvent) {
        if (!denyGrindStone) return
        val player = event.whoClicked as Player
        val top = player.openInventory
        if (top.type != InventoryType.GRINDSTONE) return
        if (event.slotType != SlotType.RESULT) return
        val uid = player.uniqueId.toString()
        val item1 = top.getItem(0)
        if (item1 != null) {
            val soulBindOwner = item1.getSoulBindOwner() ?: return
            if (soulBindOwner != uid) {
                event.isCancelled = true
                player.sendColorMessage(ownerMessage)
                return
            }
        }
        val item2 = top.getItem(1)
        if (item2 != null) {
            val soulBindOwner = item2.getSoulBindOwner() ?: return
            if (soulBindOwner != uid) {
                event.isCancelled = true
                player.sendColorMessage(ownerMessage)
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    fun onAnvil(event: PrepareAnvilEvent) {
        if (!denyAnvil) return
        val inventory = event.inventory
        val player = event.viewers.firstOrNull() ?: return
        val uid = player.uniqueId.toString()
        val item1 = inventory.getItem(0) ?: return
        val owner1 = item1.getSoulBindOwner()
        if (owner1 != null && uid != owner1) {
            inventory.setItem(0, null)
            player.sendColorMessage(ownerMessage)
            // message
            submit {
                player.giveItems(item1)
            }
        }
        val item2 = inventory.getItem(1) ?: return
        val owner2 = item2.getSoulBindOwner() ?: return
        if (uid != owner2) {
            inventory.setItem(1, null)
            player.sendColorMessage(ownerMessage)
            // message
            submit {
                player.giveItems(item2)
            }
        }
    }

    @EventHandler
    fun onEquipment(event: DePlayerEquipmentChangeEvent) {
        val player = event.player
        val armorContents = event.armors
        val listOf = mutableListOf<ItemStack>()
        armorContents.forEachIndexed { index, item ->
            if (item == null) return@forEachIndexed
            if (item.type.checkAir()) return@forEachIndexed
            if (!item.containsEnchantment(enchant)) return@forEachIndexed
            val uuid = item.getSoulBindOwner() ?: return@forEachIndexed
            if (player.uniqueId.toString() != uuid) {
                armorContents[index] = null
                listOf.add(item)
            }
        }
        if (listOf.isNotEmpty()) {
            player.sendColorMessage(ownerMessage)
            player.giveItems(listOf)
//            player.updateInventory()
        }
    }

    //灵魂绑定
    private val protectionMap = mutableMapOf<UUID, MutableMap<Int, ItemStack>>()

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val player = event.entity
        val drops = event.drops
        if (drops.isEmpty()) return
        if (event.keepInventory) return
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


    //判断是否不符合灵魂绑定true表示该取消
    private fun checkMainHand(player: Player): Boolean {
        val itemInMainHand = player.equipment?.itemInMainHand ?: return false
        return checkItem(player, itemInMainHand)
    }

    private fun checkOffHand(player: Player): Boolean {
        val itemInOffHand = player.equipment?.itemInOffHand ?: return false
        return checkItem(player, itemInOffHand)
    }

    private fun checkItem(player: Player, item: ItemStack): Boolean {
        if (item.type.checkAir()) return false
        if (!item.containsEnchantment(enchant)) return false
        val uuid = item.getSoulBindOwner() ?: return false
        return player.uniqueId.toString() != uuid
    }

    private fun ItemStack.getSoulBindOwner(): String? {
        return itemMeta?.persistentDataContainer?.get(EN_BINDING, PersistentDataType.STRING)?.split(";")?.first()
    }
}