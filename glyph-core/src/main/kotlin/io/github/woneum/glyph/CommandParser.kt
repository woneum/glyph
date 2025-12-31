package io.github.woneum.glyph

object CommandParser {
    fun parse(input: String): List<CommandToken> {
        return input.split(" ").map {
            if (it.startsWith("<") && it.endsWith(">")) {
                CommandToken.Argument(it.substring(1, it.length - 1))
            } else {
                CommandToken.Literal(it)
            }
        }
    }
}