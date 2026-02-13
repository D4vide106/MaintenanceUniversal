# MaintenanceUniversal - Velocity Edition

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/D4vide106/MaintenanceUniversal)
[![Velocity](https://img.shields.io/badge/velocity-3.0+-green.svg)](https://papermc.io/software/velocity)
[![Java](https://img.shields.io/badge/java-17+-orange.svg)](https://adoptium.net)

**Professional proxy-level maintenance management** for Velocity, BungeeCord, and Waterfall proxies.

---

## ✨ Features

### 🌐 Proxy-Level Control
- **Network-Wide Maintenance** - Control all backend servers from proxy
- **Fallback Server Support** - Redirect players to maintenance lobby
- **Connection Blocking** - Block joins before reaching backend servers
- **Custom MOTD** - Modify server list appearance during maintenance
- **Version String** - Customize displayed server version

### 🔒 Access Control
- **Permission-Based Bypass** - Allow staff with `maintenance.bypass`
- **Whitelist System** - Flexible player whitelist with reasons
- **Kick Options** - Disconnect or redirect to fallback server

### 🔄 Multi-Proxy Sync
- **Redis Integration** - Synchronized maintenance across proxies
- **Real-time Updates** - Changes propagate instantly
- **Shared Whitelist** - Network-wide whitelist management
- **Scalable** - Support unlimited proxy instances

### 📊 Statistics & Monitoring
- **Session Tracking** - Track maintenance sessions
- **Connection Blocks** - Monitor blocked connection attempts
- **Duration Tracking** - Record total maintenance time

---

## 📦 Installation

### Requirements
- **Java 17+**
- **Velocity 3.0+** (or BungeeCord/Waterfall compatible version)
- **(Optional)** Redis server for multi-proxy sync
- **(Optional)** MySQL/MariaDB for shared database

### Steps

1. **Download** `MaintenanceUniversal-Velocity-1.0.0.jar`

2. **Place** in `plugins/` folder:
   ```
   velocity/
   ├── plugins/
   │   └── MaintenanceUniversal-Velocity-1.0.0.jar
   └── velocity.toml
   ```

3. **Restart** your proxy

4. **Configure** `plugins/maintenanceuniversal/config.yml`

5. **Reload** with `/maintenance reload`

---

## ⚙️ Configuration

### Basic Config

```yaml
# Database
database:
  type: 'sqlite'  # sqlite, mysql
  # For MySQL:
  # type: 'mysql'
  # host: 'localhost'
  # port: 3306
  # database: 'maintenance'
  # username: 'root'
  # password: 'password'

# Redis multi-proxy sync
redis:
  enabled: false  # Enable for multi-proxy
  host: 'localhost'
  port: 6379
  password: ''
  database: 0
  channel: 'maintenance'

# Maintenance settings
maintenance:
  kick-on-enable: true
  kick-delay: 5
  kick-message: |
    <red><bold>Server Under Maintenance</bold></red>
    
    <gray>We're performing maintenance.</gray>
    <gray>Please check back later!</gray>
  
  # Server list MOTD
  motd:
    enabled: true
    line1: '<red><bold>⚠ MAINTENANCE MODE ⚠</bold></red>'
    line2: '<gray>Scheduled maintenance in progress</gray>'
  
  # Version text
  version:
    enabled: false
    text: 'Maintenance'
  
  # Max players display
  max-players:
    enabled: false
    value: 0

# Velocity-specific
velocity:
  # Proxy-level maintenance
  proxy-mode: true
  
  # Fallback server (maintenance lobby)
  fallback-server: ''  # Leave empty to disconnect
  
  # Kick to fallback instead of disconnect
  kick-to-fallback: false
```

---

## 🎮 Commands

### Main Command
```
/maintenance [subcommand]
```

**Aliases:** `/mt`, `/maint`

---

### Available Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/maintenance enable [reason]` | Enable maintenance | `maintenance.admin` |
| `/maintenance disable` | Disable maintenance | `maintenance.admin` |
| `/maintenance toggle` | Toggle on/off | `maintenance.admin` |
| `/maintenance status` | View status | `maintenance.admin` |
| `/maintenance whitelist add <player> [reason]` | Add to whitelist | `maintenance.admin` |
| `/maintenance whitelist remove <player>` | Remove from whitelist | `maintenance.admin` |
| `/maintenance whitelist list` | List whitelisted | `maintenance.admin` |
| `/maintenance whitelist clear` | Clear whitelist | `maintenance.admin` |
| `/maintenance stats` | View statistics | `maintenance.admin` |
| `/maintenance reload` | Reload config | `maintenance.admin` |

---

## 🔐 Permissions

| Permission | Description |
|------------|-------------|
| `maintenance.admin` | Full access to all commands |
| `maintenance.bypass` | Bypass maintenance mode |
| `maintenance.command` | Use basic commands |
| `maintenance.notify` | Receive admin notifications |

---

## 🌐 Multi-Proxy Setup

### Network Architecture

```
         Redis Server (Pub/Sub)
              │
    ┌─────────┬─────────┐
    │         │         │
  Proxy 1   Proxy 2   Proxy 3
    │         │         │
  Backend Servers (Paper/Spigot)
```

### Setup Steps

1. **Install Redis** on your network

2. **Configure each proxy** identically:

```yaml
redis:
  enabled: true
  host: 'your-redis-server.com'
  port: 6379
  password: 'your-password'
  database: 0
  channel: 'maintenance'

database:
  type: 'mysql'  # Use shared MySQL
  host: 'your-mysql-server.com'
  port: 3306
  database: 'maintenance'
  username: 'minecraft'
  password: 'secure-password'
```

3. **Restart all proxies**

4. **Test** by enabling maintenance on one proxy:
   ```
   /maintenance enable Testing sync
   ```

5. **Verify** on other proxies:
   ```
   /maintenance status
   ```

### What Gets Synced?

✅ Maintenance enable/disable  
✅ Whitelist changes  
✅ Configuration reloads  
✅ Statistics updates  

---

## 🎯 Fallback Server (Maintenance Lobby)

### Setup Maintenance Lobby

1. **Create** a dedicated maintenance server:
   - Lightweight Paper/Spigot server
   - Custom MOTD/spawn
   - Information signs/holograms

2. **Register** in `velocity.toml`:

```toml
[servers]
maintenance = "127.0.0.1:25566"
lobby = "127.0.0.1:25567"
survival = "127.0.0.1:25568"
```

3. **Configure** in MaintenanceUniversal:

```yaml
velocity:
  fallback-server: 'maintenance'
  kick-to-fallback: true
```

4. **Restart** proxy

### How It Works

- **Non-whitelisted players** are redirected to maintenance server
- **Whitelisted players** can access all servers
- **Players with bypass** permission are unaffected

---

## 📊 Example Scenarios

### Scenario 1: Quick Maintenance

```bash
# Enable maintenance
/maintenance enable Quick restart

# Add staff to whitelist
/maintenance whitelist add Steve Admin
/maintenance whitelist add Alex Moderator

# Disable when done
/maintenance disable
```

---

### Scenario 2: Scheduled Maintenance

```yaml
# Use external scheduler (e.g., CommandScheduler)
# Schedule for 3 AM daily

commands:
  - time: "03:00"
    command: "maintenance enable Scheduled daily maintenance"
  
  - time: "03:30"
    command: "maintenance disable"
```

---

### Scenario 3: Network-Wide Maintenance

**On any proxy:**
```bash
/maintenance enable Network updates
```

**Result:**
- All proxies enter maintenance
- All backend servers remain accessible to whitelisted players
- Redis syncs state across network

---

## 🛠️ Troubleshooting

### Plugin won't load

1. Check Java version:
   ```bash
   java -version
   # Should be 17 or higher
   ```

2. Check Velocity version:
   ```bash
   # In velocity.toml or startup log
   # Should be 3.0.0+
   ```

3. Review `logs/latest.log` for errors

---

### Redis sync not working

1. **Test Redis connection:**
   ```bash
   redis-cli -h your-host -p 6379 PING
   # Should return: PONG
   ```

2. **Check credentials** in config

3. **Verify channel** name matches on all proxies

4. **Check Redis logs** for connection attempts

---

### Players can still join

1. **Check maintenance is enabled:**
   ```bash
   /maintenance status
   ```

2. **Verify player doesn't have bypass:**
   ```bash
   # Check if player has maintenance.bypass permission
   ```

3. **Check whitelist:**
   ```bash
   /maintenance whitelist list
   ```

---

### Fallback server not working

1. **Verify server exists in velocity.toml:**
   ```toml
   [servers]
   maintenance = "ip:port"
   ```

2. **Check server is online:**
   ```bash
   # Try connecting directly
   ```

3. **Verify config setting:**
   ```yaml
   velocity:
     fallback-server: 'maintenance'  # Must match velocity.toml
     kick-to-fallback: true
   ```

---

## 💻 API Usage

### Get API Instance

```java
import me.d4vide106.maintenance.api.MaintenanceAPI;

MaintenanceAPI api = MaintenanceAPI.getInstance();
```

### Check Maintenance Status

```java
if (api.isMaintenanceEnabled()) {
    // Maintenance is active
}
```

### Enable Maintenance

```java
import me.d4vide106.maintenance.api.MaintenanceMode;

api.enableMaintenance(
    MaintenanceMode.GLOBAL,
    "API-triggered maintenance"
).thenAccept(success -> {
    if (success) {
        // Enabled successfully
    }
});
```

### Check Whitelist

```java
UUID playerUUID = player.getUniqueId();

if (api.isWhitelisted(playerUUID)) {
    // Player is whitelisted
}
```

---

## 📚 Additional Resources

- **[Main README](../README.md)** - Overview and all platforms
- **[API Documentation](../api/README.md)** - Full API reference
- **[GitHub Wiki](https://github.com/D4vide106/MaintenanceUniversal/wiki)** - Detailed guides
- **[GitHub Issues](https://github.com/D4vide106/MaintenanceUniversal/issues)** - Report bugs
- **[Discussions](https://github.com/D4vide106/MaintenanceUniversal/discussions)** - Ask questions

---

## ❓ FAQ

**Q: Does this work with BungeeCord?**  
A: Yes! The Velocity module is compatible with BungeeCord and Waterfall.

**Q: Can I use this with Paper servers?**  
A: Yes! Install the Paper module on backend servers for additional features.

**Q: Do I need Redis?**  
A: Only if you have multiple proxies. Single proxy works with SQLite.

**Q: Can I have different maintenance per server?**  
A: Use the Paper module on individual servers for per-server maintenance.

**Q: How do I update?**  
A: Download new JAR, replace old one, restart proxy.

---

## 📧 Support

- **🐛 Issues:** [GitHub Issues](https://github.com/D4vide106/MaintenanceUniversal/issues)
- **💬 Discussions:** [GitHub Discussions](https://github.com/D4vide106/MaintenanceUniversal/discussions)
- **📖 Wiki:** [Documentation](https://github.com/D4vide106/MaintenanceUniversal/wiki)

---

## 📄 License

MIT License - See [LICENSE](../LICENSE) for details.

---

**Made with ❤️ for the Minecraft community**

**Star ⭐ this repo if you find it useful!**
