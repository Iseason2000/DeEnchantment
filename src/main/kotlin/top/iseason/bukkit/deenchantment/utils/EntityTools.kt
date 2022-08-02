package top.iseason.bukkit.deenchantment.utils

import org.bukkit.entity.*

object EntityTools {
    //亡灵生物
    fun isUndead(entity: LivingEntity): Boolean {
        return when (entity) {
            is Zombie,
            is Skeleton,
            is Zoglin,
            is Phantom,
            is Wither,
            is SkeletonHorse,
            is ZombieHorse -> true

            else -> false
        }
    }

    //截肢生物
    fun isArthropods(entity: Entity): Boolean {
        return when (entity) {
            is Spider,
            is Silverfish,
            is Zoglin,
            is Endermite,
            is Bee -> true

            else -> false
        }
    }

}