plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
        classpath("com.guardsquare:proguard-gradle:7.3.1")
    }
}
subprojects {
    group = rootProject.group
    version = rootProject.version
    apply {
        plugin<com.github.jengelman.gradle.plugins.shadow.ShadowPlugin>()
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
    }
    repositories {
//    阿里的服务器速度快一点
        maven {
            name = "aliyun"
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        google()
        mavenCentral()
        maven {
            name = "spigot"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/")
        }
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "CodeMC"
            url = uri("https://repo.codemc.org/repository/maven-public")
        }
        maven {
            name = "PlaceHolderAPI"
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }
        mavenLocal()
    }

    dependencies {
        //基础库
        compileOnly(kotlin("stdlib-jdk8"))
        // 数据库
        val exposedVersion: String by rootProject
        compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
        compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
        compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
        compileOnly("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
        compileOnly("com.zaxxer:HikariCP:4.0.3")
        compileOnly("me.clip:placeholderapi:2.11.2")
        implementation("io.github.bananapuncher714:nbteditor:7.18.4")

    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
        }
    }
}

repositories {
//    阿里的服务器速度快一点
    maven {
        name = "aliyun"
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    google()
    mavenCentral()
}
dependencies {
    //基础库
    compileOnly(kotlin("stdlib-jdk8"))
}