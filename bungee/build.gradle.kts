plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Common module
    implementation(project(":common"))
    
    // BungeeCord API
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2")
    
    // bStats
    implementation("org.bstats:bstats-bungeecord:3.0.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("MaintenanceUniversal-BungeeCord")
        
        // Relocate dependencies
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.libs.configurate")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.libs.hikari")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.libs.jedis")
        relocate("net.kyori", "me.d4vide106.maintenance.libs.kyori")
        relocate("com.google.gson", "me.d4vide106.maintenance.libs.gson")
        relocate("org.bstats", "me.d4vide106.maintenance.libs.bstats")
        
        minimize()
        
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
    
    build {
        dependsOn(shadowJar)
    }
}
