pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
/*
// هذا هو السطر السحري الذي سيحل المشكلة:
        maven {
            url = uri("https://maven.cardinalcommerce.com/artifactory/android")
        }
        maven { url = uri("https://jitpack.io") }
 */
    }
}

rootProject.name = "Musee"
include(":app")
 