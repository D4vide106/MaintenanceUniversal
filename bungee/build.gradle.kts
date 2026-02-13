plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // BungeeCord API
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2")
    
    // Common module
    implementation(project(":common"))
    
    // Configurate (YAML)
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    
    // Jedis (Redis)
    implementation("redis.clients:jedis:5.1.0")
    
    // Adventure API for BungeeCord
    implementation("net.kyori:adventure-platform-bungeecord:4.3.2")
}

tasks {
    shadowJar {
        archiveBaseName.set("MaintenanceUniversal-BungeeCord")
        archiveClassifier.set("")
        
        // Relocate dependencies
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.lib.configurate")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.lib.jedis")
        relocate("net.kyori", "me.d4vide106.maintenance.lib.kyori")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.lib.hikari")
        
        dependencies {
            include(project(":common"))
        }
        
        minimize()
    }
    
    build {
        dependsOn(shadowJar)
    }
}
