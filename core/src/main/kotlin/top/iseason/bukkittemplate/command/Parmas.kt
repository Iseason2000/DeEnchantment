package top.iseason.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.potion.PotionEffectType

/**
 * 一个命令节点的参数
 */
class Params(val params: Array<String>, val node: CommandNode) {

    var readIndex = 0

    /**
     * 获取指定参数类型的参数,不存在返回null
     * @param index 参数的位置
     */
    inline fun <reified T> getOptionalParam(index: Int): T? {
        val param = params.getOrNull(index) ?: return null
        return ParamAdopter.getOptionalTypedParam<T>(T::class, param)
    }

    /**
     * 获取指定参数类型的参数
     * @param index 参数的位置
     */
    inline fun <reified T> getParam(index: Int): T {
        val param = params.getOrNull(index)
            ?: throw ParmaException("&c参数 &6${node.params.getOrNull(index)?.placeholder ?: "位置 $index"} &c不存在!")
        return ParamAdopter.getTypedParam(T::class, param)
    }

    /**
     * 获取下一个参数
     */
    inline fun <reified T> next() = getParam<T>(readIndex).also { readIndex++ }

    /**
     * 获取下一个可选参数
     */
    inline fun <reified T> nextOrNull() = getOptionalParam<T>(readIndex)?.also { readIndex++ }
    inline fun <reified T> nextOrDefault(default: T) = getOptionalParam<T>(readIndex)?.also { readIndex++ } ?: default

    /**
     * 判断是否存在某个参数
     */
    fun hasParma(str: String, ignoreCase: Boolean = true) = params.any { it.equals(str, ignoreCase) }
}

/**
 * 命令参数
 */
open class Param(
    /**
     * 占位符
     */
    val placeholder: String,
    /**
     * 参数建议，存在运行时建议时不会使用
     */
    var suggest: Collection<String>? = null,
    /**
     * 参数建议，运行时生成的建议，优先级高于 suggest
     */
    var suggestRuntime: RuntimeSuggestParams? = null
) {
    fun interface RuntimeSuggestParams {
        fun getParams(sender: CommandSender): Collection<String>
    }
}

/**
 * 参数建议缓存，避免无所谓的内存消耗
 */
object ParamSuggestCache {
    /**
     * 建议在线玩家名称
     */
    val playerParam: Param.RuntimeSuggestParams =
        Param.RuntimeSuggestParams { Bukkit.getOnlinePlayers().map { p -> p.name } }

    /**
     * 建议药水效果名
     */
    val potionTypes = PotionEffectType.values().filterNotNull().map {
        it.name.lowercase()
    }

    /**
     * 建议物品材质名
     */
    val materialTypes = Material.values().map {
        it.name.lowercase()
    }


}

