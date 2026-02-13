# 🔨 Build Guide - MaintenanceUniversal

## 🌟 NEW: Universal JAR System

**One JAR to rule them all!** ✨

The Universal JAR automatically detects your platform (Paper, Spigot, Velocity, BungeeCord, etc.) and loads the correct implementation.

### 🎯 Choose Your Build Strategy

| Build | JARs | Size | Use Case |
|-------|------|------|----------|
| **Universal** (⭐ Recommended) | 1 JAR | ~6 MB | Simplicity, works everywhere |
| **Individual** | 3 JARs | ~2-3 MB each | Smaller size, specific platforms |
| **All** | 4 JARs | Universal + 3 singles | Best of both worlds |

---

## 🚀 Quick Start

### Option 1: Universal JAR (Recommended) ⭐

```bash
./gradlew buildUniversal
```

**Output:**
```
📦 universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar
```

**Works on:**
- ✅ Paper 1.13-1.21+
- ✅ Spigot 1.13-1.21+
- ✅ Purpur 1.13-1.21+
- ✅ Folia 1.19.4+
- ✅ Velocity 3.0+
- ✅ BungeeCord (latest)
- ✅ Waterfall (all versions)

**How it works:**
1. Copy JAR to `plugins/` folder
2. Start server/proxy
3. Plugin auto-detects platform
4. Loads correct implementation
5. ✨ Magic!

---

### Option 2: Individual JARs (Smaller Size)

```bash
# Server only
./gradlew buildServer

# Proxy only  
./gradlew buildProxy
```

**Output:**
```
📦 paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar (~2.5 MB)
📦 velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar (~2.2 MB)
📦 bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar (~2.3 MB)
```

---

### Option 3: Everything (Universal + Singles)

```bash
./gradlew buildAll
```

**Output:**
```
📦 universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar
📦 paper/build/libs/MaintenanceUniversal-Paper-1.0.0.jar
📦 velocity/build/libs/MaintenanceUniversal-Velocity-1.0.0.jar
📦 bungee/build/libs/MaintenanceUniversal-BungeeCord-1.0.0.jar
```

---

## 📦 Module Status

### ✅ Complete & Ready

| Module | Status | Platforms | JAR Output |
|--------|:------:|-----------|------------|
| **Universal** | ✅ Complete | ALL platforms | `MaintenanceUniversal-Universal-1.0.0.jar` |
| **Paper** | ✅ Complete | Paper, Spigot, Purpur, Folia, CraftBukkit | `MaintenanceUniversal-Paper-1.0.0.jar` |
| **Velocity** | ✅ Complete | Velocity 3.0+ | `MaintenanceUniversal-Velocity-1.0.0.jar` |
| **BungeeCord** | ✅ Complete | BungeeCord, Waterfall | `MaintenanceUniversal-BungeeCord-1.0.0.jar` |

### ⏳ In Development

| Module | Status | Platforms |
|--------|:------:|------------|
| **Fabric** | 🚧 Planned | Fabric, Quilt |
| **Forge** | 🚧 Planned | Forge, NeoForge |

---

## 🎯 Universal JAR Deep Dive

### How Auto-Detection Works

```java
1. Plugin loads
2. PlatformDetector scans classpath:
   - Found Paper API? → Load Paper implementation
   - Found Velocity API? → Load Velocity implementation
   - Found BungeeCord API? → Load BungeeCord implementation
3. Correct platform loaded automatically
4. Single JAR, multiple platforms!
```

### Detection Order (Most Specific First)

1. **Paper** - `io.papermc.paper.configuration.Configuration`
2. **Spigot** - `org.spigotmc.SpigotConfig`
3. **Bukkit** - `org.bukkit.Bukkit` (CraftBukkit, etc.)
4. **Velocity** - `com.velocitypowered.api.proxy.ProxyServer`
5. **BungeeCord** - `net.md_5.bungee.api.ProxyServer` (+ Waterfall)

### Included Implementations

```
Universal JAR Contents:
├── Common module (shared code)
├── Paper implementation
├── Velocity implementation
├── BungeeCord implementation
├── PlatformDetector (bootstrap)
└── All plugin descriptors:
    ├── plugin.yml (Bukkit)
    ├── velocity-plugin.json (Velocity)
    └── bungee.yml (BungeeCord)
```

---

## 📊 JAR Comparison

### Universal JAR

**Pros:**
- ✅ One file for all platforms
- ✅ Auto-detection
- ✅ Simplified deployment
- ✅ No confusion about which JAR
- ✅ Future-proof

**Cons:**
- ⚠️ Larger file size (~6 MB)
- ⚠️ Includes unused platform code

**Best for:**
- Networks with mixed platforms
- Easy deployment
- Users who want simplicity

---

### Individual JARs

**Pros:**
- ✅ Smaller size (~2-3 MB each)
- ✅ Only includes needed code
- ✅ Faster loading

**Cons:**
- ⚠️ Must choose correct JAR
- ⚠️ Multiple files to manage

**Best for:**
- Single platform setup
- Minimal file size
- Advanced users

---

## 🔧 Build Commands Reference

### Main Commands

| Command | Output | Use Case |
|---------|--------|----------|
| `./gradlew buildUniversal` | 1 Universal JAR | ⭐ Recommended |
| `./gradlew buildAll` | Universal + 3 singles | Release builds |
| `./gradlew buildServer` | Paper JAR | Server only |
| `./gradlew buildProxy` | Velocity + Bungee | Proxy only |

