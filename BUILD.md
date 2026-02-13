# 🔨 Build Guide - MaintenanceUniversal

## 📦 Completed Modules

### ✅ Ready to Build

| Module | Status | Platforms | JAR Output |
|--------|:------:|-----------|------------|
| **Paper** | ✅ Complete | Paper, Spigot, Purpur, Folia, CraftBukkit | `MaintenanceUniversal-Paper-1.0.0.jar` |
| **Velocity** | ✅ Complete | Velocity 3.0+ | `MaintenanceUniversal-Velocity-1.0.0.jar` |
| **BungeeCord** | ✅ Complete | BungeeCord, Waterfall | `MaintenanceUniversal-BungeeCord-1.0.0.jar` |

### ⏳ In Development

| Module | Status | Platforms |
|--------|:------:|------------|
| **Fabric** | 🚧 Planned | Fabric, Quilt |
| **Forge** | 🚧 Planned | Forge, NeoForge |

---

## 🚀 Quick Start

### Requirements

- **Java 17+** (JDK)
- **Gradle 8.0+** (included via wrapper)
- **Git** (for cloning)

### Build All Modules

```bash
# Clone repository
git clone https://github.com/D4vide106/MaintenanceUniversal.git
cd MaintenanceUniversal

# Build everything
./gradlew buildAll
```

**Output:**
```
📦 paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar
📦 velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar
📦 bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar
```

---

## 🎯 Build Specific Modules

### Server Module (Paper)

```bash
./gradlew buildServer
```

**Compatible with:**
- ✅ Paper 1.13-1.21+
- ✅ Spigot 1.13-1.21+
- ✅ Purpur 1.13-1.21+
- ✅ Folia 1.19.4+
- ⚠️ CraftBukkit 1.13+ (limited features)

---

### Proxy Modules (Velocity + BungeeCord)

```bash
./gradlew buildProxy
```

**Velocity JAR compatible with:**
- ✅ Velocity 3.0.0+

**BungeeCord JAR compatible with:**
- ✅ BungeeCord (latest builds)
- ✅ Waterfall (all versions)

---

## 📋 Module Details

### Paper Module

**Build:**
```bash
./gradlew :paper:shadowJar
```

**Features:**
- 🔐 Maintenance mode with whitelist
- 🎨 Custom MOTD via ProtocolLib (optional)
- 📊 PlaceholderAPI support (optional)
- ⏰ Timer system with scheduling
- 🔄 Redis multi-server sync
- 📈 Statistics tracking
- 🌐 Folia regionized threading support

**Location:** `paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar`

---

### Velocity Module

**Build:**
```bash
./gradlew :velocity:shadowJar
```

**Features:**
- 🌐 Proxy-level maintenance
- 🔄 Redis multi-proxy sync
- 🎯 Fallback server support
- 🎨 Custom MOTD (native API)
- 🔐 Permission-based bypass
- 📊 Statistics tracking

**Location:** `velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar`

---

### BungeeCord Module

**Build:**
```bash
./gradlew :bungee:shadowJar
```

**Features:**
- 🌐 Proxy-level maintenance
- 🔄 Redis multi-proxy sync
- 🎯 Fallback server support
- 🎨 Custom MOTD (Adventure platform)
- 🔐 Permission-based bypass
- 📊 Statistics tracking

**Location:** `bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar`

---

## 🔧 Advanced Build Options

### Clean Build

```bash
# Clean all modules
./gradlew cleanAll

# Clean specific module
./gradlew :paper:clean
./gradlew :velocity:clean
./gradlew :bungee:clean
```

---

### Build Without Tests

```bash
./gradlew buildAll -x test
```

---

### Build with Debug Info

```bash
./gradlew buildAll --info
```

---

### Build Common Module

```bash
./gradlew :common:build
```

The common module contains shared code used by all platforms.

---

## 📊 Module Compatibility Matrix

### Paper JAR

