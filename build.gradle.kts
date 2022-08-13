import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.19.0")
    }
}

apply(plugin = "com.vanniktech.maven.publish")

plugins {
    // Structure
    kotlin("jvm") version "1.5.31"
    `java-library`
    // Quality
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    // Publishing
    id("org.jetbrains.dokka") version "1.6.10"
}

group = project.properties["GROUP"].toString()
version = project.properties["VERSION_NAME"].toString()
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // JWT
    implementation("com.nimbusds:nimbus-jose-jwt:9.23")
    // Json
    implementation("com.google.code.gson:gson:2.9.0")
    // testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            FAILED,
            STANDARD_ERROR,
            SKIPPED,
            PASSED
        )
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
