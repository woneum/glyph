import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":glyph-core"))
}

extra.apply {
    set("kotlinVersion", libs.versions.kotlin)
    set("paperVersion", libs.versions.paper.get().split('.').take(2).joinToString(separator = "."))
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun Jar.copyToServer(suffix: String) = doLast {
        val pluginsFolder = rootProject.file(".server/plugins-$suffix")
        copy {
            from(archiveFile)
            into(pluginsFolder)
        }
    }

    register<Jar>("clipPluginJar") {
        archiveAppendix.set("clip")
        from(sourceSets["main"].output)

        copyToServer("clip")
    }

    register<ShadowJar>("bundleJar") {
        val core = project(":glyph-core")

        configurations.add(core.configurations.runtimeClasspath.get())

        from(sourceSets["main"].output)
        from(core.sourceSets["main"].output)

        core.subprojects.forEach { compat ->
            val reobfJar = compat.tasks["reobfJar"]
            dependsOn(reobfJar)

            from(compat.sourceSets["main"].output)
        }

        copyToServer("bundle")
    }
}