# 🔧 Maintenance Universal

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.13%2B-brightgreen)

**Professional multi-platform maintenance management system for Minecraft servers**

Supports Fabric • Forge • Paper • Velocity

[Features](#features) • [Installation](#installation) • [Documentation](#documentation) • [API](#api)

</div>

---

## 📋 Features

### Core Features
- ✅ **Multi-Platform Support**: Fabric, Forge (NeoForge), Paper, and Velocity
- 🔄 **Cross-Server Sync**: MySQL and Redis support for multi-server networks
- 🎨 **Modern UI**: MiniMessage formatting with gradient support
- 🌍 **Internationalization**: Full i18n support with custom language files
- ⏲️ **Scheduled Maintenance**: Timer system with countdown and auto-enable
- 👥 **Whitelist System**: Permission-based bypass for staff members
- 📊 **Server List Customization**: Custom MOTD and player count during maintenance

### Advanced Features
- 🔌 **Developer API**: Extensive API for third-party integrations
- 📢 **Discord Integration**: Webhook notifications for maintenance events
- 📈 **Metrics & Analytics**: bStats integration for usage statistics
- 🔐 **Security**: SQL injection protection, input validation
- 🚀 **Performance**: HikariCP connection pooling, async operations
- 🎯 **PlaceholderAPI**: Full PAPI support on Paper platforms

## 🚀 Installation

### Requirements
- Java 17 or higher
- Minecraft 1.13+ (Modern versions)
- Gradle 8.0+ (for building)

### Quick Start

1. **Clone the repository**:
```bash
git clone https://github.com/D4vide106/MaintenanceUniversal.git
cd MaintenanceUniversal
```

2. **Build all platforms**:
```bash
# Linux/Mac
./gradlew clean buildAll

# Windows
gradlew.bat clean buildAll
```

3. **Locate output files**:
```
build/distributions/
├── MaintenanceUniversal-Paper-1.0.0.jar
├── MaintenanceUniversal-Velocity-1.0.0.jar
├── MaintenanceUniversal-Fabric-1.0.0.jar
└── MaintenanceUniversal-Forge-1.0.0.jar
```

4. **Install on your server**:
- Place the appropriate JAR file in your server's plugins/mods folder
- Restart the server
- Configure in `config/maintenance/config.yml`

## 📖 Documentation

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/maintenance enable` | Enable maintenance mode | `maintenance.toggle` |
| `/maintenance disable` | Disable maintenance mode | `maintenance.toggle` |
| `/maintenance whitelist add <player>` | Add player to whitelist | `maintenance.whitelist` |
| `/maintenance whitelist remove <player>` | Remove player from whitelist | `maintenance.whitelist` |
| `/maintenance timer <minutes>` | Schedule maintenance timer | `maintenance.timer` |
| `/maintenance reload` | Reload configuration | `maintenance.reload` |

### Permissions

- `maintenance.admin` - Full access to all features
- `maintenance.bypass` - Bypass maintenance mode
- `maintenance.toggle` - Enable/disable maintenance
- `maintenance.whitelist` - Manage whitelist
- `maintenance.timer` - Manage timers
- `maintenance.reload` - Reload configuration

### Configuration

```yaml
# config.yml example
maintenance:
  enabled: false
  kick-message: "<gradient:red:gold>Server is under maintenance</gradient>"
  whitelist-mode: true
  
database:
  type: sqlite # sqlite, mysql, redis
  mysql:
    host: localhost
    port: 3306
    database: maintenance
    username: root
    password: ""
    
discord:
  enabled: false
  webhook-url: ""
  send-on-enable: true
  send-on-disable: true
```

## 🔌 API

### For Developers

```java
import me.d4vide106.maintenance.api.MaintenanceAPI;
import java.util.UUID;

public class Example {
    public void example() {
        MaintenanceAPI api = MaintenanceAPI.getInstance();
        
        // Check if maintenance is enabled
        if (api.isMaintenanceEnabled()) {
            System.out.println("Maintenance is active!");
        }
        
        // Enable maintenance
        api.enableMaintenance().thenAccept(success -> {
            if (success) {
                System.out.println("Maintenance enabled!");
            }
        });
        
        // Add player to whitelist
        UUID uuid = UUID.fromString("...");
        api.addToWhitelist(uuid, "PlayerName").thenAccept(success -> {
            System.out.println("Player whitelisted: " + success);
        });
        
        // Schedule timer (30 minutes)
        api.scheduleTimer(30, 60).thenAccept(success -> {
            System.out.println("Timer scheduled!");
        });
    }
}
```

### Maven Dependency

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.D4vide106</groupId>
    <artifactId>MaintenanceUniversal</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

## 🏗️ Project Structure

```
MaintenanceUniversal/
├── common/          # Shared code across all platforms
├── paper/           # Paper/Spigot implementation
├── velocity/        # Velocity proxy implementation
├── fabric/          # Fabric mod implementation
├── forge/           # Forge/NeoForge mod implementation
├── scripts/         # Build and installation scripts
└── .github/         # GitHub Actions CI/CD
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📊 Comparison

### Why Maintenance Universal?

| Feature | Maintenance Universal | Other Plugins |
|---------|----------------------|---------------|
| Multi-Platform | ✅ Fabric, Forge, Paper, Velocity | ❌ Usually single platform |
| Cross-Server Sync | ✅ MySQL + Redis | ❌ Limited or none |
| Modern API | ✅ CompletableFuture based | ⚠️ Basic or none |
| Discord Integration | ✅ Webhooks included | ❌ Requires separate plugin |
| Performance | ✅ Async, connection pooling | ⚠️ Varies |
| Developer Friendly | ✅ Full API, events | ⚠️ Limited |

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**D4vide106**
- GitHub: [@D4vide106](https://github.com/D4vide106)
- Organization: [@Infinity-Wonderful](https://github.com/Infinity-Wonderful)

## 🌟 Support

If you find this project useful, please consider giving it a ⭐ on GitHub!

---

<div align="center">
Made with ❤️ by D4vide106
</div>