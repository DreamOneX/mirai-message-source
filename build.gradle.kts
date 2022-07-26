plugins {
  java
  kotlin("jvm") version "1.6.0"
  id("com.github.johnrengelman.shadow") version "6.0.0"
  kotlin("plugin.serialization") version "1.6.0"
  id("net.mamoe.mirai-console") version "2.12.0"

  id("me.him188.maven-central-publish") version "1.0.0-dev-3"
  id("io.codearte.nexus-staging") version "0.30.0"
}
group = "org.mesagisto"
version = "1.4.0"

mavenCentralPublish {
  nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    stagingProfileId = "9bdaa8e9e83392"
    username = credentials?.sonatypeUsername
    password = credentials?.sonatypePassword
  }
  useCentralS01()
  githubProject("Mesagisto", "mirai-message-source")
  licenseFromGitHubProject("AGPLv3", "master")
  developer("Itsusinn")
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
  }
  sourceCompatibility = "1.8"
}

repositories {
  mavenCentral()
  mavenLocal()
}
mirai {
  coreVersion = "2.12.0"
  jvmTarget = JavaVersion.VERSION_1_8
}
dependencies {
  compileOnly("io.nats:jnats:2.15.3")
  implementation("org.fusesource.leveldbjni:leveldbjni-all:1.8")
  implementation("com.github.gotson:webp-imageio:0.2.2")
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.2")
  implementation("org.mesagisto:mesagisto-client:1.5.1")
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.hamcrest:hamcrest:2.2")
}
