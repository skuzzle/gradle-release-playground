pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "gradle-release-playground"
include("hello-world")
include("readme")
