import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "com.cimbul"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test>() {
    useJUnitPlatform()
}

application {
    mainClass.set("com.cimbul.faqueldb.ApplicationKt")
}

dependencies {
    implementation("org.partiql:partiql-lang-kotlin:0.3.1")

    implementation("com.amazon.ion:ion-hash-java:1.0.0")

    implementation(platform("org.http4k:http4k-bom:4.11.0.1"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")

    val jacksonVersion = "2.12.4"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("org.unbroken-dome.base62:base62:1.1.0")

    testImplementation("io.kotest:kotest-runner-junit5:4.6.1")
    testImplementation("io.kotest:kotest-assertions-core:4.0.7")
}

configurations.all {
    resolutionStrategy {
        // Fix warning about incompatible kotlin JARs on the classpath
        force("org.jetbrains.kotlin:kotlin-reflect:${getKotlinPluginVersion()}")
    }
}
