package top.iseason.bukkit.bukkittemplate.config.annotations

/**
 * 指定配置键值,如果声明在类上则默认该类所有可变成员都为键
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Key(val key: String = "")
