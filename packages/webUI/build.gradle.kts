plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

version = "0.1.0"

kotlin {
    js {
        binaries.executable()
        browser {
            // Configure webpack for production bundle
            webpackTask {
                mainOutputFileName = "compose-web-ui.js"
            }
        }
    }

    sourceSets {
        jsMain.dependencies {
            // Compose UI for web
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            
            // Our shared UI component
            api(projects.packages.sharedUi)
        }
    }
}

// Task to copy the webpack bundle to a dist folder for easy access
tasks.register<Copy>("copyWebBundle") {
    dependsOn("jsBrowserProductionWebpack")
    
    from(layout.buildDirectory.dir("kotlin-webpack/js/productionExecutable"))
    into(layout.buildDirectory.dir("dist"))
    
    include("compose-web-ui.js")
    include("*.wasm")
}

tasks.named("build") {
    dependsOn("copyWebBundle")
}
