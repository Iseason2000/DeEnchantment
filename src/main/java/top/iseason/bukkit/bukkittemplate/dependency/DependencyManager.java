package top.iseason.bukkit.bukkittemplate.dependency;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import top.iseason.bukkit.bukkittemplate.TemplatePlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class DependencyManager {

    /**
     * 解析下载plugin.yml中的依赖
     */
    public static void parsePluginYml() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                new InputStreamReader(
                        requireNonNull(
                                TemplatePlugin.class.getClassLoader().getResourceAsStream("plugin.yml"),
                                "Jar does not contain plugin.yml")
                )
        );
        ConfigurationSection libConfigs = yml.getConfigurationSection("runtime-libraries");
        if (libConfigs == null) return;
        DependencyDownloader dd = new DependencyDownloader();
        String folder = libConfigs.getString("libraries-folder");
        if (folder != null) {
            File parent;
            if (folder.startsWith("@Plugin:")) {
                parent = new File(TemplatePlugin.getPlugin().getDataFolder(), folder.replace("@Plugin:", ""));
            } else {
                parent = new File(".", folder);
            }
            DependencyDownloader.parent = parent;
        }
        List<String> repositories = libConfigs.getStringList("repositories");
        if (repositories != null) {
            dd.repositories.clear();
            for (String repository : repositories) {
                dd.addRepository(repository);
            }
        } else {
            dd.repositories.add("https://repo.maven.apache.org/maven2/");
        }
        List<String> libraries = libConfigs.getStringList("libraries");
        if (libraries != null) {
            dd.dependencies = libraries;
        }
        dd.setup();
    }
}
