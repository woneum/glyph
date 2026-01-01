package io.github.woneum.glyph.argument

import io.github.woneum.glyph.CommandSuggestion
import io.github.woneum.glyph.GlyphContext

abstract class AbstractGlyphArgument<T> : GlyphArgument<T> {
    var suggestionProvider: (CommandSuggestion.(GlyphContext) -> Unit)? = null

    override fun suggests(provider: CommandSuggestion.(context: GlyphContext) -> Unit) {
        this.suggestionProvider = provider
    }
}