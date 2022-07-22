package top.iseason.bukkit.bukkittemplate.config.annotations

/**
 * 指定配置文件路径
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FilePath(val path: String = "config.yml")