pluginManagement {
    plugins {
        val kotlinVersion = "2.0.20-Beta1"
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion

        // Update this in libs.version.toml when you change it here
        id("io.gitlab.arturbosch.detekt") version "1.23.5"

        id("com.github.jakemarsden.git-hooks") version "0.0.2"
        id("com.github.johnrengelman.shadow") version "8.1.1"
    }
}

rootProject.name = "allium"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
