plugins {
    alias(libs.plugins.paperweight) apply false
}

subprojects {
    apply(plugin = rootProject.libs.plugins.paperweight.get().pluginId)
    dependencies {
        implementation(project(":glyph-core"))

        val paperweight = (this as ExtensionAware).extensions.getByName("paperweight")
            as io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
        paperweight.paperDevBundle("${name.substring(1)}-R0.1-SNAPSHOT")
    }
}