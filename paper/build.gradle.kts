plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Common module
    implementation(project(":common"))
    
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    
    // ProtocolLib (optional)
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    
    // PlaceholderAPI (optional)
    compileOnly("me.clip:placeholderapi:2.11.6")
    
    // bStats
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("MaintenanceUniversal-Paper")
        
        // Relocate dependencies
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.libs.configurate")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.libs.hikari")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.libs.jedis")
        relocate("net.kyori", "me.d4vide106.maintenance.libs.kyori")
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

// Plugin YAML configuration
bukkit {
    name = "MaintenanceUniversal"
    version = project.version.toString()
    description = "Professional multi-platform maintenance management system"
    author = "D4vide106"
    website = "https://github.com/D4vide106/MaintenanceUniversal"
    
    main = "me.d4vide106.maintenance.paper.MaintenancePaper"
    apiVersion = "1.14"
    
    softDepend = listOf("ProtocolLib", "PlaceholderAPI", "Vault")
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    
    commands {
        register("maintenance") {
            description = "Main maintenance command"
            usage = "/maintenance <enable|disable|toggle|status|reload|whitelist|schedule|stats|info>"
            aliases = listOf("mt", "maint")
            permission = "maintenance.command"
        }
    }
    
    permissions {
        register("maintenance.admin") {
            description = "Grants all maintenance permissions"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
            children = listOf(
                "maintenance.command",
                "maintenance.bypass",
                "maintenance.toggle",
                "maintenance.schedule",
                "maintenance.whitelist",
                "maintenance.reload",
                "maintenance.stats",
                "maintenance.notify"
            )
        }
        
        register("maintenance.command") {
            description = "Use maintenance commands"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.bypass") {
            description = "Bypass maintenance mode"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.toggle") {
            description = "Enable/disable maintenance"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.schedule") {
            description = "Schedule maintenance timers"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.whitelist") {
            description = "Manage maintenance whitelist"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.reload") {
            description = "Reload plugin configuration"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.stats") {
            description = "View maintenance statistics"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
        
        register("maintenance.notify") {
            description = "Receive maintenance notifications"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
    }
}
