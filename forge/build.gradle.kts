plugins {
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
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
    
    // Common module - regular implementation
    implementation(project(":common"))
    
    // Configurate (YAML) - use jarJar with version range
    jarJar(implementation("org.spongepowered:configurate-yaml:[4.1.2,)")!!) {
        jarJar.ranged(this, "[4.1.2,)")
    }
    
    // Jedis (Redis) - use jarJar with version range
    jarJar(implementation("redis.clients:jedis:[5.1.0,)")!!) {
        jarJar.ranged(this, "[5.1.0,)")
    }
}

tasks {
    jar {
        archiveBaseName.set("MaintenanceUniversal-Forge")
        
        // Declare explicit dependency on common:jar
        dependsOn(project(":common").tasks.named("jar"))
        
        // Include common module classes in jar
        from(project(":common").tasks.named("jar").get().outputs.files.map { zipTree(it) })
        
        // Exclude duplicate META-INF files
        exclude("META-INF/versions/**")
        
        finalizedBy("reobfJar")
    }
    
    // Make sure jarJar task output is used in build
    named("build") {
        dependsOn("jarJar")
    }
}
