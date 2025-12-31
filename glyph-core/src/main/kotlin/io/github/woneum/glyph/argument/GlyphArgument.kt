package io.github.woneum.glyph.argument

interface GlyphArgument<T> {
    fun parse(input: String): T
    fun suggest(): List<String>
}