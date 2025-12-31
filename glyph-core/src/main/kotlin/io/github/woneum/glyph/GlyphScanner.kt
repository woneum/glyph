package io.github.woneum.glyph

object GlyphScanner {
    fun scan(instance: Any): List<CommandDefinition> {
        val clazz = instance::class.java
        val cmd = clazz.getAnnotation(Command::class.java) ?: return emptyList()

        val base = CommandParser.parse(cmd.value)

        return clazz.methods
            .filter { it.isAnnotationPresent(Args::class.java) }
            .map { method ->
                val args = method.getAnnotation(Args::class.java)
                val tokens = ArrayList(base + CommandParser.parse(args.value))
                CommandDefinition(tokens, method, instance)
            }
    }
}