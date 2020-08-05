pluginManagement {
    repositories {
        mavenCentral()
//        gradlePluginPortal()
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
        }
    }

}

rootProject.name = "miraki"
