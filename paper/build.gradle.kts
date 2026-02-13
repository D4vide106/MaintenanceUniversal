plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

val paperVersion: String by project
val minecraftVersion: String by project

dependencies {
    // Common module
    implementation(project(":common"))
    
    // Paper API
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    
    // ProtocolLib (optional)
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    
    // PlaceholderAPI (optional)
    compileOnly("me.clip:placeholderapi:2.11.5")
    
    // bStats
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("MaintenanceUniversal-Paper-${project.version}.jar")
        
        // Relocate dependencies to avoid conflicts
        relocate("org.spongepowered.configurate", "me.d4vide106.maintenance.libs.configurate")
        relocate("com.zaxxer.hikari", "me.d4vide106.maintenance.libs.hikari")
        relocate("redis.clients.jedis", "me.d4vide106.maintenance.libs.jedis")
        relocate("net.kyori", "me.d4vide106.maintenance.libs.kyori")
        relocate("com.google.gson", "me.d4vide106.maintenance.libs.gson")
        relocate("org.bstats", "me.d4vide106.maintenance.libs.bstats")
        
        // Minimize JAR size
        minimize()
        
        // Exclude unnecessary files
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
    apiVersion = "1.13"
    
    // Soft dependencies (optional integrations)
    softDepend = listOf("ProtocolLib", "PlaceholderAPI", "Vault")
    
    // Load priority
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    
    // Commands
    commands {
        register("maintenance") {
            description = "Main maintenance command"
            usage = "/maintenance <enable|disable|toggle|status|reload|whitelist|schedule|stats|info>"
            aliases = listOf("mt", "maint")
            permission = "maintenance.command"
        }
    }
    
    // Permissions
    permissions {
        // Admin wildcard
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
        
        // Individual permissions
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