package top.iseason.bukkittemplate;

import org.bukkit.Bukkit;
import org.xml.sax.SAXException;
import top.iseason.bukkittemplate.dependency.XmlParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * 依赖下载器 仅支持 group:artifact:version 的格式
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DependencyDownloader {
    /**
     * 被加载到插件Classloader的依赖，默认是加载到 IsolatedClassLoader
     */
    public static final Set<String> assembly = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * 不下载重复的依赖,此为缓存
     */
    public static final Set<String> exists = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * 默认为plugin.yml中声明的依赖，覆盖子依赖中的相同依赖的不同版本
     */
    public static final Set<String> parallel = new HashSet<>();

    /**
     * 储存路径
     */
    public static File parent = new File("libraries");
    /**
     * 下载源
     */
    public List<String> repositories = new ArrayList<>();
    /**
     * 依赖 group:artifact:version to maxDepth
     * maxDepth表示最大依赖解析层数
     */
    public Map<String, Integer> dependencies = new LinkedHashMap<>();

    /**
     * 下载依赖
     * 比如 org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10
     *
     * @param dependency   依赖地址
     * @param depth        依赖深度
     * @param maxDepth     最大依赖深度
     * @param repositories maven仓库
     * @param printCache   打印缓存，可为null
     * @param isLast       是否为最后一个
     * @return true 表示加载依赖成功
     */
    public static boolean downloadDependency(String dependency,
                                             int depth,
                                             int maxDepth,
                                             List<String> repositories,
                                             List<String> printCache,
                                             boolean isLast
    ) {
        String[] split = dependency.split(":");
        //过滤非法格式
        if (split.length != 3 || checkLibraryIllegal(dependency)) {
            if (printCache != null)
                printCache.add(printTree("[F] " + dependency, depth - 1, isLast));
            else
                Bukkit.getLogger().info("[" + BukkitTemplate.getPlugin().getName() + "]" + printTree("[F] " + dependency, depth - 1, isLast));
            return true;
        }
        String groupId = split[0];
        String artifact = split[1];
        String classId = groupId + ":" + artifact;
        String version = split[2];
        //过滤重复的
        if (exists.contains(classId) || (depth > 1 && parallel.contains(classId))) {
            return true;
        }
        exists.add(classId);
        String suffix = groupId.replace(".", "/") + "/" + artifact + "/" + version + "/";
        File saveLocation = new File(parent, suffix.replace("/", File.separator));
        String jarName = artifact + "-" + version + ".jar";
        String pomName = artifact + "-" + version + ".pom";
        File jarFile = new File(saveLocation, jarName);
        File pomFile = new File(saveLocation, pomName);
        File sha = new File(jarFile + ".sha1");
        String type = "I";
        boolean success = false;
        //已经存在
        if ((sha.exists() && checkSha(jarFile, sha)) || jarFile.exists()) {
            try {
                type = addUrl(classId, jarFile.toURI().toURL());
                success = true;
            } catch (Exception e) {
                type = "E";
                e.printStackTrace();
            }
        } else {
            boolean downloaded = false;
            for (String repository : repositories) {
                try {
                    String jarStr = repository + suffix + jarName;
                    URL url = new URL(jarStr);
                    if (!downloadFile(url, jarFile)) {
                        jarFile.delete();
                        continue;
                    }
                    type = addUrl(classId, jarFile.toURI().toURL());
                    downloaded = true;
                    success = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!downloaded) {
                type = "N";
                exists.remove(classId);
            }
        }
        if (printCache != null)
            printCache.add(printTree("[" + type + "] " + dependency, depth - 1, isLast));
        else
            Bukkit.getLogger().info("[" + BukkitTemplate.getPlugin().getName() + "]" + printTree("[" + type + "] " + dependency, depth - 1, isLast));
        if (!success || depth == maxDepth) return true;

        for (String repository : repositories) {
            try {
                URL pomUrl = new URL(repository + suffix + pomName);
                if (!pomFile.exists() && !downloadFile(pomUrl, pomFile)) {
                    pomFile.delete();
                    continue;
                }
                try {
                    XmlParser xmlDependency = new XmlParser(pomFile);
                    LinkedList<String> temp = new LinkedList<>();
                    for (String subDependency : xmlDependency.getDependencies()) {
                        downloadDependency(subDependency, depth + 1, maxDepth, repositories, temp, false);
                    }
                    if (printCache != null && !temp.isEmpty()) {
                        temp.add(temp.removeLast().replace('├', '└'));
                        printCache.addAll(temp);
                    }
                    return true;
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    Bukkit.getLogger().warning("Loading file " + pomFile + " error!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String printTree(String name, int level, boolean isLast) {
        // 输出的前缀
        StringBuilder stringBuilder = new StringBuilder();
        if (level == 0) {
            return stringBuilder.append(" ").append(name).toString();
        }
        // 按层次进行缩进
        for (int i = 0; i < level; i++) {
            if (i == level - 1) {
                if (isLast) stringBuilder.append("  └──");
                else stringBuilder.append("  ├──");
            } else
                stringBuilder.append("  │  ");
        }
        stringBuilder.append("  ").append(name);
        return stringBuilder.toString();
    }

    private static String addUrl(String classId, URL url) {
        if (assembly.contains(classId)) {
            ReflectionUtil.addAssemblyURL(url);
            return "A";
        } else {
            ReflectionUtil.addIsolatedURL(url);
            return "I";
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
            return false;
        }
    }

    /**
     * 下載文件，超时5秒
     *
     * @param url  文件链接
     * @param file 保存路径
     * @return true if success
     */
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
     * 检查依赖格式是否非法
     *
     * @param libraryName
     * @return
     */
    public static boolean checkLibraryIllegal(String libraryName) {
        return XmlParser.placeHolder.matcher(libraryName).find() || libraryName.contains("[") || libraryName.contains("(");
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

    /**
     * 添加需要的依赖
     *
     * @param dependency 依赖, 将会下载依赖的子依赖(2层)
     * @return 自身
     */
    public DependencyDownloader addDependency(String dependency) {
        dependencies.put(dependency, 2);
        return this;
    }

    /**
     * 下载所有积压的依赖
     *
     * @return true if success
     */
    public boolean start(boolean parallel) {
        if (dependencies.isEmpty()) return true;
        Bukkit.getLogger().info("[" + BukkitTemplate.getPlugin().getName() + "] Loading libraries...");
        Bukkit.getLogger().info("Successful Flags: [I]=Loading Isolated [A]=Loading Assembly");
        Bukkit.getLogger().info("Failure Flags: [E]=Loading Error [N]=NetWork Error [F]=Library Format Error");
        AtomicBoolean failure = new AtomicBoolean(false);
        Stream<Map.Entry<String, Integer>> stream =
                parallel ?
                        dependencies.entrySet().parallelStream() :
                        dependencies.entrySet().stream();
        stream.forEach(entry -> {
                    if (failure.get()) return;
                    LinkedList<String> printList = new LinkedList<>();
                    if (!downloadDependency(entry.getKey(), 1, entry.getValue(), repositories, printList, false)) {
                        failure.set(true);
                    }
                    for (String s : printList) {
                        Bukkit.getLogger().info(s);
                    }
                }
        );
        return !failure.get();
    }

    /**
     * 直接下载并加载依赖
     *
     * @param dependency 依赖
     * @param maxDepth   依赖解析层数
     * @return true if success
     */
    public boolean downloadDependency(String dependency, int maxDepth) {
        return downloadDependency(dependency, 1, maxDepth, repositories, null, maxDepth == 1);
    }

    /**
     * 直接下载并加载依赖以及依赖的子依赖
     *
     * @param dependency 依赖
     * @return true if success
     */
    public boolean downloadDependency(String dependency) {
        return downloadDependency(dependency, 1, 2, repositories, null, false);
    }
}
