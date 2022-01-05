plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("jvm") version "1.5.31"
}
group = "top.iseason.kotlin"
version = "1.1.4"
val mainClass = "DeEnchantmentPlugin"
val author = "Iseason"
val jarOutputFile = "E:\\mc\\1.18 server\\plugins"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
}

dependencies {
    api("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("io.github.bananapuncher714:nbteditor:7.18.0")

}

(tasks.getByName("processResources") as ProcessResources).apply {
    val p = "${project.group}.${rootProject.name.toLowerCase()}"
    include("config.yml")
    include("plugin.yml").expand(
        "name" to rootProject.name.toLowerCase(),
        "main" to "$p.$mainClass",
        "version" to project.version,
        "author" to author
    )
}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    minimize()
    dependencies {
        include(dependency("io.github.bananapuncher714:nbteditor:7.18.0"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.5.31"))
    }
    destinationDirectory.set(file(jarOutputFile))
}
val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
tasks.jar {
    destinationDirectory.set(file(jarOutputFile))
}
