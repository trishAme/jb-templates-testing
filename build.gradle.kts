import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    kotlin("jvm") version "2.4.10"
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
        testFramework(TestFrameworkType.Starter)
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

intellijPlatform {
    instrumentCode = false
}
