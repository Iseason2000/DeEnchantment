import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.6.10"
}
group = "top.iseason"
version = "1.2.5"
val mainClass = "DeEnchantmentPlugin"
val author = "Iseason"
val jarOutputFile = "E:\\mc\\1.18 server\\plugins"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
//    maven {
//        url = uri("https://repo.codemc.org/repository/maven-public/")
//    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
//    implementation("io.github.bananapuncher714:nbteditor:7.18.1")

}
//
//(tasks.getByName("processResources") as ProcessResources).apply {
//    val p = "${project.group}.${rootProject.name.toLowerCase()}"
//    include("config.yml")
//    include("plugin.yml").expand(
//        "name" to rootProject.name.toLowerCase(),
//        "main" to "$p.$mainClass",
//        "version" to version,
//        "author" to author
//    )
//}
tasks {
    shadowJar {
        destinationDirectory.set(file(jarOutputFile))
        minimize()
//        relocate("io.github.bananapuncher714", "top.iseason.deenchantment.libs")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    processResources {
        filesMatching("plugin.yml") {
            val p = "${project.group}.${rootProject.name.toLowerCase()}"
            expand(
                "name" to rootProject.name.toLowerCase(),
                "main" to "$p.$mainClass",
                "version" to version,
                "author" to author
            )
        }
    }
}
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

tasks.jar {
    destinationDirectory.set(file(jarOutputFile))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}