import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    application
}

group = "com.cimbul"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test>() {
    useJUnitPlatform()
}

application {
    mainClass.set("com.cimbul.faqueldb.ApplicationKt")
}

dependencies {
    implementation("org.partiql:partiql-lang-kotlin:0.9.2")

    implementation("com.amazon.ion:ion-hash-java:1.0.0")

    implementation(platform("org.http4k:http4k-bom:4.37.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")

    val jacksonVersion = "2.14.2"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("org.unbroken-dome.base62:base62:1.1.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-assertions-core:5.5.4")
}

configurations.all {
    resolutionStrategy {
        // Fix warning about incompatible kotlin JARs on the classpath
        force("org.jetbrains.kotlin:kotlin-reflect:${getKotlinPluginVersion()}")
    }
}
