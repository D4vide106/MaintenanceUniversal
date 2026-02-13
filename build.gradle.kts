plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

val modVersion: String by project
val mavenGroup: String by project

allprojects {
    group = mavenGroup
    version = modVersion
    
    repositories {
        mavenCentral()
        
        // Paper
        maven("https://repo.papermc.io/repository/maven-public/")
        
        // PlaceholderAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        
        // Velocity
        maven("https://repo.velocitypowered.com/releases/")
        maven("https://repo.velocitypowered.com/snapshots/")
        
        // Fabric
        maven("https://maven.fabricmc.net/")
        
        // Forge
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net/")
        
        // Other
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
        withJavadocJar()
    }
    
    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
        
        withType<Javadoc> {
            options.encoding = "UTF-8"
        }
        
        withType<ProcessResources> {
            filteringCharset = "UTF-8"
        }
    }
}

// Build all platforms task
tasks.register("buildAll") {
    group = "build"
    description = "Builds all platform implementations"
    
    dependsOn(
        ":paper:shadowJar",
        ":velocity:shadowJar",
        ":fabric:remapJar",
        ":forge:shadowJar"
    )
    
    doLast {
        val outputDir = layout.buildDirectory.dir("distributions").get().asFile
        outputDir.mkdirs()
        
        println("┌──────────────────────────────────────────────────────────────────────┐")
        println("│                                                                      │")
        println("│           🎉 BUILD SUCCESSFUL - ALL PLATFORMS COMPILED 🎉            │")
        println("│                                                                      │")
        println("└──────────────────────────────────────────────────────────────────────┘")
        println("")
        println("✅ Paper:    ${project(":paper").layout.buildDirectory.get()}/libs/")
        println("✅ Velocity: ${project(":velocity").layout.buildDirectory.get()}/libs/")
        println("✅ Fabric:   ${project(":fabric").layout.buildDirectory.get()}/libs/")
        println("✅ Forge:    ${project(":forge").layout.buildDirectory.get()}/libs/")
        println("")
        println("📦 All builds copied to: ${outputDir.absolutePath}")
        println("")
        
        // Copy all jars to distributions folder
        copy {
            from(project(":paper").tasks.named("shadowJar"))
            from(project(":velocity").tasks.named("shadowJar"))
            from(project(":fabric").tasks.named("remapJar"))
            from(project(":forge").tasks.named("shadowJar"))
            into(outputDir)
        }
    }
}

// Clean task
tasks.register("cleanAll") {
    group = "build"
    description = "Cleans all subproject builds"
    
    dependsOn(
        ":common:clean",
        ":paper:clean",
        ":velocity:clean",
        ":fabric:clean",
        ":forge:clean"
    )
}