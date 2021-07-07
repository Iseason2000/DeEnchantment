plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"
//    kotlin("jvm") version "1.0.0"
}
group = "top.iseason.kotlin"
version = "1.0.2"
val mainClass = "DeEnchantmentPlugin"
val author = "Iseason"
val jarOutputFile = "E:\\mc\\testserver1.16.5\\plugins"

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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31")
    implementation("io.github.bananapuncher714:nbteditor:7.16.1")
}

(tasks.getByName("processResources") as ProcessResources).apply {
    val p = "${project.group}.${rootProject.name.toLowerCase()}"
    include("config.yml")
    include("plugin.yml").expand(
        "name" to rootProject.name.apply { this.toLowerCase() },
        "main" to "$p.$mainClass",
        "version" to project.version,
        "author" to author
    )
}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependencies {
        include(dependency("io.github.bananapuncher714:nbteditor:7.16.1"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.4.31"))
    }
    destinationDirectory.set(file(jarOutputFile))
}
val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8
tasks.jar {
    this.destinationDirectory.set(file(jarOutputFile))
}
