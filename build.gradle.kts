import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense

plugins {
    kotlin("jvm") version ("1.3.72")
    kotlin("kapt") version ("1.3.72")

    id("org.jenkins-ci.jpi") version "0.39.0"
}

repositories {
    maven(url = "http://bits.netbeans.org/maven2")
    maven(url = "http://repo.jenkins-ci.org/releases/")
    jcenter()
    mavenCentral()
}

group = "org.jenkins-ci.plugins"
version = "0.3.3-SNAPSHOT"
description = "Outbound WebHook for Jenkins build events"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    kapt("net.java.sezpoz:sezpoz:1.13")
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.coravy.hudson.plugins.github:github:1.29.5")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

jenkinsPlugin {
    coreVersion = "2.234"
    compatibleSinceVersion = "2.164.1"
    displayName = "Outbound WebHook for build events"
    url = "https://github.com/bziemons/outbound-webhook-plugin"
    gitHubUrl = "https://github.com/bziemons/outbound-webhook-plugin"
    shortName = "outbound-webhook"
    pluginFirstClassLoader = true
    configurePublishing = false
    maskClasses = "okhttp3 com.google.gson"

    developers = Developers().apply {
        developer(delegateClosureOf<JpiDeveloper> {
            setProperty("id", "tylerlong")
            setProperty("name", "Tyler Long")
            setProperty("email", "tyler4long@gmail.com")
        })
        developer(delegateClosureOf<JpiDeveloper> {
            setProperty("id", "bziemons")
            setProperty("name", "Benedikt Ziemons")
            setProperty("email", "ben@rs485.network")
            setProperty("url", "https://github.com/bziemons")
            setProperty("organization", "RS485")
            setProperty("organizationUrl", "https://github.com/RS485")
            setProperty("timezone", "Europe/Berlin")
        })
    }

    licenses = this.Licenses().apply {
        license(delegateClosureOf<JpiLicense> {
            setProperty("name", "MIT")
            setProperty("url", "https://opensource.org/licenses/MIT")
        })
    }
}
