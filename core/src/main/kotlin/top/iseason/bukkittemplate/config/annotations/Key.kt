package top.iseason.bukkittemplate.config.annotations

/**
 * 指定配置键值,如果声明在类上则默认该类所有可变成员都为键,只在SimpleYAMLConfig中有效
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class Key(val key: String = "")
