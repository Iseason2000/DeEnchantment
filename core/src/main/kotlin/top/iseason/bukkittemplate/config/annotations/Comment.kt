package top.iseason.bukkittemplate.config.annotations

/**
 * 添加注释,只在SimpleYAMLConfig中有效
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.FIELD)
annotation class Comment(vararg val value: String)