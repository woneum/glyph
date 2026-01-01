package io.github.woneum.glyph

import net.kyori.adventure.text.ComponentLike

interface CommandSuggestion {
    fun suggestDefault()

    fun suggest(value: Int, tooltip: (() -> ComponentLike)? = null)

    fun suggest(text: String, tooltip: (() -> ComponentLike)? = null)

    fun suggest(
        candidates: Iterable<String>,
        tooltip: ((String) -> ComponentLike)? = null
    )

    fun <T> suggest(
        candidates: Iterable<T>,
        transform: (T) -> String,
        tooltip: ((T) -> ComponentLike)? = null
    )

    fun <T> suggest(
        candidates: Map<String, T>,
        tooltip: ((T) -> ComponentLike)? = null
    )
}