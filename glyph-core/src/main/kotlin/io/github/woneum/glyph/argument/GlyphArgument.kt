package io.github.woneum.glyph.argument

import io.github.woneum.glyph.CommandSuggestion
import io.github.woneum.glyph.GlyphContext
import io.github.woneum.glyph.loader.LibraryLoader
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

interface GlyphArgument<T> {
    companion object : GlyphArgumentSupport by GlyphArgumentSupport.INSTANCE

    fun suggests(provider: CommandSuggestion.(context: GlyphContext) -> Unit)
}

interface GlyphArgumentSupport {
    companion object {
        val INSTANCE = LibraryLoader.loadNMS(GlyphArgumentSupport::class.java)
    }

    fun bool(): GlyphArgument<Boolean>

    fun int(minimum: Int = Int.MIN_VALUE, maximum: Int = Int.MAX_VALUE): GlyphArgument<Int>

    fun float(minimum: Float = Float.MIN_VALUE, maximum: Float = Float.MAX_VALUE): GlyphArgument<Float>

    fun double(minimum: Double = Double.MIN_VALUE, maximum: Double = Double.MAX_VALUE): GlyphArgument<Double>

    fun long(minimum: Long = Long.MIN_VALUE, maximum: Long = Long.MAX_VALUE): GlyphArgument<Long>

    fun string(type: StringType = StringType.SINGLE_WORD): GlyphArgument<String>

    fun player(): GlyphArgument<Player>

    fun players(): GlyphArgument<Collection<Player>>

    fun entity(): GlyphArgument<Entity>

    fun entities(): GlyphArgument<Collection<Entity>>
}

enum class StringType {
    SINGLE_WORD,
    QUOTABLE_PHRASE,
    GREEDY_PHRASE
}