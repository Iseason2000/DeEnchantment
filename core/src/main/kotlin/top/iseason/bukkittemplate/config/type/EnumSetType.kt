package top.iseason.bukkittemplate.config.type

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * EnumSet类型
 */
object EnumSetType : ConfigType {
    override fun checkType(clazz: Class<*>): Boolean {
        return EnumSet::class.java.isAssignableFrom(clazz)
    }

    override fun read(obj: Any, field: Field): Any {
        val clazz = field.type
        if (EnumSet::class.java.isAssignableFrom(clazz)) {
            val enumClass = getEnumSetClass(field)
            val noneOf = EnumSet.noneOf(enumClass)
            if (enumClass != null) {
                val mapNotNull = (obj as Collection<*>).mapNotNull {
                    try {
                        java.lang.Enum.valueOf(
                            enumClass, it.toString()
                                .trim()
                                .uppercase()
                                .replace(' ', '_')
                                .replace('-', '_')
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                noneOf.addAll(mapNotNull as Collection<Nothing>)
                return noneOf
            }
        }
        return obj
    }

    override fun save(obj: Any): Any {
        return (obj as EnumSet<*>).map { it.name }
    }

    private fun getEnumSetClass(field: Field): Class<out Enum<*>>? {
        val genericType = field.genericType
        if (genericType is ParameterizedType) {
            val actualTypeArguments = genericType.actualTypeArguments
            if (actualTypeArguments.isNotEmpty() && actualTypeArguments[0] is Class<*>) {
                val enumClass = actualTypeArguments[0] as? Class<out Enum<*>>
                if (enumClass != null && enumClass.isEnum) {
                    return enumClass
                }
            }
        }
        return null
    }
}