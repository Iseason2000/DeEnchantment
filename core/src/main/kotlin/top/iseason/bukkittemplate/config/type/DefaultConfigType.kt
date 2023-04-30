package top.iseason.bukkittemplate.config.type

import java.lang.reflect.Field

internal object DefaultConfigType : ConfigType {
    /**
     * 所有配置类型
     */
    val types = mutableListOf<ConfigType>()

    //默认类型
    init {
        types.add(SetType)
        types.add(ListType)
        types.add(MapType)
//        types.add(EnumSetType)
    }

    fun matchType(clazz: Class<*>): ConfigType {
        return types.findLast { it.checkType(clazz) } ?: DefaultConfigType
    }

    override fun checkType(clazz: Class<*>): Boolean {
        return true
    }

    override fun read(obj: Any, field: Field): Any {
        return obj
    }

    override fun save(obj: Any): Any {
        return obj
    }


}