package top.iseason.bukkittemplate;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import top.iseason.bukkittemplate.dependency.DependencyDownloader;

import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class PluginDependency {

    /**
     * 解析下载plugin.yml中的依赖
     */
    public static boolean parsePluginYml() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(new InputStreamReader(requireNonNull(BukkitTemplate.class.getClassLoader().getResourceAsStream("plugin.yml"), "Jar does not contain plugin.yml")));
        ConfigurationSection libConfigs = yml.getConfigurationSection("runtime-libraries");
        if (libConfigs == null) return true;
        DependencyDownloader dd = new DependencyDownloader();
        String folder = libConfigs.getString("libraries-folder");
        if (folder != null) {
            File parent;
            if (folder.toLowerCase().startsWith("@plugin:")) {
                parent = new File(BukkitTemplate.getPlugin().getDataFolder(), folder.substring(8));
            } else {
                parent = new File(folder);
            }
            DependencyDownloader.parent = parent;
        }
        List<String> repositories = libConfigs.getStringList("repositories");
        if (!repositories.isEmpty()) {
            dd.repositories.clear();
            for (String repository : repositories) {
                dd.addRepository(repository);
            }
        }
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (String library : libConfigs.getStringList("libraries")) {
            String[] split = library.split(",");
            if (split.length == 1) {
                map.put(library, 2);
            } else if (split.length == 2) {
                map.put(split[0], Integer.parseInt(split[1]));
            }
            String substring = library.substring(0, library.lastIndexOf(":"));
            DependencyDownloader.parallel.add(substring);
        }
        dd.dependencies = map;
        DependencyDownloader.assembly.addAll(libConfigs.getStringList("assembly"));
        DependencyDownloader.exists.addAll(libConfigs.getStringList("excludes"));

        return dd.start(libConfigs.getBoolean("parallel", false));
    }
}
