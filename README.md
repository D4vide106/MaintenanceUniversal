# 🔧 Maintenance Universal

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.13--1.26-green)
![License](https://img.shields.io/badge/license-MIT-orange)
![Platform](https://img.shields.io/badge/platform-Paper%20%7C%20Velocity%20%7C%20Fabric%20%7C%20Forge-purple)

**Professional multi-platform maintenance management system for Minecraft**

Supports Paper, Velocity, Fabric, and Forge with unified configuration and API

[Features](#-features) • [Installation](#-installation) • [Commands](#-commands) • [API](#-api) • [Contributing](#-contributing)

</div>

---

## 🎯 Features

### ✨ Core Features
- ✅ **Multi-Platform Support** - Works on Paper, Velocity, Fabric, and Forge
- 🔄 **Multi-Server Sync** - Redis integration for network-wide maintenance
- ⏰ **Scheduled Maintenance** - Automatic timers with countdown warnings
- 📝 **Whitelist System** - Advanced player whitelist with permissions
- 🎨 **MiniMessage Support** - Rich text formatting with gradients and colors
- 📦 **Database Support** - SQLite, MySQL, MariaDB, PostgreSQL
- 🔔 **Discord Integration** - Webhook notifications with rich embeds
- 📊 **Statistics** - Track sessions, durations, and player metrics
- 🌐 **Multi-Language** - Support for 7+ languages
- 🔌 **PlaceholderAPI** - Full integration for other plugins

### 🚀 Advanced Features
- Custom MOTD during maintenance
- Server list modification (version text, player count, icon)
- Boss bar countdown with customizable colors
- Action bar notifications
- Title/subtitle messages
- Sound effects on warnings
- Auto-kick with delay
- Permission-based bypass
- Command execution on enable/disable
- Auto-restart after maintenance
- Rate limiting and caching
- Async operations for performance

---

## 📦 Installation

### Paper / Spigot / Bukkit

1. Download the latest `MaintenanceUniversal-Paper-X.X.X.jar`
2. Place in your `plugins/` folder
3. Restart the server
4. Configure `plugins/MaintenanceUniversal/config.yml`

### Velocity

1. Download the latest `MaintenanceUniversal-Velocity-X.X.X.jar`
2. Place in your `plugins/` folder
3. Restart the proxy
4. Configure `plugins/MaintenanceUniversal/config.yml`

### Fabric

1. Download the latest `MaintenanceUniversal-Fabric-X.X.X.jar`
2. Place in your `mods/` folder
3. Install Fabric API if not already installed
4. Restart the server
5. Configure `config/MaintenanceUniversal/config.yml`

### Forge / NeoForge

1. Download the latest `MaintenanceUniversal-Forge-X.X.X.jar`
2. Place in your `mods/` folder
3. Restart the server
4. Configure `config/MaintenanceUniversal/config.yml`

---

## 💻 Commands

### Main Commands

```
/maintenance enable [reason]          - Enable maintenance mode
/maintenance disable                  - Disable maintenance mode
/maintenance toggle                   - Toggle maintenance on/off
/maintenance status                   - View current status
/maintenance reload                   - Reload configuration
```

### Timer Commands

```
/maintenance schedule <delay> <duration>  - Schedule maintenance
/maintenance timer cancel                 - Cancel active timer
/maintenance timer status                 - View timer status
```

### Whitelist Commands

```
/maintenance whitelist add <player> [reason]  - Add player to whitelist
/maintenance whitelist remove <player>        - Remove player from whitelist
/maintenance whitelist list                   - List whitelisted players
/maintenance whitelist clear                  - Clear entire whitelist
```

### Server Commands (Velocity Only)

```
/maintenance server <server> enable   - Enable for specific server
/maintenance server <server> disable  - Disable for specific server
/maintenance server list              - List server statuses
```

### Admin Commands

```
/maintenance stats           - View statistics
/maintenance message <type>  - Edit messages
/maintenance motd <text>     - Set custom MOTD
/maintenance info            - Plugin information
```

---

## 🛡️ Permissions

### Basic Permissions

| Permission | Description | Default |
|------------|-------------|----------|
| `maintenance.command` | Use main command | OP |
| `maintenance.bypass` | Bypass maintenance | OP |
| `maintenance.notify` | Receive admin notifications | OP |

### Admin Permissions

| Permission | Description | Default |
|------------|-------------|----------|
| `maintenance.toggle` | Enable/disable maintenance | OP |
| `maintenance.schedule` | Schedule timers | OP |
| `maintenance.whitelist` | Manage whitelist | OP |
| `maintenance.reload` | Reload configuration | OP |
| `maintenance.stats` | View statistics | OP |

### Wildcard

```yaml
maintenance.admin:  # Grants all permissions
  - maintenance.*
```

---

## 📚 API

### Getting Started

#### Maven

```xml
<repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.D4vide106</groupId>
    <artifactId>MaintenanceUniversal</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.D4vide106:MaintenanceUniversal:1.0.0")
}
```

### API Usage

```java
import me.d4vide106.maintenance.api.MaintenanceAPI;

public class MyPlugin {
    
    public void example() {
        // Get API instance
        MaintenanceAPI api = MaintenanceAPI.getInstance();
        
        // Check if maintenance is enabled
        if (api.isMaintenanceEnabled()) {
            System.out.println("Maintenance is active!");
        }
        
        // Enable maintenance
        api.enableMaintenance().thenAccept(success -> {
            if (success) {
                api.broadcastNotification("Maintenance enabled!");
            }
        });
        
        // Add player to whitelist
        UUID playerUuid = UUID.fromString("...");
        api.addToWhitelist(playerUuid, "PlayerName", "Staff member")
           .thenAccept(added -> {
               System.out.println("Player added: " + added);
           });
        
        // Schedule maintenance
        api.scheduleTimer(
            Duration.ofMinutes(10),  // Start in 10 minutes
            Duration.ofHours(1)       // Duration: 1 hour
        );
    }
}
```

### Events

```java
import me.d4vide106.maintenance.api.event.*;

public class MaintenanceListener {
    
    // Listen for maintenance enable (cancellable)
    public void onMaintenanceEnable(MaintenanceEnableEvent event) {
        if (event.getReason().contains("emergency")) {
            // Allow emergency maintenance
            return;
        }
        // Cancel non-emergency maintenance
        event.setCancelled(true);
    }
    
    // Listen for player kick (cancellable)
    public void onPlayerKick(PlayerMaintenanceKickEvent event) {
        UUID uuid = event.getPlayerUuid();
        if (hasSpecialPermission(uuid)) {
            event.setCancelled(true);  // Don't kick this player
        }
    }
    
    // Listen for maintenance enabled (not cancellable)
    public void onMaintenanceEnabled(MaintenanceEnabledEvent event) {
        // Send notification to Discord, log, etc.
        logToDiscord("Maintenance enabled by " + event.getEnabledBy());
    }
}
```

---

## 🛠️ Building from Source

### Prerequisites

- Java 17 or higher
- Git

### Clone & Build

```bash
git clone https://github.com/D4vide106/MaintenanceUniversal.git
cd MaintenanceUniversal

# Build all platforms
./gradlew clean buildAll

# Build specific platform
./gradlew :paper:shadowJar
./gradlew :velocity:shadowJar
./gradlew :fabric:remapJar
./gradlew :forge:shadowJar
```

Output files will be in `build/distributions/`

---

## 🌐 Multi-Language Support

### Available Languages

- 🇬🇧 English (en_US)
- 🇮🇹 Italian (it_IT)
- 🇪🇸 Spanish (es_ES)
- 🇩🇪 German (de_DE)
- 🇫🇷 French (fr_FR)
- 🇧🇷 Portuguese (pt_BR)
- 🇷🇺 Russian (ru_RU)

Set in `config.yml`:

```yaml
settings:
  language: it_IT
```

---

## 📊 Statistics

View plugin statistics at [bStats](https://bstats.org/plugin/bukkit/MaintenanceUniversal)

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Use Java 17 features
- Follow Google Java Style Guide
- Add JavaDoc comments for public APIs
- Write unit tests for new features

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**D4vide106**

- GitHub: [@D4vide106](https://github.com/D4vide106)
- Discord: [Join Server](https://discord.gg/your-server)

---

## ⭐ Support

If you like this project, please consider:

- Giving it a ⭐ on GitHub
- Sharing it with friends
- Contributing code or translations
- Reporting bugs and suggesting features

---

## 📢 Links

- [SpigotMC](https://www.spigotmc.org/resources/)
- [Modrinth](https://modrinth.com/plugin/maintenance-universal)
- [Hangar](https://hangar.papermc.io/D4vide106/MaintenanceUniversal)
- [Documentation](https://github.com/D4vide106/MaintenanceUniversal/wiki)
- [Discord Support](https://discord.gg/your-server)

---

<div align="center">

**Made with ❤️ by D4vide106**

</div>