| Platform | Version | Features | Status |
|----------|---------|----------|:------:|
| Paper | 1.13-1.21+ | Full | ✅ |
| Spigot | 1.13-1.21+ | Full (no Adventure native) | ✅ |
| Purpur | 1.13-1.21+ | Full + Purpur extras | ✅ |
| Folia | 1.19.4+ | Full + regionized | ✅ |
| CraftBukkit | 1.13-1.21+ | Basic (limited API) | ⚠️ |

### Velocity JAR

| Platform | Version | Features | Status |
|----------|---------|----------|:------:|
| Velocity | 3.0.0+ | Full | ✅ |

### BungeeCord JAR

| Platform | Version | Features | Status |
|----------|---------|----------|:------:|
| BungeeCord | Latest | Full | ✅ |
| Waterfall | All | Full | ✅ |

---

## 🐛 Troubleshooting

### Gradle Daemon Issues

```bash
./gradlew --stop
./gradlew buildAll
```

### Permission Denied

```bash
chmod +x gradlew
./gradlew buildAll
```

### Java Version Issues

```bash
# Check Java version
java -version

# Should be 17 or higher
# Download from: https://adoptium.net/
```

### Build Failures

```bash
# Clean and rebuild
./gradlew cleanAll
./gradlew buildAll --refresh-dependencies
```

---

## 📦 Dependency Information

### Common Dependencies (All Modules)

- **Configurate** 4.1.2 - YAML configuration
- **Jedis** 5.1.0 - Redis client
- **HikariCP** 5.1.0 - Connection pooling
- **SQLite JDBC** 3.45.1.0 - SQLite database

### Paper-Specific

- **Paper API** 1.20.4-R0.1-SNAPSHOT
- **Adventure API** (included in Paper)
- **PlaceholderAPI** 2.11.6 (optional, runtime)
- **ProtocolLib** 5.4.0 (optional, runtime)

### Velocity-Specific

- **Velocity API** 3.3.0-SNAPSHOT
- **Adventure API** 4.16.0 (included in Velocity)

### BungeeCord-Specific

- **BungeeCord API** 1.20-R0.2
- **Adventure Platform BungeeCord** 4.3.2

---

## 🚀 CI/CD Integration

### GitHub Actions Example

```yaml
name: Build

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Gradle
      run: ./gradlew buildAll
    
    - name: Upload artifacts
      uses: actions/upload-artifact@v4
      with:
        name: plugins
        path: |
          paper/build/libs/*.jar
          velocity/build/libs/*.jar
          bungee/build/libs/*.jar
```

---

## 📈 Build Statistics

### Average Build Times

| Command | Duration | Output |
|---------|----------|--------|
| `./gradlew buildAll` | ~30s | 3 JARs |
| `./gradlew buildServer` | ~15s | 1 JAR |
| `./gradlew buildProxy` | ~20s | 2 JARs |
| `./gradlew cleanAll buildAll` | ~45s | 3 JARs |

### JAR Sizes (Approximate)

| JAR | Size | Dependencies Included |
|-----|------|----------------------|
| Paper | ~2.5 MB | Common + Paper platform |
| Velocity | ~2.2 MB | Common + Velocity platform |
| BungeeCord | ~2.3 MB | Common + BungeeCord platform + Adventure |

---

## 🎯 Next Steps

After building:

1. **Test locally:**
   - Copy JAR to server/proxy `plugins/` folder
   - Start server/proxy
   - Check logs for successful load

2. **Configure:**
   - Edit `plugins/MaintenanceUniversal/config.yml`
   - Set database, Redis, and maintenance options
   - Reload with `/maintenance reload`

3. **Deploy:**
   - Upload to production servers
   - Configure Redis for multi-server sync
   - Test maintenance mode

---

## 📧 Support

If you encounter build issues:

- 🐛 [Report Issues](https://github.com/D4vide106/MaintenanceUniversal/issues)
- 💬 [Discussions](https://github.com/D4vide106/MaintenanceUniversal/discussions)
- 📖 [Wiki](https://github.com/D4vide106/MaintenanceUniversal/wiki)

---

**Built with ❤️ using Gradle 8 and Java 17**
