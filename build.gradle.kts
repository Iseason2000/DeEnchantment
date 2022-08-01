import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
        classpath("com.guardsquare:proguard-gradle:7.2.2")
    }
}
// 插件名称，请在gradle.properties 修改
val pluginName: String by project
//包名，请在gradle.properties 修改
val group: String by project
val groupS = group
// 作者，请在gradle.properties 修改
val author: String by project
// jar包输出路径，请在gradle.properties 修改
val jarOutputFile: String by project
//插件版本，请在gradle.properties 修改
val version: String by project
// shadowJar 版本 ，请在gradle.properties 修改
val shadowJar: ShadowJar by tasks
// exposed 数据库框架版本，请在gradle.properties 修改
val exposedVersion: String by project
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

    mavenLocal()
}

dependencies {
    //基础库
    compileOnly(kotlin("stdlib-jdk8"))
//    反射库
//    compileOnly(kotlin("reflect"))

//    协程库
//    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

//    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
//    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
//    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.willfp:EcoEnchants:8.102.0")

}


tasks {
    shadowJar {
        relocate("top.iseason.bukkit.bukkittemplate", "$groupS.lib.core")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "main" to "$groupS.lib.core.TemplatePlugin",
                "name" to pluginName,
                "version" to project.version,
                "author" to author,
                "kotlinVersion" to getProperties("kotlinVersion"),
                "exposedVersion" to exposedVersion
            )
        }
    }
}
task<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = "$groupS.lib"
    shadowJar.minimize()
}
tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

tasks.register<proguard.gradle.ProGuardTask>("buildPlugin") {
    group = "minecraft"
    verbose()
    injars(tasks.named("shadowJar"))
    //是否混淆，注销掉启用混淆
    val obfuscated = getProperties("obfuscated") == "true"
    val shrink = getProperties("shrink") == "true"
    if (!obfuscated) {
        dontobfuscate()
    }
    if (!shrink) {
        dontshrink()
    }
    optimizationpasses(5)
    dontwarn()
    val javaHome = System.getProperty("java.home")
    if (JavaVersion.current() < JavaVersion.toVersion(9)) {
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        libraryjars(
            mapOf(
                "jarfilter" to "!**.jar",
                "filter" to "!module-info.class"
            ),
            "$javaHome/jmods/java.base.jmod"
        )
    }
    val allowObf = mapOf("allowobfuscation" to true)
    libraryjars(configurations.compileClasspath.get().files)
    keep("class $groupS.lib.core.TemplatePlugin {}")
    keep("class * implements top.iseason.bukkit.deenchantment.listeners.BaseEnchant {*;}")
    keep(allowObf, "class * implements $groupS.lib.core.KotlinPlugin {*;}")
    keepclassmembers(allowObf, "class * implements org.bukkit.event.Event {*;}")
    keepclassmembers("class * extends $groupS.lib.core.config.SimpleYAMLConfig {*;}")
    keepclassmembers(allowObf, "class * implements org.bukkit.event.Listener {*;}")
    keep(allowObf, "class $groupS.lib.core.utils.MessageUtilsKt {*;}")
    keepattributes("Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod")
    keepkotlinmetadata()
    repackageclasses()
    if (obfuscated)
        outjars(File(jarOutputFile, "${project.name}-${project.version}-obfuscated.jar"))
    else
        outjars(File(jarOutputFile, "${project.name}-${project.version}.jar"))
}

fun getProperties(properties: String) = rootProject.properties[properties].toString()