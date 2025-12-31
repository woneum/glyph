package io.github.woneum.glyph.argument

object ArgumentRegistry {
    private val arguments = mutableMapOf<String, GlyphArgument<*>>()

    fun register(id: String, argument: GlyphArgument<*>) {
        arguments[id] = argument
    }

    operator fun get(id: String): GlyphArgument<*> =
        arguments[id] ?: throw IllegalArgumentException("Unknown glyph argument $id")
}