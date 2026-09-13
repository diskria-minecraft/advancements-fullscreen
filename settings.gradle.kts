pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        gradlePluginPortal()
    }
}

rootProject.name = "advancements_fullscreen"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
    }
}
