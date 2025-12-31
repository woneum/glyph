package io.github.woneum.glyph

import java.lang.reflect.Method

data class CommandDefinition(
    val tokens: ArrayList<CommandToken>,
    val method: Method,
    val instance: Any
)