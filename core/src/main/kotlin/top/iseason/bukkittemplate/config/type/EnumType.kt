package top.iseason.bukkittemplate.config.type

import java.lang.reflect.Field

/**
 * 枚举类型
 */
object EnumType : ConfigType {
    override fun checkType(clazz: Class<*>): Boolean {
        return Enum::class.java.isAssignableFrom(clazz)
    }

    override fun read(obj: Any, field: Field): Any {
        return try {
            java.lang.Enum.valueOf(
                field.type as Class<out Enum<*>>,
                obj.toString().trim().uppercase()
                    .replace(' ', '_')
                    .replace('-', '_')
            )
        } catch (e: Exception) {
            obj
        }
    }

    override fun save(obj: Any): Any {
        return obj.toString()
    }
}