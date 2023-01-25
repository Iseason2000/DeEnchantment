package top.iseason.bukkittemplate.utils.other

import top.iseason.bukkittemplate.debug.warn

/**
 * 性能测试工具
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object LagCatcher {

    private val startTimesMap = mutableMapOf<String, Long>()

    /**
     *  对任务进行性能测试，输出任务耗时
     *
     *  @param name 任务名称
     *  @param cycles 循环任务次数
     *  @param task 任务
     */
    fun performanceTest(name: String, cycles: Int, task: () -> Unit) {
        start(name)
        for (i in 0 until cycles) {
            task()
        }
        end(name)
    }


    /**
     *  检测该任务是否超过 checkValue 毫秒, 超过就输出警告信息
     *
     *  @param name 任务名称
     *  @param checkValue 检测值
     *  @param task 任务
     */
    fun performanceCheck(name: String, checkValue: Int = 10, task: () -> Unit) {
        start(name)

        task()

        end(name, checkValue)
    }

    private fun start(name: String) {
        startTimesMap[name] = System.currentTimeMillis()
    }

    private fun end(name: String, checkValue: Int = 0) {
        val duration = System.currentTimeMillis() - (startTimesMap[name] ?: error("$name 未开启性能监控"))
        startTimesMap.remove(name)

        if (duration >= checkValue) {
            warn("任务『$name』耗时 $duration ms")
        }
    }
}