plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val velocityVersion: String by project

dependencies {
    // Common module
    implementation(project(":common"))
    
    // Velocity API
    compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
    annotationProcessor("com.velocitypowered:velocity-api:$velocityVersion")
    
    // bStats for Velocity
    implementation("org.bstats:bstats-velocity:3.0.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("MaintenanceUniversal-Velocity-${project.version}.jar")
        
        // Relocate dependencies
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.libs.configurate")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.libs.hikari")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.libs.jedis")
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