rootProject.name = "glyph"

val prefix = rootProject.name

val core = "$prefix-core"
include(core)
file(core).listFiles()?.filter {
    it.isDirectory && it.name.startsWith("v")
}?.forEach { file ->
    include(":$core:${file.name}")
}

include("$prefix-plugin")
include("$prefix-publish")