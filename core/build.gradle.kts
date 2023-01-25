plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.7.10"
}

group = "top.iseason.bukkittemplate"

val exposedVersion: String by rootProject
repositories {
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}
dependencies {

    implementation("io.github.bananapuncher714:nbteditor:7.18.3")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
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