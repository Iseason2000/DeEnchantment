package top.iseason.bukkit.bukkittemplate.utils

import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.bukkittemplate.TemplatePlugin

/**
 * 提交一个任务
 * @param delay 延迟 单位tick
 * @param period 循环周期 单位tick
 * @param async 是否异步
 * @param task 你的任务
 */
fun submit(
    delay: Long = 0,
    period: Long = 0,
    async: Boolean = false,
    task: Submitter.() -> Unit
) {
    check(delay >= 0) { "delay must grater than 0" }
    check(period >= 0) { "period must grater than 0" }
    val submitter = Submitter(delay, period, async, task)
    if (submitter.async) {
        if (submitter.period > 0) {
            submitter.runTaskTimerAsynchronously(TemplatePlugin.getPlugin(), submitter.delay, submitter.period)
        } else {
            submitter.runTaskLaterAsynchronously(TemplatePlugin.getPlugin(), submitter.delay)
        }
    } else {
        if (submitter.period > 0) {
            submitter.runTaskTimer(TemplatePlugin.getPlugin(), submitter.delay, submitter.period)
        } else {
            submitter.runTaskLater(TemplatePlugin.getPlugin(), submitter.delay)
        }
    }
}

fun submit(
    delay: Long = 0,
    period: Long = 0,
    async: Boolean = false,
    task: BukkitRunnable
) {
    check(delay >= 0) { "delay must grater than 0" }
    check(period >= 0) { "period must grater than 0" }
    if (async) {
        if (period > 0) {
            task.runTaskTimerAsynchronously(TemplatePlugin.getPlugin(), delay, period)
        } else {
            task.runTaskLaterAsynchronously(TemplatePlugin.getPlugin(), delay)
        }
    } else {
        if (period > 0) {
            task.runTaskTimer(TemplatePlugin.getPlugin(), delay, period)
        } else {
            task.runTaskLater(TemplatePlugin.getPlugin(), delay)
        }
    }
}

class Submitter(
    val delay: Long,
    val period: Long,
    val async: Boolean,
    val task: Submitter.() -> Unit
) : BukkitRunnable() {
    override fun run() {
        task()
    }
}