package top.iseason.bukkittemplate;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import top.iseason.bukkittemplate.runtime.ClassAppender;
import top.iseason.bukkittemplate.runtime.RuntimeManager;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginYmlRuntime {

    /**
     * 解析下载plugin.yml中的依赖
     */
    public static RuntimeManager parsePluginYml() throws RuntimeException {

        YamlConfiguration yml = null;
        // 为什么不用 classloader 的 getResource呢，因为某些sb系统或者服务端会乱改
        // 导致 getResource 的内容错误, 已测试 Debian + CatServer
        String location = PluginYmlRuntime.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try (JarFile jarFile = new JarFile(URLDecoder.decode(location, "UTF-8"), false)) {
            JarEntry entry = jarFile.getJarEntry("plugin.yml");
            InputStream resource = jarFile.getInputStream(entry);
            yml = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (yml == null) return null;
        ConfigurationSection libConfigs = yml.getConfigurationSection("runtime-libraries");
        if (libConfigs == null) return null;
        String folder = libConfigs.getString("libraries-folder");
        File parent = new File("libraries");
        if (folder != null) {
            if (folder.toLowerCase().startsWith("@plugin:")) {
                parent = new File(BukkitTemplate.getPlugin().getDataFolder(), folder.substring(8));
            } else {
                parent = new File(folder);
            }
        }
        List<String> repositories = libConfigs.getStringList("repositories");
        if (repositories.isEmpty()) {
            repositories.add("https://repo.maven.apache.org/maven2/");
        }
        List<String> libraries = libConfigs.getStringList("libraries");
        List<String> assemblyList = libConfigs.getStringList("assembly");
        List<String> excludes = libConfigs.getStringList("excludes");
        ClassAppender classAppender = new ClassAppender(PluginYmlRuntime.class.getClassLoader().getParent());
        RuntimeManager runtimeManager = new RuntimeManager(parent, classAppender, repositories, libraries, assemblyList, libConfigs.getBoolean("parallel"));
        runtimeManager.addExcludes(excludes);
        try {
            runtimeManager.injectTo(PluginYmlRuntime.class.getClassLoader());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("运行环境注入失败!");
        }
        RuntimeManager.logger = BukkitTemplate.getPlugin().getLogger();
        runtimeManager.downloadAll();
        return runtimeManager;
    }
}
