import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}
group = "org.akteam"
version = "4.0-SNAPSHOT"

repositories {
//    mavenLocal()
    maven(url = "https://maven.aliyun.com/repository/public")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://kotlin.bintray.com/kotlinx")
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

object Versions {
    const val mirai = "1.2.2"

    object Kotlin {
        const val compiler = "1.4.0"
        const val stdlib = "1.4.0"
        const val coroutines = "1.3.9"
        const val atomicFU = "0.14.4"
        const val serialization = "1.0.0-RC"
        const val ktor = "1.4.0"
        const val binaryValidator = "0.2.3"

        const val io = "0.1.16"
        const val coroutinesIo = "0.1.16"
        const val dokka = "0.10.1"
    }
}

dependencies {
    implementation(mirai("core"))
    implementation(mirai("core-qqandroid"))

    implementation(kotlinx("serialization-core", Versions.Kotlin.serialization))

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
            attributes(
                "Main-Class" to "org.akteam.miraki.BotMainKt"
            )
        }
    }
}

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

fun ktor(id: String, version: String = Versions.Kotlin.ktor) = "io.ktor:ktor-$id:$version"

fun mirai(id: String, version: String = Versions.mirai) = "net.mamoe:mirai-$id:$version"
