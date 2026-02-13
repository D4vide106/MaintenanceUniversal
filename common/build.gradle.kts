plugins {
    `java-library`
}

val adventureVersion: String by project
val configurateVersion: String by project
val hikariVersion: String by project
val jedisVersion: String by project
val gsonVersion: String by project

dependencies {
    // Adventure API (text components)
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-minimessage:$adventureVersion")
    api("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    
    // Configurate (YAML configuration)
    api("org.spongepowered:configurate-yaml:$configurateVersion")
    api("org.spongepowered:configurate-core:$configurateVersion")
    
    // HikariCP (database connection pooling)
    api("com.zaxxer:HikariCP:$hikariVersion")
    
    // Jedis (Redis client)
    api("redis.clients:jedis:$jedisVersion")
    
    // Gson (JSON serialization)
    api("com.google.code.gson:gson:$gsonVersion")
    
    // SQLite JDBC driver
    api("org.xerial:sqlite-jdbc:3.44.1.0")
    
    // MySQL JDBC driver
    compileOnly("mysql:mysql-connector-java:8.0.33")
    
    // PostgreSQL JDBC driver
    compileOnly("org.postgresql:postgresql:42.7.1")
    
    // JetBrains Annotations
    compileOnly("org.jetbrains:annotations:24.1.0")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
}

tasks.test {
    useJUnitPlatform()
}