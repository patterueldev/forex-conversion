rootProject.name = "forexconversion"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":apps:androidApp")
include(":apps:composeApp")
include(":apps:server")
include(":packages:shared")
include(":packages:apiCore")
include(":packages:forex-web-sdk")
project(":packages:forex-web-sdk").projectDir = file("packages/webCore")
include(":packages:mobileCore")