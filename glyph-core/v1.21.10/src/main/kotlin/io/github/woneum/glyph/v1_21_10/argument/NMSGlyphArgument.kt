package io.github.woneum.glyph.v1_21_10.argument

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.woneum.glyph.GlyphContext
import io.github.woneum.glyph.argument.AbstractGlyphArgument
import io.github.woneum.glyph.argument.GlyphArgument
import io.github.woneum.glyph.argument.GlyphArgumentSupport
import io.github.woneum.glyph.argument.StringType
import io.github.woneum.glyph.v1_21_10.NMSCommandSuggestion
import io.github.woneum.glyph.v1_21_10.NMSGlyphContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture

open class NMSGlyphArgument<T>(
    val type: ArgumentType<*>,
    private val provider: (NMSGlyphContext, name: String) -> T,
    private val defaultSuggestionProvider: SuggestionProvider<CommandSourceStack>? = null
) : AbstractGlyphArgument<T>() {
    private companion object {
        private val originalMethod: Method = ArgumentType::class.java.declaredMethods.find { method ->
            val parameterTypes = method.parameterTypes

            parameterTypes.count() == 2
                    && parameterTypes[0] == CommandContext::class.java
                    && parameterTypes[1] == SuggestionsBuilder::class.java
        } ?: error("Not found listSuggestion")

        private val overrideSuggestions = hashMapOf<Class<*>, Boolean>()

        private fun checkOverrideSuggestions(type: Class<*>): Boolean = overrideSuggestions.computeIfAbsent(type) {
            originalMethod.declaringClass != type.getMethod(
                originalMethod.name,
                *originalMethod.parameterTypes
            ).declaringClass
        }
    }

    private val hasOverrideSuggestion: Boolean by lazy {
        checkOverrideSuggestions(type.javaClass)
    }

    fun from(context: NMSGlyphContext, name: String): T {
        return provider(context, name)
    }

    fun listSuggestions(
        context: NMSGlyphContext,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        this.suggestionProvider?.let {
            val suggestion = NMSCommandSuggestion(builder)
            it(suggestion, context)
            if (!suggestion.suggestsDefault) return builder.buildFuture()
        }

        defaultSuggestionProvider?.let { return it.getSuggestions(context.handle, builder) }
        if (hasOverrideSuggestion) return type.listSuggestions(context.handle, builder)
        return builder.buildFuture()
    }
}

infix fun <T> ArgumentType<*>.provide(
    provider: (context: CommandContext<CommandSourceStack>, name: String) -> T
): NMSGlyphArgument<T> {
    return NMSGlyphArgument(this, { context, name ->
        provider(context.handle, name)
    })
}

class NMSGlyphArgumentSupport : GlyphArgumentSupport {
    override fun bool(): GlyphArgument<Boolean> {
        return BoolArgumentType.bool() provide BoolArgumentType::getBool
    }

    override fun int(
        minimum: Int,
        maximum: Int
    ): GlyphArgument<Int> {
        return IntegerArgumentType.integer(minimum, maximum) provide IntegerArgumentType::getInteger
    }

    override fun float(
        minimum: Float,
        maximum: Float
    ): GlyphArgument<Float> {
        return FloatArgumentType.floatArg(minimum, maximum) provide FloatArgumentType::getFloat
    }

    override fun double(
        minimum: Double,
        maximum: Double
    ): GlyphArgument<Double> {
        return DoubleArgumentType.doubleArg(minimum, maximum) provide DoubleArgumentType::getDouble
    }

    override fun long(
        minimum: Long,
        maximum: Long
    ): GlyphArgument<Long> {
        return LongArgumentType.longArg(minimum, maximum) provide LongArgumentType::getLong
    }

    override fun string(type: StringType): GlyphArgument<String> {
        return type.createType() provide StringArgumentType::getString
    }

    override fun player(): GlyphArgument<Player> {
        return EntityArgument.player() provide { context, name ->
            EntityArgument.getPlayer(context, name).bukkitEntity
        }
    }

    override fun players(): GlyphArgument<Collection<Player>> {
        return EntityArgument.players() provide { context, name ->
            EntityArgument.getPlayers(context, name).map { it.bukkitEntity }
        }
    }

    override fun entity(): GlyphArgument<Entity> {
        return EntityArgument.entity() provide { context, name ->
            EntityArgument.getEntity(context, name).bukkitEntity
        }
    }

    override fun entities(): GlyphArgument<Collection<Entity>> {
        return EntityArgument.entities() provide { context, name ->
            EntityArgument.getEntities(context, name).map { it.bukkitEntity }
        }
    }
}

fun StringType.createType(): StringArgumentType {
    return when (this) {
        StringType.SINGLE_WORD -> StringArgumentType.word()
        StringType.QUOTABLE_PHRASE -> StringArgumentType.string()
        StringType.GREEDY_PHRASE -> StringArgumentType.greedyString()
    }
}