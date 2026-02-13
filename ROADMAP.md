# 🗺️ Maintenance Universal - Development Roadmap

## 🎯 Project Vision

Creare il **plugin di manutenzione più completo e professionale** disponibile per Minecraft, con supporto multi-piattaforma (Paper, Velocity, Fabric, Forge) e compatibilità estesa dalle versioni legacy alle più recenti.

---

## 📅 Timeline Overview

```
Fase 1: Foundation          [████████░░] 80% - 2 settimane
Fase 2: Paper Implementation [████░░░░░░] 40% - 3 settimane  
Fase 3: Velocity Support     [░░░░░░░░░░]  0% - 2 settimane
Fase 4: Fabric/Forge         [░░░░░░░░░░]  0% - 3 settimane
Fase 5: Testing & Polish     [░░░░░░░░░░]  0% - 2 settimane
Fase 6: Release              [░░░░░░░░░░]  0% - 1 settimana

Tempo totale stimato: 13 settimane (~3 mesi)
```

---

## 🛣️ Fase 1: Foundation (CORRENTE)

### ✅ Completato

- [x] Struttura progetto con Gradle multi-modulo
- [x] Modulo Common con API completa
- [x] Sistema eventi completo (10 eventi)
- [x] Sistema configurazione YAML avanzato
- [x] Utilities (TimeUtil, ComponentUtil, ValidationUtil)
- [x] Modelli dati (WhitelistedPlayer, MaintenanceStats, MaintenanceMode)
- [x] README professionale con documentazione
- [x] GitHub Actions CI/CD
- [x] Licenza MIT
- [x] .gitignore completo

### 🔧 In Progress

- [ ] **Database Layer** (Common)
  - [ ] Interfaccia DatabaseProvider
  - [ ] Implementazione SQLite
  - [ ] Implementazione MySQL/MariaDB
  - [ ] Implementazione PostgreSQL
  - [ ] HikariCP connection pooling
  - [ ] Migration system per aggiornamenti schema
  - [ ] Query builder per sicurezza SQL injection

- [ ] **Redis Integration** (Common)
  - [ ] RedisManager per pub/sub
  - [ ] Serializzazione messaggi cross-server
  - [ ] Auto-reconnect e failover
  - [ ] Pool connections Jedis

- [ ] **Manager Classes** (Common)
  - [ ] MaintenanceManager (logica core)
  - [ ] WhitelistManager (gestione whitelist)
  - [ ] TimerManager (scheduling)
  - [ ] NotificationManager (broadcast)
  - [ ] StatisticsManager (tracking metriche)

### 📅 Timeline: Settimane 1-2

---

## 📜 Fase 2: Paper Implementation

### 🎯 Obiettivi

Implementazione completa per Paper/Spigot con supporto versioni esteso.

### 📦 Versioni Supportate

#### Tier 1: Modern (Priorità Alta)
- **1.20.x** - Latest (1.20.1 → 1.20.6) ⭐
- **1.19.x** - Previous major (1.19 → 1.19.4)
- **1.18.x** - LTS Support (1.18 → 1.18.2)
- **1.17.x** - Cave Update (1.17 → 1.17.1)
- **1.16.x** - Nether Update (1.16.1 → 1.16.5)

**Features**: Tutti i feature completi (Adventure API, MiniMessage, ProtocolLib)

#### Tier 2: Stable (Priorità Media)
- **1.15.x** - Bee Update (1.15 → 1.15.2)
- **1.14.x** - Village & Pillage (1.14 → 1.14.4)
- **1.13.x** - Update Aquatic (1.13 → 1.13.2) ⚠️

**Features**: Feature completi ma senza alcune librerie moderne (fallback a legacy color codes)

#### Tier 3: Legacy (Priorità Bassa)
- **1.12.x** - World of Color (1.12 → 1.12.2)
- **1.11.x** - Exploration Update (1.11 → 1.11.2)
- **1.10.x** - Frostburn (1.10 → 1.10.2)
- **1.9.x** - Combat Update (1.9 → 1.9.4)
- **1.8.x** - Bountiful Update (1.8 → 1.8.9) ⚠️
- **1.7.10** - Ultima versione 1.7 ⚠️

**Features**: Solo feature base (no Adventure API, no MiniMessage, no ProtocolLib)

### 💻 Tasks

#### Core Implementation
- [ ] MaintenancePaper main class
- [ ] Bukkit event listeners
- [ ] Command system (Bukkit commands)
- [ ] Permission integration
- [ ] PlaceholderAPI expansion
- [ ] ProtocolLib integration (MOTD/ping)

