package top.iseason.bukkittemplate.utils.other

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import top.iseason.bukkittemplate.BukkitTemplate
import java.util.concurrent.CompletableFuture

/**
 * 提交一个 bukkit runnable任务
 * @param delay 延迟 单位tick
 * @param period 循环周期 单位tick
 * @param async 是否异步
 * @param task 你的任务
 */
fun submit(
    delay: Long = 0,
    period: Long = 0,
    async: Boolean = false,
    task: Runnable
): BukkitTask {
    check(delay >= 0) { "delay must grater than 0" }
    check(period >= 0) { "period must grater than 0" }
    return if (async) {
        if (period > 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(BukkitTemplate.getPlugin(), task, delay, period)
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitTemplate.getPlugin(), task, delay)
        }
    } else {
        if (period > 0) {
            Bukkit.getScheduler().runTaskTimer(BukkitTemplate.getPlugin(), task, delay, period)
        } else {
            Bukkit.getScheduler().runTaskLater(BukkitTemplate.getPlugin(), task, delay)
        }
    }
}

/**
 * 在主线程运行任务(使用BukkitRunnable)
 */
fun runSync(task: Runnable) = Bukkit.getScheduler().runTask(BukkitTemplate.getPlugin(), task)

/**
 * 在异步线程运行任务
 */
fun runAsync(task: Runnable) {
    CompletableFuture.runAsync(task)
}