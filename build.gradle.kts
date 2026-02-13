plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    group = "me.d4vide106"
    version = "1.0.0"
    
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

subprojects {
    apply(plugin = "java-library")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

// Helper to check if module exists
fun hasModule(name: String): Boolean {
    return rootProject.childProjects.containsKey(name)
}

// ============================================
// TASK: buildUniversal - Universal JAR
// ============================================
tasks.register("buildUniversal") {
    group = "build"
    description = "Builds the Universal JAR (works on ALL plugin platforms)"
    
    dependsOn(":universal:shadowJar")
    
    doLast {
        println("")
        println("════════════════════════════════════════════════════════════")
        println("  ✅ Universal JAR Built!")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  🌐 Universal Plugin JAR:")
        println("     universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar")
        println("     ✅ Paper, Spigot, Purpur, Folia, Velocity, BungeeCord, Waterfall")
        println("")
        println("════════════════════════════════════════════════════════════")
    }
}

// ============================================
// TASK: buildMods - Mod JARs (Fabric + Forge)
// ============================================
tasks.register("buildMods") {
    group = "build"
    description = "Builds mod JARs (Fabric + Forge/NeoForge) - if modules are enabled"
    
    val dependencies = mutableListOf<String>()
    if (hasModule("fabric")) dependencies.add(":fabric:remapJar")
    if (hasModule("forge")) dependencies.add(":forge:shadowJar")
    
    if (dependencies.isEmpty()) {
        doLast {
            println("⚠️  Mod modules are disabled in settings.gradle.kts")
            println("   To enable, uncomment fabric and forge in settings.gradle.kts")
        }
    } else {
        dependsOn(dependencies)
        
        doLast {
            if (hasModule("fabric")) {
                println("✅ Fabric JAR: fabric/build/libs/MaintenanceUniversal-Fabric-1.0.0.jar")
            }
            if (hasModule("forge")) {
                println("✅ Forge JAR: forge/build/libs/MaintenanceUniversal-Forge-1.0.0.jar")
            }
        }
    }
}

// ============================================
// TASK: buildPlugins - Plugin JARs
// ============================================
tasks.register("buildPlugins") {
    group = "build"
    description = "Builds all plugin JARs (Paper + Velocity + BungeeCord)"
    
    dependsOn(
        ":paper:shadowJar",
        ":velocity:shadowJar",
        ":bungee:shadowJar"
    )
    
    doLast {
        println("")
        println("════════════════════════════════════════════════════════════")
        println("  ✅ Plugin JARs Built Successfully!")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  ✅ Paper JAR:")
        println("     paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
        println("")
        println("  ✅ Velocity JAR:")
        println("     velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar")
        println("")
        println("  ✅ BungeeCord JAR:")
        println("     bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar")
        println("")
        println("════════════════════════════════════════════════════════════")
    }
}

// ============================================
// TASK: buildAll - EVERYTHING
// ============================================
tasks.register("buildAll") {
    group = "build"
    description = "Builds ALL available JARs (Plugins + Mods if enabled)"
    
    val dependencies = mutableListOf(
        ":universal:shadowJar",
        ":paper:shadowJar",
        ":velocity:shadowJar",
        ":bungee:shadowJar"
    )
    
    if (hasModule("fabric")) dependencies.add(":fabric:remapJar")
    if (hasModule("forge")) dependencies.add(":forge:shadowJar")
    
    dependsOn(dependencies)
    
    doLast {
        println("")
        println("════════════════════════════════════════════════════════════")
        println("  ✅ All JARs Built Successfully!")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  🌐 UNIVERSAL JAR (⭐ Recommended):")
        println("     universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar")
        println("")
        println("  📐 PLUGIN JARs:")
        println("     paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
        println("     velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar")
        println("     bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar")
        
        if (hasModule("fabric") || hasModule("forge")) {
            println("")
            println("  🧩 MOD JARs:")
            if (hasModule("fabric")) {
                println("     fabric/build/libs/MaintenanceUniversal-Fabric-1.0.0.jar")
            }
            if (hasModule("forge")) {
                println("     forge/build/libs/MaintenanceUniversal-Forge-1.0.0.jar")
            }
        }
        
        println("")
        println("════════════════════════════════════════════════════════════")
    }
}

// ============================================
// TASK: buildServer - Server JAR only
// ============================================
tasks.register("buildServer") {
    group = "build"
    description = "Builds Server platform JAR (Paper)"
    dependsOn(":paper:shadowJar")
    
    doLast {
        println("✅ Server JAR: paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
    }
}

// ============================================
// TASK: buildProxy - Proxy JARs only
// ============================================
tasks.register("buildProxy") {
    group = "build"
    description = "Builds Proxy platform JARs (Velocity + BungeeCord)"
    dependsOn(
        ":velocity:shadowJar",
        ":bungee:shadowJar"
    )
    
    doLast {
        println("✅ Velocity JAR: velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar")
        println("✅ BungeeCord JAR: bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar")
    }
}

// ============================================
// TASK: cleanAll - Clean all modules
// ============================================
tasks.register("cleanAll") {
    group = "build"
    description = "Cleans all build directories"
    
    val dependencies = mutableListOf(
        ":common:clean",
        ":paper:clean",
        ":velocity:clean",
        ":bungee:clean",
        ":universal:clean"
    )
    
    if (hasModule("fabric")) dependencies.add(":fabric:clean")
    if (hasModule("forge")) dependencies.add(":forge:clean")
    
    dependsOn(dependencies)
}
