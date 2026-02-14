plugins {
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val minecraftVersion = "1.20.4"
val forgeVersion = "49.0.31"

minecraft {
    mappings("official", minecraftVersion)
}

repositories {
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    // Forge/NeoForge
    minecraft("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    
    // Common module
    implementation(project(":common"))
    
    // Configurate (YAML) - use Maven version range
    jarJar(implementation("org.spongepowered:configurate-yaml:[4.1.2,)")!!) {
        jarJar.ranged(this, "[4.1.2,)")
    }
    
    // Jedis (Redis) - use Maven version range
    jarJar(implementation("redis.clients:jedis:[5.1.0,)")!!) {
        jarJar.ranged(this, "[5.1.0,)")
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("MaintenanceUniversal-Forge")
        archiveClassifier.set("")
        
        dependencies {
            include(project(":common"))
        }
        
        // Relocate dependencies
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.lib.configurate")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.lib.jedis")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.lib.hikari")
        
        minimize()
    }
    
    build {
        dependsOn(shadowJar)
    }
    
    jar {
        finalizedBy("reobfJar")
    }
}
