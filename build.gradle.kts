plugins {
    java
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" apply false
    id("net.minecrell.plugin-yml.bungee") version "0.6.0" apply false
}

val minecraftVersion: String by project
val modVersion: String by project
val mavenGroup: String by project

group = mavenGroup
version = modVersion

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    
    group = mavenGroup
    version = modVersion
    
    repositories {
        mavenCentral()
        
        // Minecraft
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "PaperMC"
        }
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.neoforged.net/releases/") {
            name = "NeoForged"
        }
        maven("https://maven.minecraftforge.net/") {
            name = "Forge"
        }
        maven("https://repo.velocitypowered.com/releases/") {
            name = "Velocity"
        }
        
        // Libraries
        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            name = "Sonatype Snapshots"
        }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
            name = "PlaceholderAPI"
        }
        maven("https://repo.dmulloy2.net/repository/public/") {
            name = "ProtocolLib"
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
        withJavadocJar()
    }
}

subprojects {
    apply(plugin = "java-library")
    
    dependencies {
        // Logging
        compileOnly("org.slf4j:slf4j-api:2.0.9")
        
        // Annotations
        compileOnly("org.jetbrains:annotations:24.1.0")
        
        // Testing
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
        testImplementation("org.mockito:mockito-core:5.8.0")
    }
    
    tasks.test {
        useJUnitPlatform()
    }
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds all platform implementations"
    
    dependsOn(
        ":paper:shadowJar",
        ":velocity:shadowJar",
        ":fabric:build",
        ":forge:shadowJar"
    )
    
    doLast {
        val outputDir = file("$buildDir/distributions")
        outputDir.mkdirs()
        
        // Copy Paper
        copy {
            from(project(":paper").tasks.named("shadowJar"))
            into(outputDir)
        }
        
        // Copy Velocity
        copy {
            from(project(":velocity").tasks.named("shadowJar"))
            into(outputDir)
        }
        
        // Copy Fabric
        copy {
            from(project(":fabric").buildDir.resolve("libs"))
            include("*.jar")
            exclude("*-sources.jar", "*-dev.jar")
            into(outputDir)
        }
        
        // Copy Forge
        copy {
            from(project(":forge").tasks.named("shadowJar"))
            into(outputDir)
        }
        
        println("")
        println("=" .repeat(50))
        println("✅ All platforms built successfully!")
        println("=" .repeat(50))
        println("📁 Output directory: ${outputDir.absolutePath}")
        println("")
        println("📦 Generated files:")
        outputDir.listFiles()?.forEach {
            println("  • ${it.name} (${it.length() / 1024}KB)")
        }
        println("=" .repeat(50))
    }
}

tasks.register("clean") {
    group = "build"
    description = "Cleans all build directories"
    
    doLast {
        delete(buildDir)
        subprojects.forEach {
            delete(it.buildDir)
        }
        println("✅ All build directories cleaned!")
    }
}