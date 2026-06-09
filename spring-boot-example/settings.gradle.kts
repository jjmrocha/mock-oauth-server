plugins {
    // Auto-detect / provision the JDK 21 toolchain so the build is independent
    // of the JVM Gradle itself runs on (e.g. a newer default JDK).
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "spring-boot-example"
