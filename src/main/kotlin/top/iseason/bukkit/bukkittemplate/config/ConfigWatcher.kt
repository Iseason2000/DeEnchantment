package top.iseason.bukkit.bukkittemplate.config

import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService

class ConfigWatcher private constructor(private val folder: File) : BukkitRunnable() {
    private var isEnable = true
    private val service: WatchService = FileSystems.getDefault().newWatchService()
        .apply { folder.toPath().register(this, StandardWatchEventKinds.ENTRY_MODIFY) }

    override fun run() {
        while (isEnable) {
            val key = try {
                service.take()
            } catch (e: Exception) {
                break
            }
            key.pollEvents().forEach {
                //监听文件修改
                if (it.kind() != StandardWatchEventKinds.ENTRY_MODIFY) return@forEach
                val path = it.context() as Path
                val absolutePath = "${folder}${File.separatorChar}$path"
                val simpleYAMLConfig = SimpleYAMLConfig.configs[absolutePath] ?: return@forEach
                if (simpleYAMLConfig.isAutoUpdate)
                    simpleYAMLConfig.load()
            }
            key.reset()
        }
    }

    override fun cancel() {
        super.cancel()
        isEnable = false
        service.close()
    }

    companion object {
        private val folders = mutableMapOf<String, ConfigWatcher>()

        fun fromFile(file: File): ConfigWatcher {
            val parentFile = file.absoluteFile.parentFile
            val folder = parentFile.toString()
            val existWatcher = folders[folder]
            if (existWatcher != null) return existWatcher
            val configWatcher = ConfigWatcher(parentFile)
            folders[folder] = configWatcher
            configWatcher.runTaskAsynchronously(TemplatePlugin.getPlugin())
            return configWatcher
        }

        fun onDisable() {
            for (v in folders.values) {
                v.cancel()
            }
            folders.clear()
        }

    }
}