#### Version Compatibility
- [ ] **Abstraction Layer** per differenze API tra versioni
  - [ ] ComponentAdapter (Adventure ↔ Legacy)
  - [ ] SoundAdapter (sound names cambiati tra versioni)
  - [ ] MaterialAdapter (material names cambiati)
  - [ ] EntityAdapter (player/entity API differences)
- [ ] **Version Detection** automatico al startup
- [ ] **Reflection utilities** per accesso NMS quando necessario
- [ ] **Testing matrix** per ogni versione Tier 1/2

#### Commands
- [ ] `/maintenance` - Main command
- [ ] `/maintenance enable/disable`
- [ ] `/maintenance schedule <delay> <duration>`
- [ ] `/maintenance whitelist <add|remove|list|clear>`
- [ ] `/maintenance reload`
- [ ] `/maintenance stats`
- [ ] `/maintenance info`
- [ ] Tab completion avanzato

#### Listeners
- [ ] PlayerLoginEvent (blocco connessioni)
- [ ] PlayerJoinEvent (whitelist check)
- [ ] AsyncPlayerPreLoginEvent (async checks)
- [ ] ServerListPingEvent (MOTD customization)
- [ ] PlayerQuitEvent (cleanup)

#### Integrations
- [ ] **PlaceholderAPI**
  - [ ] %maintenance_status%
  - [ ] %maintenance_mode%
  - [ ] %maintenance_remaining%
  - [ ] %maintenance_duration%
  - [ ] %maintenance_is_whitelisted%
  - [ ] 10+ placeholder totali
- [ ] **ProtocolLib** (opzionale)
  - [ ] Modifica packet ServerListPing
  - [ ] Custom protocol version
  - [ ] Hover player list customization
- [ ] **Vault** (opzionale)
  - [ ] Economy integration per costi whitelist
  - [ ] Permission sync

### 📅 Timeline: Settimane 3-5

---

## 🌐 Fase 3: Velocity Support

### 🎯 Obiettivi

Proxy support per reti multi-server con sincronizzazione Redis.

### 📦 Versioni Supportate

- **Velocity 3.3.x** - Latest (API 4.0.0)
- **Velocity 3.2.x** - Previous
- **Velocity 3.1.x** - Older but stable

### 💻 Tasks

#### Core Implementation
- [ ] MaintenanceVelocity main class
- [ ] Velocity event listeners
- [ ] Command system (Velocity commands)
- [ ] Redis sync per multi-server
- [ ] Server-specific maintenance

#### Features Specifiche Velocity
- [ ] **Multi-Server Management**
  - [ ] Enable/disable per server specifico
  - [ ] Lista server in manutenzione
  - [ ] Redirect automatico a lobby
- [ ] **Network-Wide Sync**
  - [ ] Redis pub/sub per sync real-time
  - [ ] Fallback polling se Redis offline
  - [ ] Conflict resolution (ultimo write vince)
- [ ] **Proxy Commands**
  - [ ] `/maintenance server <name> enable`
  - [ ] `/maintenance server <name> disable`
  - [ ] `/maintenance server list`
  - [ ] `/maintenance global enable`

#### Listeners
- [ ] LoginEvent
- [ ] ServerConnectedEvent
- [ ] ServerPreConnectEvent
- [ ] ProxyPingEvent (MOTD)

### 📅 Timeline: Settimane 6-7

---

## 🧵 Fase 4: Fabric & Forge Support

### 🎯 Obiettivi

Supporto mod loader per server modded.

### 📦 Fabric Versioni

#### Modern Fabric
- **1.20.x** (Fabric Loader 0.15.x, Fabric API 0.96.x)
- **1.19.x** (Fabric Loader 0.14.x, Fabric API 0.76.x)
- **1.18.x** (Fabric Loader 0.13.x, Fabric API 0.46.x)

#### Legacy Fabric
- **1.17.x** (Fabric Loader 0.12.x)
- **1.16.x** (Fabric Loader 0.11.x)
- **1.15.x** (Fabric Loader 0.10.x)
- **1.14.x** (Fabric Loader 0.9.x)

### 📦 Forge Versioni

#### NeoForge (Recommended)
- **1.20.4+** (NeoForge 20.4.x+)
- Successore ufficiale di Forge

