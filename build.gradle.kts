import shadow.org.apache.commons.io.FilenameUtils
plugins {
  java
  kotlin("jvm") version "1.6.0"
  id("com.github.johnrengelman.shadow") version "5.2.0"
  kotlin("plugin.serialization") version "1.6.0"
  id("net.mamoe.mirai-console") version "2.12.0-RC"
}
group = "org.meowcat"
version = "1.2.2"
tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
  }
  sourceCompatibility = "1.8"
}

repositories {
  mavenCentral()
  maven("https://jitpack.io")
  google()
  mavenLocal()
}
mirai {
  coreVersion = "2.12.0-RC"
  jvmTarget = JavaVersion.VERSION_1_8
  excludeDependency("org.jetbrains.kotlin:kotlin-stdlib")
  excludeDependency("org.jetbrains.kotlin:kotlin-reflect")
  excludeDependency("org.jetbrains.kotlin:kotlin-stdlib-common")
  excludeDependency("org.jetbrains:annotations")

  configureShadow {

    //   relocate("kotlinx.serialization.", "relocate.kotlinx.serialization.")
    exclude { file ->
      val excludeFiles = arrayOf(
        "kotlin/*",
        "kotlinx/coroutines/*",
        "kotlinx/serialization/*",
        "org/bouncycastle/*",
        "org/jetbrains/*",
        "org/intellij/*",
        "META-INF/*.kotlin_module",
        "META-INF/*.DSA",
        "META-INF/versions/*",
        "META-INF/maven/*",
        "*.md",
      )
      val includeFiles = arrayOf(
        "kotlinx/serialization/cbor/*"
      )
      var shouldExclude = false

      excludeFiles.forEach first@{ excludeFile ->
        if (FilenameUtils.wildcardMatch(file.path, excludeFile)) {
          shouldExclude = true
          includeFiles.forEach second@{ includeFile ->
            if (FilenameUtils.wildcardMatch(file.path, includeFile)) {
              shouldExclude = false
              return@second
            }
          }
          return@first
        }
      }
      shouldExclude
    }
    minimize()
  }
}
dependencies {
  compileOnly("io.nats:jnats:2.15.3")
  implementation("org.rocksdb:rocksdbjni:7.2.2")
  implementation("org.fusesource.leveldbjni:leveldbjni-all:1.8")
  implementation("com.github.gotson:webp-imageio:0.2.2")

  compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.2.2")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.2")
  implementation("org.meowcat:mesagisto-client-jvm:1.3.4")
  // implementation("org.meowcat:mesagisto-client:1.3.4-fix")
  testCompileOnly("junit:junit:4.13.2")
}
