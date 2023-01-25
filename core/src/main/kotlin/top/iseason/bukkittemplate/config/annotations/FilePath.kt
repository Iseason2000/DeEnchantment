package top.iseason.bukkittemplate.config.annotations

/**
 * 指定配置文件路径,只在SimpleYAMLConfig中有效
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class FilePath(val path: String = "config.yml")