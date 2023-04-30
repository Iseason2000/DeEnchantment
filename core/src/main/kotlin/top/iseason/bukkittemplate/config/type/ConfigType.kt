package top.iseason.bukkittemplate.config.type

import java.lang.reflect.Field

/**
 * SimpleYAMLConfig 的对象转换器
 */
interface ConfigType {

    /**
     * 该对象是否符合该类型
     * @param clazz Field 的类型
     */
    fun checkType(clazz: Class<*>): Boolean

    /**
     * 从config对象中读取，返回值将写入Field中
     * @param obj config.get 返回值
     * @param clazz Field 的类型
     */
    fun read(obj: Any, field: Field): Any

    /**
     *  保存到配置中,返回值将直接调用 config.set 写入配置中J
     *  @param obj Field 的值
     */
    fun save(obj: Any): Any
}