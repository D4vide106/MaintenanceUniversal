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

// ============================================
// TASK: buildUniversal - Universal JAR
// ============================================
tasks.register("buildUniversal") {
    group = "build"
    description = "Builds the Universal JAR (works on ALL platforms)"
    
    dependsOn(":universal:shadowJar")
    
    doLast {
        println("")
        println("════════════════════════════════════════════════════════════")
        println("  ✅ Universal JAR Built!")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  🌐 Universal JAR (ALL platforms):")
        println("     universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar")
        println("")
        println("  ✅ Works on:")
        println("     - Paper 1.13+")
        println("     - Spigot 1.13+")
        println("     - Purpur 1.13+")
        println("     - Folia 1.19.4+")
        println("     - Velocity 3.0+")
        println("     - BungeeCord (latest)")
        println("     - Waterfall (all versions)")
        println("")
        println("  💡 Auto-detects platform and loads correct implementation!")
        println("")
        println("════════════════════════════════════════════════════════════")
    }
}

// ============================================
// TASK: buildAll - ALL JARs (Universal + Singles)
// ============================================
tasks.register("buildAll") {
    group = "build"
    description = "Builds Universal JAR + all individual platform JARs"
    
    dependsOn(
        ":universal:shadowJar",
        ":paper:shadowJar",
        ":velocity:shadowJar",
        ":bungee:shadowJar"
    )
    
    doLast {
        println("")
        println("════════════════════════════════════════════════════════════")
        println("  ✅ All JARs Built!")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  🌐 Universal JAR (Recommended):")
        println("     universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar")
        println("     ✅ Auto-detects: Paper, Spigot, Purpur, Folia, Velocity, BungeeCord, Waterfall")
        println("")
        println("  📐 Individual Server JAR:")
        println("     paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
        println("     ✅ Paper + Spigot + Purpur + Folia + CraftBukkit")
        println("")
        println("  🌐 Individual Proxy JARs:")
        println("     velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar")
        println("     ✅ Velocity 3.0+")
        println("")
        println("     bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar")
        println("     ✅ BungeeCord + Waterfall")
        println("")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  💡 Use Universal JAR for simplicity, or individual JARs for smaller size!")
        println("")
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
        println("✅ Server JAR ready: paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
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
    
    dependsOn(
        ":common:clean",
        ":paper:clean",
        ":velocity:clean",
        ":bungee:clean",
        ":universal:clean"
    )
}
