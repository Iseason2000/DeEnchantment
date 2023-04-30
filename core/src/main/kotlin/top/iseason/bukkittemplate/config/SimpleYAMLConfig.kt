package top.iseason.bukkittemplate.config

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.config.type.ConfigType
import top.iseason.bukkittemplate.config.type.DefaultConfigType
import top.iseason.bukkittemplate.debug.debug
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.other.submit
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.nio.file.Files
import java.util.*


/**
 * 一个简单的支持自动重载的配置类，不建议作为数据储存用
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class SimpleYAMLConfig(
    /**
     * 默认配置路径，以.yml结尾，覆盖@FilePath
     */
    val defaultPath: String? = null,
    /**
     * 是否自动重载
     */
    var isAutoUpdate: Boolean = true,
    /**
     * 重载是否提示,如果你要自定义提示请关闭
     */
    var updateNotify: Boolean = true
) {

    /**
     * 更新时间
     */
    var updateTime = 0L

    /**
     * 配置文件路径
     */
    val configPath = getPath().apply {
        if (!exists()) {
            parentFile.mkdirs()
            createNewFile()
        }
    }

    /**
     * 配置对象,修改并不会生效，只能直接修改成员
     */
    var config: ConfigurationSection = YamlConfiguration()
        private set

    /**
     * 直接保存ConfigurationSection到文件中,而不是从属性读取
     */
    fun saveYaml() {
        (config as YamlConfiguration).save(configPath)
    }

    private val typeMap = HashMap<Field, ConfigType>()

    /**
     * 本配置类的所有项
     */
    private val keys = buildList {
        //当前类是否标注了 @Key
        val isAllKey = this@SimpleYAMLConfig.javaClass.getAnnotation(Key::class.java) != null
        var superClass: Class<*>? = this@SimpleYAMLConfig::class.java
        val list = ArrayList<Class<*>>()
        do {
            list.add(superClass!!)
            superClass = superClass.superclass
        } while (superClass != null && superClass != SimpleYAMLConfig::class.java)
        for (i in list.size - 1 downTo 0) {
            inner@ for (field in list[i].declaredFields) {
                if (Modifier.isFinal(field.modifiers)) {
                    continue@inner
                }
                val keyAnnotation = field.getAnnotation(Key::class.java)
                val key = keyAnnotation?.key?.ifEmpty { field.name.replace("__", ".").replace('_', '-') }
                    ?: if (isAllKey) field.name.replace("__", ".").replace('_', '-') else continue@inner
                val comments = LinkedList<String>()
                for (comment in field.getAnnotationsByType(Comment::class.java)) {
                    for (c in comment.value) {
                        if (c.isBlank())
                            comments.add("")
                        else if (!c.startsWith("#"))
                            comments.add("# $c")
                        else
                            comments.add(c)
                    }
                }
                typeMap[field] = DefaultConfigType.matchType(field.type)
                add(ConfigKey(key, field, if (comments.isEmpty()) null else comments))
            }
        }
    }

    init {
        if (isAutoUpdate) {
            try {
                ConfigWatcher.fromFile(configPath.absoluteFile)
            } catch (e: Exception) {
                warn("file watch Service error. Automatic updates will been closed!")
                isAutoUpdate = false
            }
        }
        configs[configPath.absolutePath] = this
    }

    /**
     * 设置重载或者保存时是否提醒
     */
    fun setUpdate(enable: Boolean) {
        isAutoUpdate = enable
    }

    /**
     * 将文件路径转化为文件系统标准
     */
    private fun normalizeFileStr(file: String) = file.replace('\\', File.separatorChar).replace('/', File.separatorChar)

    private fun getPath(): File {
        val dataFolder = BukkitTemplate.getPlugin().dataFolder
        if (defaultPath != null) return File(dataFolder, normalizeFileStr(defaultPath)).absoluteFile
        val annotation = this::class.java.getAnnotation(FilePath::class.java)
        require(annotation != null) { "path must not null" }
        return File(dataFolder, normalizeFileStr(annotation.path)).absoluteFile
    }

    /**
     * 异步保存配置
     */
    fun saveAsync(notify: Boolean = updateNotify) {
        submit(async = true) {
            save(notify)
        }
    }

    /**
     * 保存配置
     */
    fun save(notify: Boolean = updateNotify) {
        update(false)
        try {
            onSaved(config)
        } catch (_: Exception) {
        }
        if (notify)
            info("Config $configPath was saved!")
    }

    /**
     * 从文件异步加载配置
     */
    fun loadAsync(notify: Boolean = updateNotify) {
        submit(async = true) {
            load(notify)
        }
    }

    /**
     * 从文件加载配置
     */
    fun load(notify: Boolean = updateNotify) {
        if (!update(true)) {
            return
        }
        try {
            onLoaded(config)
        } catch (_: Exception) {
        }
        if (notify)
            info(notifyMessage.format(configPath.name))
    }

    /**
     * 更新配置
     * @param isReadOnly 是否只读
     * @return 更新成功返回true
     */
    private fun update(isReadOnly: Boolean): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - updateTime < 2000L) return false
        val newConfig =
            if (configPath.exists()) YamlConfiguration.loadConfiguration(configPath) else YamlConfiguration()
        var temp = YamlConfiguration()
        val commentMap = hashMapOf<String, List<String>>()
        //缺了键补上
        var incomplete = false
        var iterator = keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            //获取并设置注释
            var keyName = key.key
            val configType = typeMap[key.field]!!
            if (isReadOnly) {
                val value = newConfig.get(keyName)
                if (value != null) {
                    //获取修改的键值
                    try {
                        key.setValue(this, configType.read(value, key.field))
                    } catch (e: Exception) {
                        debug("Loading config $configPath error! key:${keyName} value: $value")
                    }
                } else {
                    //缺少键，重写入
                    if (!incomplete) {
                        incomplete = true
                        iterator = keys.iterator()
                        temp = YamlConfiguration()
                        commentMap.clear()
                        debug("completing file $configPath ")
                        continue
                    }
                }
            }
            if (!(!incomplete && isReadOnly)) {
                //处理注释
                val comments = key.comments
                if (comments != null) {
                    val random = UUID.randomUUID().toString()
                    commentMap[random] = comments
                    keyName = "${keyName}$random"
                }
            }
            //将数据写入临时配置
            try {
                val value = key.getValue(this)
                temp.set(keyName, if (value == null) null else configType.save(value))
                if (!temp.contains(keyName)) {
                    temp.createSection(keyName)
                }
            } catch (e: Exception) {
                debug("setting config $configPath error! key:${keyName}")
            }
        }
        if (!(!incomplete && isReadOnly) || !configPath.exists()) {
            //删除重复的键
            temp.getKeys(true)
                .sortedByDescending { it.count { c -> c == '.' } }
                .forEach {
                    if (it.length <= 36) return@forEach
                    val takeLast = it.takeLast(36)
                    if (!commentMap.containsKey(takeLast)) return@forEach
                    val dropLast = it.dropLast(36)
                    if (temp.isConfigurationSection(dropLast)) {
                        temp.set(it, temp.getConfigurationSection(dropLast))
                        temp.set(dropLast, null)
                    }
                }

            val saveToString = temp.saveToString()
//            println(saveToString)
            val split = saveToString.split('\n')
            //转换注释
            commentFile(split, commentMap)
        }
        config = YamlConfiguration.loadConfiguration(configPath)
        updateTime = System.currentTimeMillis()
        return true
    }

    /**
     * 配置读取完毕之后的回调
     */
    open fun onLoaded(section: ConfigurationSection) {

    }

    /**
     * 配置保存完毕之后的回调
     */
    open fun onSaved(section: ConfigurationSection) {

    }

    /**
     * 转换配置文件的注释
     */
    private fun commentFile(lines: List<String>, commentMap: Map<String, List<String>>) {
        val newFile: MutableList<String> = LinkedList()
        //逐行扫描,匹配注释并替换
        lines.forEach {
            var nextLine: String = it
            //判断注释
            val split = nextLine.split(':', ignoreCase = false, limit = 2)
            if (split.size == 2 && split[0].length > 36) {
                val uuid = split[0].takeLast(36)
                var comments = commentMap[uuid]
                //是注释
                if (comments != null) {
                    val indexOfFirst = split[0].indexOfFirst { char -> !char.isWhitespace() }
                    if (indexOfFirst > 0) {
                        val prefix = String(CharArray(indexOfFirst) { ' ' })
                        comments = comments.map { com -> "$prefix$com" }
                    }
                    newFile.addAll(comments)
//                    return@forEach
                    nextLine = nextLine.replace(uuid, "")
                }
            }
            newFile.add(nextLine)
        }
        //写入数据到文件
        Files.write(configPath.toPath(), newFile)
    }

    private fun getAllFields(): List<Field> {
        val fields = mutableListOf<Field>()
        var superClass: Class<*> = this::class.java
        while (superClass != SimpleYAMLConfig::class.java) {
            fields.addAll(0, listOf(*superClass.declaredFields))
            superClass = superClass.superclass
        }
        return fields
    }

    companion object {
        //监听器列表
        val configs = mutableMapOf<String, SimpleYAMLConfig>()

        //重载提示信息
        var notifyMessage: String = "Config %s was reloaded!"
    }
}