### Maintenance Commands

| Command | Description |
|---------|-------------|
| `./gradlew cleanAll` | Clean all build directories |
| `./gradlew :universal:shadowJar` | Build only Universal |
| `./gradlew :paper:shadowJar` | Build only Paper |
| `./gradlew :velocity:shadowJar` | Build only Velocity |
| `./gradlew :bungee:shadowJar` | Build only BungeeCord |

---

## 🎨 Example Build Output

```bash
$ ./gradlew buildUniversal

> Task :common:compileJava
> Task :paper:compileJava
> Task :velocity:compileJava
> Task :bungee:compileJava
> Task :universal:shadowJar

════════════════════════════════════════════════════════════
  ✅ Universal JAR Built!
════════════════════════════════════════════════════════════

  🌐 Universal JAR (ALL platforms):
     universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar

  ✅ Works on:
     - Paper 1.13+
     - Spigot 1.13+
     - Purpur 1.13+
     - Folia 1.19.4+
     - Velocity 3.0+
     - BungeeCord (latest)
     - Waterfall (all versions)

  💡 Auto-detects platform and loads correct implementation!

════════════════════════════════════════════════════════════

BUILD SUCCESSFUL in 24s
```

---

## 🧪 Testing Universal JAR

### Test on Paper

```bash
# Copy to Paper server
cp universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar \
   ~/servers/paper/plugins/

# Start server
cd ~/servers/paper
java -jar paper.jar

# Check logs:
# [MaintenanceUniversal] Platform: Paper
# [MaintenanceUniversal] ✅ Loaded Paper implementation
```

### Test on Velocity

```bash
# Copy to Velocity proxy
cp universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar \
   ~/proxies/velocity/plugins/

# Start proxy
cd ~/proxies/velocity
java -jar velocity.jar

# Check logs:
# [MaintenanceUniversal] Platform: Velocity
# [MaintenanceUniversal] ✅ Loaded Velocity implementation
```

### Test on BungeeCord

```bash
# Copy to BungeeCord proxy
cp universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar \
   ~/proxies/bungee/plugins/

# Start proxy
cd ~/proxies/bungee
java -jar BungeeCord.jar

# Check logs:
# [MaintenanceUniversal] Platform: BungeeCord
# [MaintenanceUniversal] ✅ Loaded BungeeCord implementation
```

---

## 🐛 Troubleshooting Universal JAR

### "Unsupported platform detected"

**Cause:** Platform not recognized  
**Fix:** Make sure you're using Paper/Spigot/Velocity/BungeeCord

```bash
# Check server version
java -jar server.jar --version
```

---

### "Failed to load platform implementation"

**Cause:** Missing dependencies or corrupted JAR  
**Fix:** Rebuild and re-download

```bash
./gradlew cleanAll
./gradlew buildUniversal
```

---

### JAR too large

**Cause:** Universal JAR includes all platforms  
**Solution:** Use individual JARs instead

```bash
# For Paper servers
./gradlew buildServer

# For Velocity/BungeeCord proxies
./gradlew buildProxy
```

---

## 📈 Build Statistics

### File Sizes

| JAR | Size | Platforms Included |
|-----|------|-------------------|
| **Universal** | ~6.0 MB | Paper + Velocity + BungeeCord |
| **Paper** | ~2.5 MB | Paper only |
| **Velocity** | ~2.2 MB | Velocity only |
| **BungeeCord** | ~2.3 MB | BungeeCord only |

### Build Times (Approximate)

| Command | Duration | Output |
|---------|----------|--------|
| `buildUniversal` | ~35s | 1 JAR |
| `buildAll` | ~45s | 4 JARs |
| `buildServer` | ~15s | 1 JAR |
| `buildProxy` | ~20s | 2 JARs |

---

## 🎯 Recommendation

### For Most Users: Universal JAR ⭐

```bash
./gradlew buildUniversal
```

**Why?**
- ✅ Works everywhere
- ✅ No confusion
- ✅ Future-proof
- ✅ Easy deployment
- ⚠️ Slightly larger (~6 MB vs 2-3 MB)

---

### For Advanced Users: Individual JARs

```bash
./gradlew buildAll
```

**Use individual JARs when:**
- You know your exact platform
- File size matters
- You want minimal overhead

---

## 🚀 CI/CD with Universal JAR

### GitHub Actions

```yaml
name: Build Universal

on:
  push:
    branches: [ main ]
  release:
    types: [ created ]

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
    
    - name: Build Universal JAR
      run: ./gradlew buildUniversal
    
    - name: Upload Universal JAR
      uses: actions/upload-artifact@v4
      with:
        name: MaintenanceUniversal-Universal
        path: universal/build/libs/MaintenanceUniversal-Universal-1.0.0.jar
```

---

## 📚 Additional Resources

- **[Main README](README.md)** - Overview and features
- **[Paper Guide](paper/README.md)** - Paper-specific docs
- **[Velocity Guide](velocity/README.md)** - Velocity-specific docs
- **[API Documentation](api/README.md)** - Developer API

---

## 📧 Support

- 🐛 [Report Issues](https://github.com/D4vide106/MaintenanceUniversal/issues)
- 💬 [Discussions](https://github.com/D4vide106/MaintenanceUniversal/discussions)
- 📖 [Wiki](https://github.com/D4vide106/MaintenanceUniversal/wiki)

---

**Built with ❤️ using Gradle 8 and Java 17**

**Universal JAR = One File, All Platforms! ✨**
