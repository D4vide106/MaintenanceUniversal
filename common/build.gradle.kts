plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Adventure API (text components)
    api("net.kyori:adventure-api:4.16.0")
    api("net.kyori:adventure-text-minimessage:4.16.0")
    api("net.kyori:adventure-text-serializer-legacy:4.16.0")
    api("net.kyori:adventure-text-serializer-plain:4.16.0")
    
    // Configurate (YAML configuration)
    api("org.spongepowered:configurate-yaml:4.1.2")
    api("org.spongepowered:configurate-core:4.1.2")
    
    // HikariCP (database connection pooling)
    api("com.zaxxer:HikariCP:5.1.0")
    
    // Jedis (Redis client)
    api("redis.clients:jedis:5.1.0")
    
    // Gson (JSON serialization)
    api("com.google.code.gson:gson:2.10.1")
    
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
