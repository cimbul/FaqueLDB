import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
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

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.17.23"))
    implementation("software.amazon.awssdk:qldbsession")
    implementation("org.partiql:partiql-lang-kotlin:0.3.1")

    testImplementation("io.kotest:kotest-runner-junit5:4.6.1")
    testImplementation("io.kotest:kotest-assertions-core:4.0.7")
}
