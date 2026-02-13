plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val minecraftVersion = "1.20.4"
val fabricLoaderVersion = "0.15.6"
val fabricApiVersion = "0.96.4+1.20.4"

repositories {
    maven("https://maven.fabricmc.net/")
}

dependencies {
    // Minecraft and Fabric
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${minecraftVersion}+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
    
    // Common module
    implementation(project(":common"))
    
    // Configurate (YAML)
    include(implementation("org.spongepowered:configurate-yaml:4.1.2")!!)
    
    // Jedis (Redis)
    include(implementation("redis.clients:jedis:5.1.0")!!)
    
    // Include common module
    include(project(":common"))
}

tasks {
    processResources {
        inputs.property("version", project.version)
        
        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
    
    withType<JavaCompile> {
        options.release.set(17)
    }
    
    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
    }
    
    shadowJar {
        archiveBaseName.set("MaintenanceUniversal-Fabric")
        archiveClassifier.set("")
        configurations = listOf(project.configurations.shadow.get())
    }
    
    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveBaseName.set("MaintenanceUniversal-Fabric")
    }
}
