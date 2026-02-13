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

// Task per creare TUTTI i JAR
tasks.register("buildAll") {
    group = "build"
    description = "Builds all platform JARs"
    
    dependsOn(
        ":paper:shadowJar",
        ":velocity:shadowJar",
        ":bungee:shadowJar"
    )
    
    doLast {
        println("")
        println("════════════════════════════════════════════════════════════")
        println("  ✅ Build Complete!")
        println("════════════════════════════════════════════════════════════")
        println("")
        println("  📐 Server JAR:")
        println("     paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
        println("     ✅ Paper + Spigot + Purpur + Folia + CraftBukkit")
        println("")
        println("  🌐 Proxy JARs:")
        println("     velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar")
        println("     ✅ Velocity 3.0+")
        println("")
        println("     bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar")
        println("     ✅ BungeeCord + Waterfall")
        println("")
        println("════════════════════════════════════════════════════════════")
    }
}

// Task per Server JARs
tasks.register("buildServer") {
    group = "build"
    description = "Builds Server platform JAR (Paper)"
    dependsOn(":paper:shadowJar")
    
    doLast {
        println("✅ Server JAR ready: paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar")
    }
}

// Task per Proxy JARs
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

// Clean all
tasks.register("cleanAll") {
    group = "build"
    description = "Cleans all build directories"
    
    dependsOn(
        ":common:clean",
        ":paper:clean",
        ":velocity:clean",
        ":bungee:clean"
    )
}
