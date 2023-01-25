plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    // 依赖core模块
    api(project(":core"))
//    反射库
//    compileOnly(kotlin("reflect"))

//    协程库
//    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    // 本地依赖放在libs文件夹内
    compileOnly(fileTree("libs") { include("*.jar") })
    compileOnly("com.willfp:eco:6.48.0")
    compileOnly("com.willfp:libreforge:3.127.2")
    compileOnly("com.willfp:EcoEnchants:9.15.3")
    compileOnly("com.github.Slimefun:Slimefun4:RC-32")
    compileOnly("io.github.bananapuncher714:nbteditor:7.18.4")
    compileOnly("io.github.baked-libs:dough-api:1.2.0")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}

// 插件名称，请在gradle.properties 修改
val pluginName: String by rootProject
//包名，请在gradle.properties 修改
val group: String by rootProject
val groupS = group
// 作者，请在gradle.properties 修改
val author: String by rootProject
// jar包输出路径，请在gradle.properties 修改
val jarOutputFile: String by rootProject
//插件版本，请在gradle.properties 修改
val version: String by rootProject
// shadowJar 版本 ，请在gradle.properties 修改
val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
// exposed 数据库框架版本，请在gradle.properties 修改

val exposedVersion: String by rootProject
val obfuscated: String by rootProject
val isObfuscated = obfuscated == "true"
val shrink: String by rootProject
val defaultFile = File("../build", "${rootProject.name}-${rootProject.version}.jar")
val output =
    if (isObfuscated)
        File(jarOutputFile, "${rootProject.name}-${rootProject.version}-obfuscated.jar")
    else
        File(jarOutputFile, "${rootProject.name}-${rootProject.version}.jar")
tasks {
    shadowJar {
        if (isObfuscated) {
            relocate("top.iseason.bukkittemplate.BukkitTemplate", "a")
        }
        relocate("top.iseason.bukkittemplate", "$groupS.libs.core")
        relocate("org.bstats", "$groupS.libs.bstats")
        relocate("io.github.bananapuncher714.nbteditor", "$groupS.libs.nbteditor")
    }
    build {
        dependsOn("buildPlugin")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    processResources {
        filesMatching("plugin.yml") {
            // 删除注释,你可以返回null以删除整行，但是IDEA有bug会报错，故而返回了""
            filter {
                if (it.trim().startsWith("#")) "" else it
            }
            expand(
                "main" to if (isObfuscated) "a" else "$groupS.libs.core.BukkitTemplate",
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
    prefix = "$groupS.libs"
    shadowJar.minimize()
}
tasks.shadowJar.get().dependsOn(tasks.getByName("relocateShadowJar"))

tasks.register<proguard.gradle.ProGuardTask>("buildPlugin") {
    group = "minecraft"
    verbose()
    injars(tasks.named("shadowJar"))
    if (!isObfuscated) {
        dontobfuscate()
    }
    if (shrink != "true") {
        dontshrink()
    }
    optimizationpasses(5)
    dontwarn()
//    dontpreverify()
    //添加运行环境
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
    libraryjars(configurations.compileClasspath.get().files)
    //启用混淆的选项
    val allowObf = mapOf("allowobfuscation" to true)
    //class规则
    if (isObfuscated) keep(allowObf, "class a {}")
    else keep("class $groupS.libs.core.BukkitTemplate {}")
    keep("class kotlin.Metadata {}")
    keep(allowObf, "class * implements $groupS.libs.core.KotlinPlugin {*;}")
    keep(allowObf, "class * extends top.iseason.bukkit.deenchantment.listeners.BaseEnchant {*;}")
    keepclassmembers("class * extends $groupS.libs.core.config.SimpleYAMLConfig {*;}")
    keepclassmembers("class * implements $groupS.libs.core.ui.container.BaseUI {*;}")
    keepclassmembers(allowObf, "class * implements org.bukkit.event.Listener {*;}")
    keepclassmembers(allowObf, "class $groupS.libs.bstats {}")
    keepclassmembers(allowObf, "class * extends org.bukkit.event.Event {*;}")
    keepclassmembers(allowObf, "class * extends org.jetbrains.exposed.dao.id.IdTable {*;}")
    keepclassmembers(allowObf, "class * extends org.jetbrains.exposed.dao.Entity {*;}")
    keepattributes("Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*")
//    keepkotlinmetadata()
    repackageclasses()
    outjars(defaultFile)
}
fun getProperties(properties: String) = rootProject.properties[properties].toString()
