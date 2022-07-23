package top.iseason.bukkit.bukkittemplate.dependency;

import org.bukkit.Bukkit;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * 依赖下载器
 */
public class DependencyDownloader {

    public static File parent = new File(".", "libraries");
    public List<String> repositories = new ArrayList<>();
    public List<String> dependencies = new ArrayList<>();
    public static Set<String> exists = new HashSet<>();

    /**
     * 下载依赖
     *
     * @param dependency   依赖地址
     * @param recursiveSub 是否下载子依赖
     */
    public static void downloadDependency(String dependency, boolean recursiveSub, List<String> repositories) {
        String[] split = dependency.split(":");
        if (split.length != 3) {
            Bukkit.getLogger().warning("invalid dependency " + dependency);
            return;
        }
        String groupId = split[0];
        String artifact = split[1];
        String classId = groupId + "." + artifact;
        if (exists.contains(classId)) return;
        exists.add(classId);
        String version = split[2];
        String suffix = groupId.replace(".", "/") + "/" + artifact + "/" + version + "/";
        File saveLocation = new File(parent, suffix.replace("/", File.separator));
        String jarName = artifact + "-" + version + ".jar";
        String pomName = artifact + "-" + version + ".pom";
        File jarFile = new File(saveLocation, jarName);
        File pomFile = new File(saveLocation, pomName);
        //已经存在
        if (jarFile.exists()) {
            try {
                ClassInjector.addURL(jarFile.toURI().toURL());
            } catch (Exception ignored) {
            }
        } else {
            for (String repository : repositories) {
                try {
                    String jarStr = repository + suffix + jarName;
                    URL url = new URL(jarStr);
                    if (!downloadFile(url, jarFile)) {
                        jarFile.delete();
                        continue;
                    }
                    ClassInjector.addURL(jarFile.toURI().toURL());
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (String repository : repositories) {
            try {
                URL pomUrl = new URL(repository + suffix + pomName);
                if (!pomFile.exists() && !downloadFile(pomUrl, pomFile)) {
                    pomFile.delete();
                    continue;
                }
                for (String subDependency : new XmlDependency(pomFile).getDependency()) {
                    if (recursiveSub)
                        downloadDependency(subDependency, false, repositories);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载文件并校验
     *
     * @param url  下载地址
     * @param file 保存目录
     * @return 是否下载并校验成功
     */
    public static boolean downloadFile(URL url, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        if (!download(url, file)) {
            return false;
        }
        //下载sha文件
        try {
            File sha = new File(file + ".sha1");
            URL shaUrl = new URL(url + ".sha1");
            sha.createNewFile();
            if (!download(shaUrl, sha)) return false;
            return checkSha(file, sha);
        } catch (IOException e) {
            return true;
        }
    }

    private static boolean download(URL url, File file) {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) return false;
        } catch (Exception e) {
            return false;
        }
        if (url.toString().endsWith(".jar"))
            Bukkit.getLogger().info("Downloading " + url);
        try (InputStream is = connection.getInputStream()) {
            Files.copy(is, file.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件的sha1值。
     */
    static String sha1(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[1024 * 1024 * 10];
            int len;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            StringBuilder sha1 = new StringBuilder(new BigInteger(1, digest.digest()).toString(16));
            int length = 40 - sha1.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    sha1.insert(0, "0");
                }
            }
            return sha1.toString();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 校验文件sha值
     *
     * @param file 待校验的文件
     * @param sha  sha文件
     * @return true 如果通过的话，反之false
     */
    static boolean checkSha(File file, File sha) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(sha));
            String shaStr = buffer.readLine();
            buffer.close();
            String s = sha1(file);
            return Objects.equals(s, shaStr);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 添加仓库地址
     *
     * @param repository 仓库地址，请以 "/" 结尾
     * @return 自身
     */
    public DependencyDownloader addRepository(String repository) {
        String temp = repository;
        if (!repository.endsWith("/")) temp = repository + "/";
        repositories.add(temp);
        return this;
    }

    public DependencyDownloader addDependency(String dependency) {
        dependencies.add(dependency);
        return this;
    }

    public void setup() {
        for (String dependency : dependencies) {
            downloadDependency(dependency, true, repositories);
        }
    }

    public void downloadDependency(String dependency) {
        downloadDependency(dependency, true, repositories);
    }
}
