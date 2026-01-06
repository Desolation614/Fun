plugins {
    java
    application
}

group = "myclient"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    exclude("party/Party.java")
}


dependencies {
    // All Ferox jars in /libs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Lombok for @NonNull
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    // JetBrains annotations
    implementation("org.jetbrains:annotations:24.0.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// THIS IS THE APPLICATION PLUGIN CONFIG
application {
    mainClass.set("MyClientLauncher") // fully-qualified if in package
}
