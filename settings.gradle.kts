val pluginName: String by settings
rootProject.name = pluginName

pluginManagement {
    //kotlin 版本
    val kotlinVersion: String by settings
    //shadowJar 版本
    val shadowJarVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        id("com.github.johnrengelman.shadow") version shadowJarVersion
    }
}
include("core", "plugin")
