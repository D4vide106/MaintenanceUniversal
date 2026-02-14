plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
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
    
    // Common module - implementation + include to bundle
    implementation(project(":common"))
    include(project(":common"))
    
    // Configurate (YAML) - include to bundle
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    include("org.spongepowered:configurate-yaml:4.1.2")
    
    // Jedis (Redis) - include to bundle
    implementation("redis.clients:jedis:5.1.0")
    include("redis.clients:jedis:5.1.0")
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
    
    remapJar {
        archiveBaseName.set("MaintenanceUniversal-Fabric")
    }
}
