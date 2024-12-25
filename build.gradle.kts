plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "eu.codetopic.anty.aoc"
version = "1.0-SNAPSHOT"

//set an entry point for the application
application {
    mainClass = "eu.codetopic.anty.aoc.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.sksamuel.scrimage:scrimage-core:4.3.0")
}

tasks.test {
    useJUnitPlatform()
}