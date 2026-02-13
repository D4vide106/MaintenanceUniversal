plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Include ALL platform modules
    implementation(project(":common"))
    implementation(project(":paper"))
    implementation(project(":velocity"))
    implementation(project(":bungee"))
    
    // All platform APIs as compileOnly
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2")
}

tasks {
    shadowJar {
        archiveBaseName.set("MaintenanceUniversal-Universal")
        archiveClassifier.set("")
        
        // Include all platform implementations
        dependencies {
            include(project(":common"))
            include(project(":paper"))
            include(project(":velocity"))
            include(project(":bungee"))
        }
        
        // Relocate shared dependencies
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.lib.configurate")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.lib.jedis")
        relocate("net.kyori", "me.d4vide106.maintenance.lib.kyori")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.lib.hikari")
        
        // Keep platform classes separate
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        
        manifest {
            attributes["Multi-Release"] = "true"
        }
        
        minimize()
    }
    
    build {
        dependsOn(shadowJar)
    }
}
