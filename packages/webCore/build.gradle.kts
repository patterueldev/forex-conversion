import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    // npm publish plugin to allow publishing the JS package
    alias(libs.plugins.npmPublish)
}

version = "0.1.11"

kotlin {
    js {
        binaries.library()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.clientLogging)
            implementation(libs.ktor.serializationKotlinxJson)
            implementation(libs.kotlinx.serialization.json)

            api(projects.packages.apiCore)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
