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
    mainClass.set("com.cimbul.faqeldb.ApplicationKt")
}

dependencies {
    implementation("org.partiql:partiql-lang-kotlin:0.3.1")

    implementation(platform("org.http4k:http4k-bom:4.11.0.1"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")

    val jacksonVersion = "2.12.4"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    testImplementation("io.kotest:kotest-runner-junit5:4.6.1")
    testImplementation("io.kotest:kotest-assertions-core:4.0.7")
}
