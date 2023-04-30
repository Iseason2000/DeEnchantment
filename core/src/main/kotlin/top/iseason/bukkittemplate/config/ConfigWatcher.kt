package top.iseason.bukkittemplate.config

import com.sun.nio.file.SensitivityWatchEventModifier
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.DisableHook
import java.io.File
import java.io.IOException
import java.nio.file.*

/**
 * 配置文件监听器，负责实现配置修改之后的自动重载
 */
class ConfigWatcher private constructor(private val folder: Path) : BukkitRunnable() {

    private val service: WatchService = run {
        val path = if (Files.isSymbolicLink(folder)) {
            Files.readSymbolicLink(folder)
        } else folder
        val newWatchService = path.fileSystem.newWatchService()
        try {
            path.register(
                newWatchService,
                arrayOf(StandardWatchEventKinds.ENTRY_MODIFY),
                SensitivityWatchEventModifier.HIGH,
            )
        } catch (_: Exception) {
            //报错说明被注册过了，不再注册
        }
        newWatchService
    }

    /**
     * 自动重载的实现方法
     */
    override fun run() {
        val key = try {
            service.poll() ?: return
        } catch (e: Exception) {
            cancel()
            return
        }
        //监听文件修改
        for (pollEvent in key.pollEvents()) {
            if (pollEvent.kind() != StandardWatchEventKinds.ENTRY_MODIFY) continue
            val path = pollEvent.context() as Path
            if (!path.toString().endsWith(".yml")) break
            val absolutePath = "${folder}${File.separatorChar}$path"
            val simpleYAMLConfig = SimpleYAMLConfig.configs[absolutePath] ?: continue
            if (simpleYAMLConfig.isAutoUpdate) {
                simpleYAMLConfig.load()
                break
            }
        }
        key.reset()
    }

    /**
     * 取消本监听器
     */
    override fun cancel() {
        super.cancel()
        service.close()
    }

    companion object {
        private val folders = hashMapOf<String, ConfigWatcher>()

        init {
            DisableHook.addTask {
                onDisable()
            }
        }

        /**
         * 监听某个文件夹，如果存在则返回该监听器
         */
        @Throws(IOException::class)
        fun fromFile(file: File): ConfigWatcher {
            val parentFile = file.absoluteFile.parentFile
            val folder = parentFile.toString()
            val existWatcher = folders[folder]
            if (existWatcher != null) return existWatcher
            val configWatcher = ConfigWatcher(parentFile.toPath())
            folders[folder] = configWatcher
            configWatcher.runTaskTimerAsynchronously(BukkitTemplate.getPlugin(), 20L + (folders.size % 5) * 4, 20L)
            return configWatcher
        }

        private fun onDisable() {
            for (v in folders.values) {
                try {
                    v.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            folders.clear()
        }
    }
}