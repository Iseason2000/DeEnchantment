package top.iseason.bukkittemplate.command

/**
 * 参数异常，用于传递消息
 */
class ParmaException(val arg: String, val paramAdopter: ParamAdopter<*>? = null) : Exception(arg)