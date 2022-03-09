package top.iseason.deenchantment.utils

import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.util.jar.JarFile

class ClassGetter(plugin: JavaPlugin, packageName: String) {
    val classes: ArrayList<Class<*>> = ArrayList()

    init {
        val src = plugin.javaClass.protectionDomain.codeSource
        if (src != null) {
            val resource = src.location
            processJarfile(resource, packageName)
        }
    }

    private fun processJarfile(resource: URL, packageName: String) {
        val relPath = packageName.replace('.', '/')
        val resPath = resource.path.replace("%20", " ").replaceFirst("[.]jar[!].*".toRegex(), ".jar")
            .replaceFirst("file:".toRegex(), "")
        val jarPath = URLDecoder.decode(resPath, "utf-8")
        try {
            val entries = JarFile(jarPath).entries()
            while (entries.hasMoreElements()) {
                val entryName = entries.nextElement().name
                var className: String? = null
                if (entryName.endsWith(".class") && entryName.startsWith(relPath)
                    && entryName.length > relPath.length + "/".length
                ) {
                    className = entryName.replace('/', '.')
                        .replace('\\', '.').replace(".class", "")
                }
                if (className != null) {
                    val c = Class.forName(className)
                    classes.add(c)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception("类名获取异常!")
        }
    }
}
