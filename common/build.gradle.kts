dependencies {
    // Adventure API per componenti testo cross-platform
    api("net.kyori:adventure-api:4.16.0")
    api("net.kyori:adventure-text-minimessage:4.16.0")
    api("net.kyori:adventure-text-serializer-legacy:4.16.0")
    api("net.kyori:adventure-text-serializer-gson:4.16.0")
    
    // Configuration library
    api("org.spongepowered:configurate-yaml:4.1.2")
    api("org.spongepowered:configurate-gson:4.1.2")
    api("org.spongepowered:configurate-hocon:4.1.2")
    
    // Database
    api("com.zaxxer:HikariCP:5.1.0")
    api("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    api("com.mysql:mysql-connector-j:8.2.0")
    api("redis.clients:jedis:5.1.0")
    
    // Utilities
    api("com.google.code.gson:gson:2.10.1")
    api("org.jetbrains:annotations:24.1.0")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
}