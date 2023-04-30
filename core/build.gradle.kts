plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.7.20"
}

group = "top.iseason.bukkittemplate"

val exposedVersion: String by rootProject
repositories {

}
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("net.kyori:adventure-text-minimessage:4.13.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.8.10")
}
tasks {
    compileJava {
        options.isFailOnError = false
        options.isWarnings = false
        options.isVerbose = false
    }
    build {
        dependsOn(named("shadowJar"))
    }
    dokkaHtml.configure {
        dokkaSourceSets {
            named("main") {
                moduleName.set("BukkitTemplate")
            }
        }
    }
}
