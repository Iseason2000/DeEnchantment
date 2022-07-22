package top.iseason.bukkit.bukkittemplate.command

/**
 * 参数异常，用于传递消息
 */
class ParmaException(val arg: String, val typeParam: TypeParam<*>? = null) : Exception(arg)