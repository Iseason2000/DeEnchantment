package top.iseason.bukkittemplate.runtime;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 运行环境构造器
 */
public class RuntimeBuilder {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ClassLoader classLoader;
        private File storagePath = new File("libraries");
        private List<String> repositories = new LinkedList<>();
        private List<String> dependencies = new LinkedList<>();
        private final List<String> assemblyDependencies = new LinkedList<>();
        private final List<String> excludeDependencies = new LinkedList<>();
        private boolean isParallel = false;
        private Logger logger = Logger.getLogger(RuntimeManager.class.getSimpleName());

        /**
         * 设置依赖储存路径
         */
        public Builder storagePath(File storagePath) {
            this.storagePath = storagePath;
            return this;
        }

        /**
         * 设置需要 Runtime 的classloader
         */
        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        /**
         * 将当前class的ClassLoader设置为需要runtime的ClassLoader
         */
        public Builder currentClassLoader() {
            this.classLoader = this.getClass().getClassLoader();
            return this;
        }

        /**
         * 添加依赖仓库，按添加顺序下载
         */
        public Builder repository(String repository) {
            if (!repository.endsWith("/")) repository = repository + "/";
            this.repositories.add(repository);
            return this;
        }

        /**
         * 添加依赖仓库，按添加顺序下载
         */
        public Builder repositories(List<String> repositories) {
            this.repositories = repositories;
            return this;
        }

        /**
         * 添加依赖<p>
         * 格式为 group:artifact:version<p>
         * 或 group:artifact:version,depth<p>
         * 其中的 depth 表示依赖树层数，默认2
         */
        public Builder dependency(String dependency) {
            if (RuntimeManager.checkLibraryIllegal(dependency)) {
                throw new IllegalArgumentException(dependency + " was not a illegal dependency");
            }
            this.dependencies.add(dependency);
            return this;
        }

        /**
         * 添加依赖
         */
        public Builder dependencies(List<String> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        /**
         * 下载依赖的过程，此依赖将不从隔离的ClassLoader中读取，而是按照双亲委派的方式读取<p>
         * 建议将一些日志API添加进去<p>
         * 格式: group:artifact
         */
        public Builder assembly(String dependency) {
            this.assemblyDependencies.add(dependency);
            return this;
        }

        /**
         * 下载依赖时会解析依赖的依赖，可能出现一些不想要的依赖，在此声明以排除<p>
         * 建议将一些日志API添加进去<p>
         * 格式: group:artifact
         */
        public Builder exclude(String dependency) {
            this.excludeDependencies.add(dependency);
            return this;
        }

        /**
         * 设置并行下载依赖
         */
        public Builder parallel() {
            this.isParallel = true;
            return this;
        }

        /**
         * 设置 logger null 不记录日志
         */
        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * 构建运行环境并下载依赖
         *
         * @return 运行环境管理者
         */
        public RuntimeManager build() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
            if (classLoader == null) throw new IllegalArgumentException("classLoader can not be null");
            if (repositories == null) throw new IllegalArgumentException("repositories can not be null");
            if (dependencies == null) throw new IllegalArgumentException("dependencies can not be null");
            if (assemblyDependencies == null)
                throw new IllegalArgumentException("assemblyDependencies can not be null");
            if (excludeDependencies == null) throw new IllegalArgumentException("excludeDependencies can not be null");
            if (storagePath == null) storagePath = new File("libraries");
            if (repositories.isEmpty()) throw new IllegalArgumentException("classLoader can not be null");
            RuntimeManager.logger = this.logger;
            ClassAppender classAppender = new ClassAppender(classLoader.getParent());
            RuntimeManager runtimeManager = new RuntimeManager(storagePath, classAppender, repositories, dependencies, assemblyDependencies, isParallel);
            runtimeManager.addExcludes(excludeDependencies);
            runtimeManager.injectTo(classLoader);
            runtimeManager.downloadAll();
            return runtimeManager;
        }
    }
}
