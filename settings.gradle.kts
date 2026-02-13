pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.architectury.dev/") {
            name = "Architectury"
        }
        maven("https://maven.neoforged.net/releases/") {
            name = "NeoForged"
        }
        maven("https://maven.minecraftforge.net/") {
            name = "Forge"
        }
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "MaintenanceUniversal"

// Platform modules
include("common")
include("paper")
include("velocity")
include("fabric")
include("forge")