plugins {
    java
}

group = "com.yourname.colossus"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper API for 1.21.x
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    
    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql:42.7.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.register<Copy>("copyToServer") {
    dependsOn("build")
    from(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    into("${System.getProperty("user.home")}/mc-dev/server/plugins")
}
