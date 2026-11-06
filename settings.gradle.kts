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
    // PREFER_SETTINGS (not FAIL_ON_PROJECT_REPOS): the Kotlin web toolchain adds its own download
    // sites; they are declared below instead and the plugin's own copies are ignored.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // The web app's build tools (:webApp): Node.js, Yarn and Binaryen, downloaded once by Gradle.
        ivy("https://nodejs.org/dist") {
            name = "Node.js"
            patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
            metadataSources { artifact() }
            content { includeModule("org.nodejs", "node") }
        }
        ivy("https://github.com/yarnpkg/yarn/releases/download") {
            name = "Yarn"
            patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
            metadataSources { artifact() }
            content { includeModule("com.yarnpkg", "yarn") }
        }
        ivy("https://github.com/WebAssembly/binaryen/releases/download") {
            name = "Binaryen"
            patternLayout { artifact("version_[revision]/[module]-version_[revision]-[classifier].[ext]") }
            metadataSources { artifact() }
            content { includeModule("com.github.webassembly", "binaryen") }
        }
        // LiveKit's audio routing (audioswitch fork) is only published on JitPack.
        maven("https://jitpack.io") {
            content { includeGroup("com.github.davidliu") }
        }
    }
}

rootProject.name = "YapNPlay"

// Single appengine library. core/, games/base/, protocol and games/*,
// ui/, di/ are plain source folders inside it — not Gradle modules.
// :androidApp is a thin launch shell (manifest + activity only).
include(":appengine")
include(":androidApp")
// :webApp is the browser launch shell (page + entry point only), for iPhone users and any browser.
include(":webApp")
