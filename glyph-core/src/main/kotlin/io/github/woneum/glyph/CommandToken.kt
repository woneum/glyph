package io.github.woneum.glyph

sealed interface CommandToken {
    data class Literal(val value: String) : CommandToken
    data class Argument(val value: String) : CommandToken
}