package top.iseason.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.inventory.MerchantRecipe
import top.iseason.deenchantment.utils.EnchantTools

class MerchantListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onVillagerAcquireTradeEvent(event: VillagerAcquireTradeEvent) {
        if (event.isCancelled) return
        val recipe = event.recipe
        val result = recipe.result
        val enchant = EnchantTools.getEnchantOrStoredEnchant(result)
        if(enchant.isEmpty())return
        EnchantTools.translateEnchantsByChance(result)
        event.recipe = MerchantRecipe(result,recipe.maxUses).apply {
            uses = recipe.uses
            priceMultiplier = recipe.priceMultiplier
            ingredients = recipe.ingredients
            setExperienceReward(recipe.hasExperienceReward())
            villagerExperience = recipe.villagerExperience
        }
    }
}