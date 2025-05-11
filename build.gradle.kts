import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR

plugins {
    // Structure
    kotlin("jvm") version "1.9.25"
    `java-library`
    // Quality
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.17.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    // Publishing
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.31.0"
}

group = project.properties["GROUP"].toString()
version = project.properties["VERSION_NAME"].toString()

java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}

repositories {
    mavenCentral()
}

dependencies {
    // http
    implementation("net.uiqui:embedhttp:0.5.3")
    // JWT
    implementation("com.nimbusds:nimbus-jose-jwt:10.3")
    // Json
    implementation("com.google.code.gson:gson:2.13.1")
    // testing
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.27.3")
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
            ),
        )
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(
            FAILED,
            STANDARD_ERROR,
            SKIPPED,
            PASSED,
        )
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

kotlin {
    jvmToolchain(21)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
