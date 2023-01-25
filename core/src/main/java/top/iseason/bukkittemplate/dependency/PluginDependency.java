package top.iseason.bukkittemplate.dependency;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import top.iseason.bukkittemplate.BukkitTemplate;

import java.io.File;
import java.io.InputStreamReader;
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
                parent = new File(".", folder);
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
        dd.dependencies = libConfigs.getStringList("libraries");
        return dd.setup();
    }
}
