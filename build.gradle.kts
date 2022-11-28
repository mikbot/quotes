import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
    id("dev.schlaubi.mikbot.gradle-plugin") version "2.6.0"
}

group = "net.stckoverflw"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    mikbot("dev.schlaubi", "mikbot-api", "3.10.0-SNAPSHOT")
    ksp("dev.schlaubi", "mikbot-plugin-processor", "2.3.0")
}

mikbotPlugin {
    description.set("A simple Quote Bot mikbot Plugin")
    provider.set("StckOverflw")
    license.set("MIT")
}

pluginPublishing {
    repositoryUrl.set("https://quotes-repo.stckoverflw.net")
    targetDirectory.set(rootProject.file("ci-repo").toPath())
    projectUrl.set("https://github.com/mikbot/quotes")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "18"
}