#### Forge Legacy
- **1.20.1** (Forge 47.x)
- **1.19.x** (Forge 43.x)
- **1.18.x** (Forge 40.x)
- **1.16.x** (Forge 36.x)
- **1.12.2** (Forge 14.23.x) - Versione molto popolare
- **1.7.10** (Forge 10.13.x) - Legacy support

### 💻 Tasks

#### Fabric Implementation
- [ ] fabric.mod.json configuration
- [ ] Fabric API integration
- [ ] Event system (Fabric events)
- [ ] Command registration
- [ ] Config screen integration (Cloth Config)

#### Forge Implementation
- [ ] mods.toml configuration
- [ ] Forge event bus
- [ ] Command registration
- [ ] Config GUI (Forge config screens)
- [ ] NeoForge compatibility layer

#### Shared Mod Features
- [ ] Client-side notifications
- [ ] Integrated server support
- [ ] Single-player world maintenance

### 📅 Timeline: Settimane 8-10

---

## 🧪 Fase 5: Testing & Polish

### 🎯 Obiettivi

Testing completo, bugfixing, ottimizzazioni e polish finale.

### 💻 Tasks

#### Testing
- [ ] **Unit Tests**
  - [ ] Common API tests (JUnit 5)
  - [ ] Manager classes tests
  - [ ] Utility classes tests
  - [ ] Database operations tests
  - [ ] Redis sync tests
  - [ ] Target: 80%+ code coverage

- [ ] **Integration Tests**
  - [ ] Paper tests (MockBukkit)
  - [ ] Velocity tests
  - [ ] Multi-server sync tests
  - [ ] Database migration tests

- [ ] **Manual Testing Matrix**
  ```
  Platform    | Ver 1.20 | Ver 1.16 | Ver 1.13 | Ver 1.12 | Ver 1.7
  ------------|----------|----------|----------|----------|----------
  Paper       |    ✓     |    ✓     |    ✓     |    ✓     |    ✓
  Velocity    |    ✓     |    -     |    -     |    -     |    -
  Fabric      |    ✓     |    ✓     |    -     |    -     |    -
  Forge       |    ✓     |    ✓     |    -     |    ✓     |    ✓
  ```

#### Performance Optimization
- [ ] Profiling con JProfiler/YourKit
- [ ] Cache optimization
- [ ] Database query optimization
- [ ] Async operation review
- [ ] Memory leak detection
- [ ] Thread pool tuning

#### Documentation
- [ ] **Wiki completo**
  - [ ] Installation guide per piattaforma
  - [ ] Configuration guide dettagliata
  - [ ] Commands & permissions reference
  - [ ] API documentation con esempi
  - [ ] Troubleshooting guide
  - [ ] Migration guide da altri plugin
  - [ ] FAQ

- [ ] **JavaDoc**
  - [ ] 100% coverage per API pubblica
  - [ ] Code examples inline
  - [ ] @since tags
  - [ ] @deprecated warnings

- [ ] **Video Tutorials** (YouTube)
  - [ ] Quick start guide (5 min)
  - [ ] Advanced configuration (10 min)
  - [ ] API usage for developers (15 min)
  - [ ] Multi-server setup (10 min)

#### Localization
- [ ] Complete translations:
  - [x] English (en_US)
  - [ ] Italian (it_IT) - Priority
  - [ ] Spanish (es_ES)
  - [ ] German (de_DE)
  - [ ] French (fr_FR)
  - [ ] Portuguese (pt_BR)
  - [ ] Russian (ru_RU)
- [ ] Translation system with Crowdin
- [ ] Community translation contributions

### 📅 Timeline: Settimane 11-12

---

## 🚀 Fase 6: Release

### 🎯 Obiettivi

Public release su tutte le piattaforme di distribuzione.

### 💻 Tasks

#### Pre-Release
- [ ] Final code review
- [ ] Security audit
- [ ] License compliance check
- [ ] Version bump to 1.0.0
- [ ] Changelog generation
- [ ] Release notes

#### Release Platforms
- [ ] **GitHub Release**
  - [ ] Tag v1.0.0
  - [ ] Release notes
  - [ ] Compiled JARs per piattaforma
  - [ ] Source code zip

- [ ] **SpigotMC**
  - [ ] Resource page setup
  - [ ] Screenshots
  - [ ] Description
  - [ ] Icon/banner
  - [ ] Upload JAR

- [ ] **Modrinth**
  - [ ] Project setup
  - [ ] All versions upload
  - [ ] Platform tags
  - [ ] Gallery images

- [ ] **Hangar (PaperMC)**
  - [ ] Project creation
  - [ ] Version upload
  - [ ] Documentation links

