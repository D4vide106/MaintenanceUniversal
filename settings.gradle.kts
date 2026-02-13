pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "MaintenanceUniversal"

// Common module (shared code)
include("common")

// Plugin implementations (ALWAYS included)
include("paper")
include("velocity")
include("bungee")
include("universal")  // Universal JAR with all platforms

// Mod implementations (OPTIONAL - comment out if causing issues)
// Uncomment these when you want to build mods:
// include("fabric")
// include("forge")

project(":common").projectDir = file("common")
project(":paper").projectDir = file("paper")
project(":velocity").projectDir = file("velocity")
project(":bungee").projectDir = file("bungee")
project(":universal").projectDir = file("universal")
// project(":fabric").projectDir = file("fabric")
// project(":forge").projectDir = file("forge")
