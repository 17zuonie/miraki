import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}
group = "org.akteam"
version = "4.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
//    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

object Versions {
    const val mirai = "1.1.3"

    object Kotlin {
        const val stdlib = "1.3.70"
        const val coroutines = "1.3.7"
        const val atomicFU = "0.14.2"
        const val serialization = "0.20.0"
        const val ktor = "1.3.2"
        const val binaryValidator = "0.2.3"

        const val io = "0.1.16"
        const val coroutinesIo = "0.1.16"
        const val dokka = "0.10.1"
    }

    object Publishing {
        const val bintray = "1.8.5"
    }
}

dependencies {
    implementation(mirai("core"))
    implementation(mirai("core-qqandroid"))

    implementation(kotlinx("serialization-runtime", Versions.Kotlin.serialization))

    implementation(ktor("server-core"))
    implementation(ktor("server-netty"))
    implementation(ktor("auth"))
    implementation(ktor("auth-jwt"))
    implementation(ktor("serialization"))

    implementation("me.liuwj.ktorm", "ktorm-core", "3.0.0")
    implementation("me.liuwj.ktorm", "ktorm-support-postgresql", "3.0.0")
    implementation("com.impossibl.pgjdbc-ng", "pgjdbc-ng", "0.8.4")
    implementation("org.slf4j", "slf4j-simple", "1.7.29")

    implementation("com.squareup.okhttp3", "okhttp", "4.7.2")
    implementation("org.jsoup", "jsoup", "1.13.1")
}

tasks {
    shadowJar {
//        minimize()
        manifest {
            attributes(mapOf("Main-Class" to "org.akteam.miraki.BotMainKt"))
        }
    }
}

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

fun ktor(id: String, version: String = Versions.Kotlin.ktor) = "io.ktor:ktor-$id:$version"

fun mirai(id: String, version: String = Versions.mirai) = "net.mamoe:mirai-$id:$version"
