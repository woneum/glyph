package io.github.woneum.glyph

abstract class AbstractCommandSuggestion : CommandSuggestion {
    var suggestsDefault = false
        private set

    override fun suggestDefault() {
        suggestsDefault = true
    }
}