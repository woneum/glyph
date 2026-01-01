package io.github.woneum.glyph.v1_21_10

import com.mojang.brigadier.Message
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.woneum.glyph.AbstractCommandSuggestion
import io.github.woneum.glyph.ref.getValue
import io.github.woneum.glyph.ref.weak
import net.kyori.adventure.text.ComponentLike
import net.minecraft.commands.SharedSuggestionProvider
import java.util.Locale

class NMSCommandSuggestion(
    handle: SuggestionsBuilder
): AbstractCommandSuggestion() {
    private val handle by weak(handle)
    override fun suggest(value: Int, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) handle.suggest(value)
        else handle.suggest(value, Message {
            tooltip().asComponent().toString()
        })
    }

    override fun suggest(text: String, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) handle.suggest(text)
        else handle.suggest(text, Message {
            tooltip().asComponent().toString()
        })
    }

    override fun suggest(
        candidates: Iterable<String>,
        tooltip: ((String) -> ComponentLike)?
    ) {
        val handle = handle
        val input: String = handle.remaining.lowercase(Locale.ROOT)

        candidates.forEach { candidate ->
            val lowerCandidate = candidate.lowercase(Locale.ROOT)

            if (SharedSuggestionProvider.matchesSubStr(input, lowerCandidate)) {
                if (tooltip == null) handle.suggest(candidate)
                else handle.suggest(candidate, Message {
                    tooltip(candidate).asComponent().toString()
                })
            }
        }
    }

    override fun <T> suggest(
        candidates: Iterable<T>,
        transform: (T) -> String,
        tooltip: ((T) -> ComponentLike)?
    ) {
        val handle = handle
        val input: String = handle.remaining.lowercase(Locale.ROOT)

        candidates.forEach {
            val candidate = transform(it)
            val lowerCandidate = candidate.lowercase(Locale.ROOT)

            if (SharedSuggestionProvider.matchesSubStr(input, lowerCandidate)) {
                if (tooltip == null) handle.suggest(candidate)
                else handle.suggest(candidate, Message {
                    tooltip(it).asComponent().toString()
                })
            }
        }
    }

    override fun <T> suggest(
        candidates: Map<String, T>,
        tooltip: ((T) -> ComponentLike)?
    ) {
        val handle = handle
        val input: String = handle.remaining.lowercase(Locale.ROOT)

        candidates.forEach { (key, value) ->
            val lowerCandidate = key.lowercase(Locale.ROOT)

            if (SharedSuggestionProvider.matchesSubStr(input, lowerCandidate)) {
                if (tooltip == null) handle.suggest(key)
                else handle.suggest(key, Message {
                    tooltip(value).asComponent().toString()
                })
            }
        }
    }

}