- [ ] **CurseForge**
  - [ ] Fabric/Forge releases
  - [ ] Mod dependencies
  - [ ] Category tags

#### Post-Release
- [ ] **Community**
  - [ ] Discord server setup
  - [ ] Forum monitoring
  - [ ] Issue templates GitHub
  - [ ] Contributing guidelines

- [ ] **Marketing**
  - [ ] SpigotMC announcement
  - [ ] Reddit post (r/admincraft)
  - [ ] Twitter/X announcement
  - [ ] Discord server promotion

- [ ] **Analytics**
  - [ ] bStats setup
  - [ ] Download tracking
  - [ ] User feedback collection

### 📅 Timeline: Settimana 13

---

## 🔮 Future Roadmap (Post 1.0.0)

### Version 1.1.0 - Enhanced Features
- [ ] Web dashboard (React + REST API)
- [ ] Mobile app (Flutter)
- [ ] Advanced scheduling (cron-like)
- [ ] Maintenance templates
- [ ] Custom actions/hooks system
- [ ] Integration with more plugins (LuckPerms, CoreProtect, etc.)

### Version 1.2.0 - Enterprise Features
- [ ] Multi-network support (più proxy networks)
- [ ] Role-based access control (RBAC)
- [ ] Audit logging avanzato
- [ ] Backup/restore automation
- [ ] Incident management integration
- [ ] Metrics dashboard (Grafana)

### Version 2.0.0 - Next Generation
- [ ] Kubernetes integration
- [ ] Microservices architecture
- [ ] GraphQL API
- [ ] Machine learning per predictive maintenance
- [ ] Automated rollback su errori
- [ ] A/B testing features

---

## 📊 Success Metrics

### Technical KPIs
- ✅ 80%+ code coverage
- ✅ <100ms response time API calls
- ✅ Zero memory leaks
- ✅ Support 10+ Minecraft versions
- ✅ 4 platforms supported

### Community KPIs (6 mesi post-release)
- 🎯 10,000+ downloads
- 🎯 100+ stars GitHub
- 🎯 50+ forks
- 🎯 4.5+ rating SpigotMC
- 🎯 1,000+ servers using it (via bStats)
- 🎯 Active community (Discord 500+ members)

---

## 👥 Team & Resources

### Current Team
- **D4vide106** - Lead Developer, Architecture, All Platforms

### Looking For
- Beta testers (tutte le piattaforme)
- Translators (6 lingue)
- Documentation writers
- Community moderators

### Resources Needed
- Server di test per ogni versione Minecraft
- Redis server per testing sync
- Database servers (MySQL, PostgreSQL)
- Budget per hosting (wiki, Discord bot)

---

## 📝 Notes

### Design Decisions

1. **Perché Gradle Multi-Module?**
   - Separazione clara tra common e platforms
   - Riutilizzo codice massimizzato
   - Build paralleli più veloci
   - API versionabile indipendentemente

2. **Perché Java 17?**
   - Modern language features (records, pattern matching)
   - Performance improvements
   - LTS version supportata
   - Compatible con tutte le versioni MC moderne

3. **Perché Adventure API?**
   - Standard de-facto per text components
   - MiniMessage support nativo
   - Cross-platform compatible
   - Migliore di Bungee Chat API

4. **Perché HikariCP?**
   - Fastest connection pool Java
   - Battle-tested in produzione
   - Auto-tuning capabilities
   - Ottimo monitoring

### Risk Mitigation

1. **Compatibilità Versioni**
   - Risk: Breaking changes tra versioni MC
   - Mitigation: Abstraction layers, extensive testing

2. **Performance**
   - Risk: Overhead su server grandi
   - Mitigation: Async ops, caching, profiling

3. **Redis Dependency**
   - Risk: Single point of failure
   - Mitigation: Fallback polling, graceful degradation

4. **Database Migrations**
   - Risk: Data loss durante update
   - Mitigation: Auto-backup, rollback capability

---

## 🔗 Links Utili

- **Repository**: https://github.com/D4vide106/MaintenanceUniversal
- **Issues**: https://github.com/D4vide106/MaintenanceUniversal/issues
- **Wiki**: https://github.com/D4vide106/MaintenanceUniversal/wiki
- **Discord**: TBD
- **bStats**: TBD

---

**Last Updated**: February 13, 2026  
**Version**: 1.0.0-SNAPSHOT  
**Status**: 🟡 In Development - Phase 1