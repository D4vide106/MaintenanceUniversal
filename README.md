# MaintenanceUniversal

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/D4vide106/MaintenanceUniversal)
[![Minecraft](https://img.shields.io/badge/minecraft-1.13--1.21+-green.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/java-17+-orange.svg)](https://adoptium.net)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

**Professional multi-platform maintenance management system** for Minecraft servers with Redis sync, advanced scheduling, and comprehensive API.

---

## 🎯 Universal Platform Support

| Platform | Status | Features |
|----------|:------:|----------|
| 📜 **Paper/Spigot/Purpur** | ✅ | Full support, PlaceholderAPI, ProtocolLib |
| 🌐 **Velocity** | ✅ | Proxy-level maintenance, network-wide control |
| 🧵 **Fabric** | ✅ | Client/Server mod support |
| ⛏️ **Forge** | ✅ | Mod support, NeoForge compatible |
| 🧪 **Folia** | ✅ | Regionized threading support |
| 🌉 **BungeeCord** | 🚧 | Use Velocity module (compatible) |

---

## 📋 Table of Contents

- [Features](#-features)
- [Installation](#-installation)
- [Commands](#-commands)
- [Permissions](#-permissions)
- [Configuration](#-configuration)
- [Placeholders](#-placeholders)
- [API Usage](#-api-usage)
- [Database](#-database)
- [Redis Multi-Server Sync](#-redis-multi-server-sync)
- [Platform-Specific Guides](#-platform-specific-guides)
- [FAQ](#-faq)

---

## ✨ Features

### 🔒 Core Features
- **Global Maintenance Mode** - Block all non-whitelisted players across network
- **Flexible Whitelist System** - Per-player whitelist with reasons and timestamps
- **Advanced Scheduling** - Schedule maintenance with automatic countdown warnings
- **Statistics Tracking** - Track sessions, duration, kicks, and blocked connections
- **Custom MOTD** - Customize server list appearance during maintenance
- **Rich Text Support** - MiniMessage/Adventure API for modern text formatting

### 🚀 Technical Features
- **Redis Multi-Server Sync** - Synchronized maintenance across unlimited servers
- **Multiple Database Support** - SQLite, MySQL, PostgreSQL
- **Developer API** - Full API for external plugin/mod integration
- **PlaceholderAPI Integration** - 12+ maintenance placeholders (Paper)
- **ProtocolLib Support** - Advanced packet manipulation (Paper, optional)
- **HikariCP Connection Pool** - Optimized database performance
- **Async Operations** - Non-blocking database and Redis operations

### 🌍 Platform-Specific Features

#### Paper/Spigot/Purpur
- PlaceholderAPI expansion
- ProtocolLib MOTD manipulation
- Legacy 1.13+ support
- Folia regionized threading
- bStats metrics

#### Velocity
- Proxy-level maintenance
- Network-wide control
- Player routing
- Status server fallback

#### Fabric
- Client-side notifications
- Server-side enforcement
- Mod integration API

#### Forge/NeoForge
- Forge event system
- Config screen GUI
- Mod compatibility layer

---

## 📦 Installation

### Requirements

| Platform | Java | Minecraft | Optional |
|----------|------|-----------|----------|
| Paper | 17+ | 1.13-1.21+ | PlaceholderAPI, ProtocolLib |
| Velocity | 17+ | 1.7.2-1.21+ | - |
| Fabric | 17+ | 1.19+ | Fabric API |
| Forge | 17+ | 1.19+ | - |

### Quick Start

1. **Download** the appropriate JAR for your platform:
   - `MaintenanceUniversal-Paper-1.0.0.jar` (Paper/Spigot/Purpur/Folia)
   - `MaintenanceUniversal-Velocity-1.0.0.jar` (Velocity proxy)
   - `MaintenanceUniversal-Fabric-1.0.0.jar` (Fabric)
   - `MaintenanceUniversal-Forge-1.0.0.jar` (Forge/NeoForge)

2. **Place** the JAR in the appropriate folder:
   - Paper: `plugins/`
   - Velocity: `plugins/`
   - Fabric: `mods/`
   - Forge: `mods/`

3. **Restart** your server/proxy

4. **Configure** the generated config file

5. **Reload** with `/maintenance reload`

---

## 🎮 Commands

### Main Command
```
/maintenance [subcommand]
```

**Aliases:** `/mt`, `/maint`

---

### 🔧 Maintenance Control

#### Enable Maintenance
```bash
/maintenance enable [reason]
```
Activates maintenance mode with optional reason.

**Examples:**
```bash
/maintenance enable
/maintenance enable Server updates in progress
/maintenance enable <red>Critical bug fixes - ETA 30 min</red>
/maintenance enable <gradient:red:yellow>Scheduled maintenance</gradient>
```

**Features:**
- Kicks all non-whitelisted players (configurable)
- Broadcasts reason to all players
- Logs session start with timestamp
- Syncs across network via Redis

---

#### Disable Maintenance
```bash
/maintenance disable
```
Deactivates maintenance mode.

**Effects:**
- Allows all players to join
- Saves session statistics
- Calculates total duration
- Broadcasts to network

---

#### Toggle Maintenance
```bash
/maintenance toggle
```
Quick enable/disable toggle.

---

### 📊 Information Commands

#### Check Status
```bash
/maintenance status
```
or
```bash
/maintenance info
```

**Output:**
```
═══ Maintenance Status ═══
Enabled: ✅ Yes
Mode: GLOBAL
Reason: Server updates
Duration: 15m 30s
Timer: 45m remaining
Whitelist: 5 players
```

---

#### View Statistics
```bash
/maintenance stats
```

**Output:**
```
═══ Maintenance Statistics ═══
Total Sessions: 42
Total Duration: 5h 23m 47s
Players Kicked: 156
Connections Blocked: 1,234
Last Session: 2h 15m ago
```

---

### 👥 Whitelist Management

#### Add Player
```bash
/maintenance whitelist add <player> [reason]
```

**Examples:**
```bash
/maintenance whitelist add Steve
/maintenance whitelist add Alex Admin access
/maintenance whitelist add Notch Developer testing
```

**Features:**
- Supports online and offline players
- Records who added them
- Stores timestamp
- Syncs via Redis

---

#### Remove Player
```bash
/maintenance whitelist remove <player>
```

---

#### List Whitelist
```bash
/maintenance whitelist list
```

**Output:**
```
═══ Whitelisted Players (5) ═══
• Steve - Admin access
  Added by: Console, 2 days ago
• Alex - Developer
  Added by: Notch, 5 hours ago
• Herobrine - Testing
  Added by: Steve, 30 minutes ago
```

---

#### Clear Whitelist
```bash
/maintenance whitelist clear
```
Removes all whitelisted players (requires confirmation).

---

### ⏰ Scheduling & Timers

#### Schedule Maintenance
```bash
/maintenance schedule <delay> <duration>
```

**Time Formats:**
- `s` or `sec` = seconds
- `m` or `min` = minutes
- `h` or `hour` = hours
- `d` or `day` = days

**Examples:**
```bash
/maintenance schedule 10m 1h
# Starts in 10 minutes, lasts 1 hour

/maintenance schedule 30s 5m
# Starts in 30 seconds, lasts 5 minutes

/maintenance schedule 2h 30m
# Starts in 2 hours, lasts 30 minutes

/maintenance schedule 1d 2h
# Starts in 1 day, lasts 2 hours
```

**Features:**
- Automatic countdown warnings at configured intervals
- Title + sound notifications (configurable)
- Action bar countdown
- Boss bar progress indicator
- Auto-enable at scheduled time
- Auto-disable after duration

**Default Warning Intervals:**
- 5 minutes (300s)
- 3 minutes (180s)
- 1 minute (60s)
- 30 seconds
- 10 seconds

---

#### Check Timer
```bash
/maintenance timer status
```

**Output:**
```
═══ Active Timer ═══
Scheduled Start: 9m 45s
Estimated End: 1h 9m 45s
Duration: 1 hour
Mode: GLOBAL
```

---

#### Cancel Timer
```bash
/maintenance timer cancel
```
Cancels scheduled maintenance (doesn't affect current state).

---

### 🔄 Configuration

#### Reload Config
```bash
/maintenance reload
```

**Reloads:**
- Configuration file
- Messages
- Timer settings
- MOTD settings

**Does NOT reload:**
- Database connections
- Redis connections

---

## 🔐 Permissions

### Permission Nodes

| Permission | Description | Default |
|------------|-------------|:-------:|
| `maintenance.admin` | 🔑 Full access (grants all below) | OP |
| `maintenance.command` | Use basic commands | OP |
| `maintenance.bypass` | 🚪 Bypass maintenance mode | OP |
| `maintenance.toggle` | Enable/disable maintenance | OP |
| `maintenance.schedule` | ⏰ Schedule timers | OP |
| `maintenance.whitelist` | 👥 Manage whitelist | OP |
| `maintenance.whitelist.add` | Add to whitelist | OP |
| `maintenance.whitelist.remove` | Remove from whitelist | OP |
| `maintenance.whitelist.clear` | Clear whitelist | OP |
| `maintenance.reload` | 🔄 Reload configuration | OP |
| `maintenance.stats` | 📊 View statistics | OP |
| `maintenance.notify` | 🔔 Receive admin notifications | OP |

### Permission Examples

**Full Admin:**
```yaml
groups:
  admin:
    permissions:
      - maintenance.admin
```

**Moderator (can enable/whitelist):**
```yaml
groups:
  moderator:
    permissions:
      - maintenance.toggle
      - maintenance.whitelist
      - maintenance.bypass
```

**VIP (bypass only):**
```yaml
groups:
  vip:
    permissions:
      - maintenance.bypass
```

---

## ⚙️ Configuration

### Main Config (config.yml)

```yaml
#══════════════════════════════════════════════════#
#          MaintenanceUniversal Configuration          #
#══════════════════════════════════════════════════#

# Database configuration
database:
  # Type: sqlite, mysql, postgresql
  type: 'sqlite'
  
  # MySQL/PostgreSQL settings
  host: 'localhost'
  port: 3306
  database: 'maintenance'
  username: 'root'
  password: 'password'
  
  # Connection pool
  pool-size: 10
  connection-timeout: 5000
  
  # Table prefix
  table-prefix: 'maintenance_'

# Redis multi-server sync
redis:
  enabled: false
  host: 'localhost'
  port: 6379
  password: ''
  database: 0
  channel: 'maintenance'
  timeout: 2000

# Maintenance settings
maintenance:
  # Kick players when enabling
  kick-on-enable: true
  kick-delay: 5  # seconds
  
  # Kick message (supports MiniMessage)
  kick-message: |
    <red><bold>Server Under Maintenance</bold></red>
    
    <gray>We're currently performing maintenance.</gray>
    <gray>Please check back later!</gray>
    
    <yellow>Estimated time: <white>{duration}</white></yellow>
  
  # Server list (MOTD)
  motd:
    enabled: true
    line1: '<red><bold>⚠ MAINTENANCE MODE ⚠</bold></red>'
    line2: '<gray>Scheduled maintenance in progress</gray>'
  
  # Version text
  version:
    enabled: true
    text: '<red>Maintenance</red>'
  
  # Max players display
  max-players:
    enabled: true
    value: 0
  
  # Server icon
  icon:
    enabled: false
    path: 'maintenance-icon.png'
  
  # Bypass join message
  bypass-join-message: |
    <green><bold>✓</bold> You have bypass permission!</green>
    <gray>Server is in maintenance mode</gray>

# Timer settings
timer:
  # Warning intervals (seconds before start)
  warnings: [300, 180, 60, 30, 10, 5, 3, 2, 1]
  
  # Warning message
  warning-message: |
    <yellow><bold>⚠ Maintenance Alert</bold></yellow>
    <gray>Server maintenance starts in <white>{time}</white></gray>
  
  # Title notifications
  title:
    enabled: true
    fade-in: 10
    stay: 40
    fade-out: 10
    title: '<red><bold>MAINTENANCE</bold></red>'
    subtitle: '<yellow>Starts in {time}</yellow>'
  
  # Action bar countdown
  actionbar:
    enabled: true
    text: '<yellow>⏰ Maintenance in {time}</yellow>'
  
  # Boss bar
  bossbar:
    enabled: true
    color: 'RED'  # BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW
    style: 'SOLID'  # SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20
    text: '<red>Maintenance starting in {time}</red>'
  
  # Sound effect
  sound:
    enabled: true
    type: 'BLOCK_NOTE_BLOCK_PLING'
    volume: 1.0
    pitch: 1.0

# Messages
messages:
  prefix: '<gradient:red:yellow>[Maintenance]</gradient>'
  
  enabled: '<green>Maintenance <bold>ENABLED</bold></green>'
  disabled: '<green>Maintenance <bold>DISABLED</bold></green>'
  
  whitelist-added: '<green>Added <white>{player}</white> to whitelist</green>'
  whitelist-removed: '<green>Removed <white>{player}</white> from whitelist</green>'
  whitelist-cleared: '<red>Whitelist <bold>CLEARED</bold></red>'
  
  timer-scheduled: '<green>Maintenance scheduled for <white>{time}</white></green>'
  timer-cancelled: '<red>Scheduled maintenance <bold>CANCELLED</bold></red>'
  
  no-permission: '<red>You don\'t have permission to do that!</red>'
  player-not-found: '<red>Player <white>{player}</white> not found!</red>'
  already-enabled: '<yellow>Maintenance is already enabled</yellow>'
  already-disabled: '<yellow>Maintenance is already disabled</yellow>'
  
  reload-success: '<green>Configuration <bold>RELOADED</bold></green>'
  reload-failed: '<red>Failed to reload configuration!</red>'

# bStats metrics
bstats:
  enabled: true

# Debug mode
debug: false
```

---

## 🏷️ Placeholders

### PlaceholderAPI (Paper Only)

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%maintenance_status%` | Status text | `Enabled` / `Disabled` |
| `%maintenance_status_colored%` | Colored status | `§cEnabled` / `§aDisabled` |
| `%maintenance_status_symbol%` | Status symbol | `✗` / `✓` |
| `%maintenance_enabled%` | Boolean | `true` / `false` |
| `%maintenance_mode%` | Current mode | `GLOBAL` / `SCHEDULED` |
| `%maintenance_reason%` | Maintenance reason | `Server updates` |
| `%maintenance_duration%` | Current session duration | `15m 30s` |
| `%maintenance_remaining%` | Time until end | `45m 20s` |
| `%maintenance_timer_active%` | Timer active? | `true` / `false` |
| `%maintenance_timer_remaining%` | Timer countdown | `9m 45s` |
| `%maintenance_whitelist_count%` | Whitelist size | `5` |
| `%maintenance_is_whitelisted%` | Player whitelisted? | `true` / `false` |
| `%maintenance_can_bypass%` | Can bypass? | `true` / `false` |
| `%maintenance_sessions_total%` | Total sessions | `42` |
| `%maintenance_kicks_total%` | Total kicks | `156` |
| `%maintenance_blocks_total%` | Blocked connections | `1,234` |

### Usage Examples

**TAB Plugin:**
```yaml
header:
  - ''
  - '&eServer Status: %maintenance_status_colored%'
  - '%maintenance_remaining%'
  - ''
```

**FeatherBoard:**
```yaml
maintenance-board:
  title: '&c&lMAINTENANCE'
  lines:
    - '&7Status: %maintenance_status_colored%'
    - '&7Duration: %maintenance_duration%'
    - '&7Remaining: %maintenance_remaining%'
```

**DeluxeChat:**
```yaml
formats:
  default:
    format: '[%maintenance_status_symbol%] {player}: {message}'
```

---

## 💻 API Usage

### Getting Started

#### Add Dependency

**Maven:**
```xml
<repositories>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>me.d4vide106</groupId>
        <artifactId>maintenance-api</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

**Gradle:**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'me.d4vide106:maintenance-api:1.0.0'
}
```

---

### Basic Usage

```java
import me.d4vide106.maintenance.api.MaintenanceAPI;
import me.d4vide106.maintenance.api.MaintenanceMode;
import me.d4vide106.maintenance.api.MaintenanceStats;
import me.d4vide106.maintenance.api.WhitelistedPlayer;

import java.time.Duration;
import java.util.UUID;
import java.util.List;

public class MyPlugin {
    
    private MaintenanceAPI api;
    
    public void onEnable() {
        // Get API instance
        api = MaintenanceAPI.getInstance();
        
        // Check status
        if (api.isMaintenanceEnabled()) {
            System.out.println("Maintenance is active!");
            System.out.println("Mode: " + api.getMaintenanceMode());
            System.out.println("Reason: " + api.getMaintenanceReason());
        }
    }
}
```

---

### Enable/Disable Maintenance

```java
// Enable maintenance
api.enableMaintenance(
    MaintenanceMode.GLOBAL,
    "Server updates"
).thenAccept(success -> {
    if (success) {
        System.out.println("Maintenance enabled!");
    }
});

// Disable maintenance
api.disableMaintenance().thenAccept(success -> {
    if (success) {
        System.out.println("Maintenance disabled!");
    }
});
```

---

### Whitelist Management

```java
UUID playerUUID = player.getUniqueId();
String playerName = player.getName();

// Check if whitelisted
if (api.isWhitelisted(playerUUID)) {
    System.out.println("Player is whitelisted!");
}

// Add to whitelist
api.addToWhitelist(
    playerUUID,
    playerName,
    "VIP Access"
).thenAccept(success -> {
    System.out.println("Added to whitelist: " + success);
});

// Remove from whitelist
api.removeFromWhitelist(playerUUID).thenAccept(success -> {
    System.out.println("Removed from whitelist: " + success);
});

// Get all whitelisted players
List<WhitelistedPlayer> whitelisted = api.getWhitelistedPlayers();
for (WhitelistedPlayer wp : whitelisted) {
    System.out.println(wp.getName() + " - " + wp.getReason());
}

// Clear whitelist
api.clearWhitelist().thenRun(() -> {
    System.out.println("Whitelist cleared!");
});
```

---

### Timer Scheduling

```java
// Schedule maintenance
api.scheduleTimer(
    Duration.ofMinutes(10),  // Start in 10 minutes
    Duration.ofHours(1)      // Last 1 hour
).thenAccept(success -> {
    if (success) {
        System.out.println("Maintenance scheduled!");
    }
});

// Check timer status
if (api.isTimerActive()) {
    Duration remaining = api.getRemainingTime();
    System.out.println("Time remaining: " + remaining.toMinutes() + "m");
}

// Cancel timer
api.cancelTimer().thenAccept(success -> {
    System.out.println("Timer cancelled: " + success);
});
```

---

### Statistics

```java
api.getStats().thenAccept(stats -> {
    System.out.println("=== Statistics ===");
    System.out.println("Total Sessions: " + stats.getTotalSessions());
    System.out.println("Total Duration: " + stats.getTotalDuration().toHours() + " hours");
    System.out.println("Players Kicked: " + stats.getPlayersKicked());
    System.out.println("Connections Blocked: " + stats.getConnectionsBlocked());
});
```

---

### API Events (Paper)

```java
import me.d4vide106.maintenance.api.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MaintenanceListener implements Listener {
    
    @EventHandler
    public void onMaintenanceEnabled(MaintenanceEnableEvent event) {
        System.out.println("Maintenance enabled!");
        System.out.println("Mode: " + event.getMode());
        System.out.println("Reason: " + event.getReason());
        System.out.println("Started by: " + event.getInitiator());
        
        // Cancel event to prevent enabling
        // event.setCancelled(true);
    }
    
    @EventHandler
    public void onMaintenanceDisabled(MaintenanceDisableEvent event) {
        System.out.println("Maintenance disabled!");
        System.out.println("Duration: " + event.getDuration());
    }
    
    @EventHandler
    public void onWhitelistAdd(WhitelistAddEvent event) {
        System.out.println("Player added to whitelist: " + event.getPlayerName());
        System.out.println("Added by: " + event.getAddedBy());
    }
    
    @EventHandler
    public void onWhitelistRemove(WhitelistRemoveEvent event) {
        System.out.println("Player removed from whitelist: " + event.getPlayerUUID());
    }
    
    @EventHandler
    public void onTimerScheduled(TimerScheduleEvent event) {
        System.out.println("Timer scheduled!");
        System.out.println("Start delay: " + event.getStartDelay());
        System.out.println("Duration: " + event.getDuration());
    }
}
```

---

## 🗄️ Database

### SQLite (Default)

**Location:** `plugins/MaintenanceUniversal/maintenance.db`

**Pros:**
- ✅ No setup required
- ✅ Portable
- ✅ Fast for single servers
- ✅ No external dependencies

**Cons:**
- ❌ Single server only
- ❌ Limited concurrent access

**Best for:** Single servers, testing, small networks

---

### MySQL/MariaDB

**Configuration:**
```yaml
database:
  type: 'mysql'
  host: 'localhost'
  port: 3306
  database: 'maintenance'
  username: 'minecraft'
  password: 'secure_password'
  pool-size: 10
```

**Pros:**
- ✅ Multi-server support
- ✅ Better performance at scale
- ✅ Network-wide data sharing
- ✅ Professional grade

**Cons:**
- ❌ Requires external database
- ❌ Additional configuration

**Best for:** Networks, production servers, large servers

---

### PostgreSQL

**Status:** 🚧 Coming in v1.1.0

**Configuration:**
```yaml
database:
  type: 'postgresql'
  host: 'localhost'
  port: 5432
  database: 'maintenance'
  username: 'postgres'
  password: 'secure_password'
```

---

### Database Schema

**Tables:**

1. **maintenance_settings**
   - Key-value configuration storage
   - Maintenance state, mode, reason

2. **maintenance_whitelist**
   - Whitelisted player data
   - UUID, name, reason, timestamp, added_by

3. **maintenance_stats**
   - Statistical counters
   - Sessions, kicks, blocks, duration

4. **maintenance_history**
   - Historical session data
   - Start time, end time, duration, mode, reason

5. **maintenance_schedule**
   - Scheduled maintenance timers
   - Start time, duration, warnings

---

## 🔄 Redis Multi-Server Sync

### Setup

1. **Install Redis** on your network

2. **Configure** each server:

```yaml
redis:
  enabled: true
  host: 'your-redis-host.com'
  port: 6379
  password: 'your-password'
  database: 0
  channel: 'maintenance'
```

3. **Restart** all servers

---

### What Gets Synced?

| Action | Synced | Real-time |
|--------|:------:|:---------:|
| Maintenance enable/disable | ✅ | ✅ |
| Whitelist add/remove | ✅ | ✅ |
| Whitelist clear | ✅ | ✅ |
| Timer schedule/cancel | ✅ | ✅ |
| Configuration reload | ✅ | ✅ |
| Statistics updates | ✅ | ✅ |

---

### Network Architecture

```
┌────────────────────────────────────┐
│         Redis Server (Pub/Sub)          │
│   Channel: maintenance                  │
└───────┬─────────┬─────────┬─────────┘
       │         │         │         │
       │         │         │         │
   ┌───┴───┐ ┌───┴───┐ ┌───┴───┐ ┌───┴───┐
   │Lobby 1│ │Lobby 2│ │Survival│ │Creative│
   └────────┘ └────────┘ └────────┘ └────────┘
```

**Benefits:**
- 🚀 Instant synchronization (< 100ms)
- 🌐 Network-wide consistency
- ⚡ Scales to unlimited servers
- 🔒 Centralized control
- 📄 Shared whitelist across network

---

### Proxy-Level Maintenance (Velocity)

For proxy-level maintenance, install on your Velocity proxy:

**Benefits:**
- Block connections before reaching backend servers
- Network-wide maintenance with single command
- Reduced backend server load
- Centralized management

**Setup:**
```yaml
# Velocity: plugins/MaintenanceUniversal/config.yml
maintenance:
  fallback-server: 'maintenance-server'  # Optional maintenance lobby
  mode: 'proxy'  # proxy or mixed
```

---

## 🌍 Platform-Specific Guides

### Paper/Spigot/Purpur/Folia

**Installation:** Place JAR in `plugins/` folder

**Config Location:** `plugins/MaintenanceUniversal/config.yml`

**Features:**
- Full PlaceholderAPI integration
- ProtocolLib MOTD manipulation
- bStats metrics
- Legacy 1.13+ support
- Folia regionized threading

**Optional Dependencies:**
- PlaceholderAPI 2.11.6+
- ProtocolLib 5.4.0+

---

### Velocity

**Installation:** Place JAR in `plugins/` folder

**Config Location:** `plugins/MaintenanceUniversal/config.yml`

**Features:**
- Proxy-level maintenance
- Player routing to maintenance server
- Network-wide control
- Redis sync with backend servers

**Special Config:**
```yaml
maintenance:
  mode: 'proxy'
  fallback-server: 'maintenance'
  kick-to-fallback: true
```

---

### Fabric

**Installation:** Place JAR in `mods/` folder

**Dependencies:** Fabric API

**Config Location:** `config/maintenance-universal/config.toml`

**Features:**
- Client-side notifications
- Server-side enforcement
- Mod integration API
- Cloth Config GUI (optional)

---

### Forge/NeoForge

**Installation:** Place JAR in `mods/` folder

**Config Location:** `config/maintenance-universal/config.toml`

**Features:**
- Forge event system
- Config screen GUI
- Mod compatibility layer
- NeoForge support

---

## ❓ FAQ

### General

**Q: How do I allow specific players during maintenance?**  
A: Use `/maintenance whitelist add <player>` or give them `maintenance.bypass` permission.

**Q: Can I customize messages?**  
A: Yes! Edit `messages` section in config.yml. Supports MiniMessage format.

**Q: Does this work across multiple servers?**  
A: Yes! Enable Redis in config for network-wide synchronization.

**Q: Can I schedule recurring maintenance?**  
A: Use a scheduler plugin (CommandScheduler, CronScheduler) to run schedule commands.

**Q: What happens if Redis goes down?**  
A: Plugin continues working locally. Sync resumes when Redis reconnects.

---

### Platform-Specific

**Q: Do I need both Velocity and Paper plugins?**  
A: No. Use Velocity for proxy-level OR Paper for server-level. Use both for maximum control.

**Q: Does this work with BungeeCord?**  
A: Use Velocity module - it's compatible with BungeeCord networks.

**Q: Can I use this on Folia?**  
A: Yes! Full Folia support with regionized threading compatibility.

**Q: Does Fabric version require server-side?**  
A: Works both client-side (notifications) and server-side (enforcement).

---

### Technical

**Q: How do I migrate from SQLite to MySQL?**  
A: Export SQLite data, import to MySQL, update config, restart server.

**Q: Can I use this with other maintenance plugins?**  
A: Yes, but disable conflicting features. API allows integration.

**Q: Where are statistics stored?**  
A: In database tables. Use `/maintenance stats` or API to retrieve.

**Q: How do I backup my data?**  
A: Backup database file (SQLite) or dump MySQL database regularly.

---

## 🛠️ Troubleshooting

### Plugin won't load

1. Check Java version (requires 17+)
2. Check server/proxy version compatibility
3. Review startup logs for errors
4. Verify file permissions
5. Check for conflicting plugins

### Database connection failed

1. Verify credentials in config
2. Check if database exists
3. Test network connectivity
4. Review database logs
5. Check firewall rules

### Redis sync not working

1. Verify Redis is running
2. Check credentials and host
3. Ensure all servers use same channel
4. Check Redis logs
5. Test with `redis-cli PING`

### Commands not working

1. Check permissions
2. Verify command isn't disabled
3. Look for plugin conflicts
4. Check console for errors
5. Try `/maintenance help`

---

## 📧 Support

- **🐛 GitHub Issues:** [Report bugs](https://github.com/D4vide106/MaintenanceUniversal/issues)
- **💬 Discussions:** [Ask questions](https://github.com/D4vide106/MaintenanceUniversal/discussions)
- **📖 Wiki:** [Full documentation](https://github.com/D4vide106/MaintenanceUniversal/wiki)
- **📨 Discord:** [Community server](https://discord.gg/example)

---

## 📄 License

MaintenanceUniversal is licensed under the **MIT License**.

See [LICENSE](LICENSE) for details.

---

## 🙏 Credits

**Author:** [D4vide106](https://github.com/D4vide106)

**Contributors:** [View all](https://github.com/D4vide106/MaintenanceUniversal/graphs/contributors)

**Built with:**
- Paper API / Spigot API
- Velocity API
- Fabric API
- Forge/NeoForge
- HikariCP
- Configurate
- Jedis (Redis client)
- Adventure API
- bStats

---

**Made with ❤️ for the Minecraft community**

**Star ⭐ this repo if you find it useful!**
