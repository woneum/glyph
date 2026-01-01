package io.github.woneum.glyph.v1_21_10

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import io.github.woneum.glyph.CommandDefinition
import io.github.woneum.glyph.CommandToken
import io.github.woneum.glyph.Glyph
import io.github.woneum.glyph.GlyphContext
import io.github.woneum.glyph.GlyphScanner
import io.github.woneum.glyph.argument.ArgumentRegistry
import io.github.woneum.glyph.v1_21_10.argument.NMSGlyphArgument
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.command.VanillaCommandWrapper
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class NMSGlyph : Glyph {
    private val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
    private val vanillaCommands: Commands = server.commands
    private val dispatcher: CommandDispatcher<CommandSourceStack> = vanillaCommands.dispatcher
    private val root: RootCommandNode<CommandSourceStack> = dispatcher.root

    private val commandMap = Bukkit.getCommandMap()

    override fun compute(plugin: JavaPlugin, vararg handlers: Any) {
        Bukkit.getPluginManager().registerEvents(PlayerListener(this), plugin)

        handlers.forEach {
            val defs = GlyphScanner.scan(it)
            val root = defs.first().tokens[0]
            val rootNode = literal((root as CommandToken.Literal).value)
            defs.forEach { def ->
                def.tokens.removeFirst()
                rootNode.then(build(def))
            }

            val node = dispatcher.register(rootNode)
            commandMap.register(
                "test",
                VanillaCommandWrapper(node)
            )
        }

    }

    override fun sendCommandsPacket(player: Player) {
        vanillaCommands.sendCommands((player as CraftPlayer).handle)
    }

    private fun build(def: CommandDefinition): ArgumentBuilder<CommandSourceStack, *> {
        var current: ArgumentBuilder<CommandSourceStack, *>? = null

        for (token in def.tokens.asReversed()) {
            var glyphContext: NMSGlyphContext? = null
            current = when (token) {
                is CommandToken.Literal -> {
                    if (current == null) {
                        literal(token.value).also {
                            it.executes { context ->
                                glyphContext = NMSGlyphContext(context.source.bukkitSender, context)
                                invoke(def, glyphContext)
                                1
                            }
                        }
                    } else {
                        literal(token.value).then(current)
                    }
                }
                is CommandToken.Argument -> {
                    if (current == null) {
                        val arg = ArgumentRegistry.get(token.value) as NMSGlyphArgument<*>
                        argument(token.value, arg.type).also {
                            it.suggests { context, builder ->
                                glyphContext = NMSGlyphContext(context.source.bukkitSender, context)
                                arg.listSuggestions(glyphContext, builder)
                            }
                            it.executes { context ->
                                 invoke(def, glyphContext!!)
                                1
                            }
                        }
                    } else {
                        val arg = ArgumentRegistry.get(token.value) as NMSGlyphArgument<*>
                        argument(token.value, arg.type).also {
                            it.suggests { context, builder ->
                                glyphContext = NMSGlyphContext(context.source.bukkitSender, context)
                                arg.listSuggestions(glyphContext, builder)
                            }
                            it.then(current)
                        }
                    }
                }
            }
        }

        return current!!
    }

    private fun invoke(def: CommandDefinition, glyphContext: NMSGlyphContext) {
        val method = def.method
        val params = method.parameters
        val args = ArrayList<Any>()

        var argIndex = 0

        for ((index, param) in params.withIndex()) {
            if (index == 0 && CommandSender::class.java.isAssignableFrom(param.type)) {
                args += glyphContext.sender
            } else if (GlyphContext::class.java.isAssignableFrom(param.type)) {
                args += glyphContext
            } else {
                val token = def.tokens.filterIsInstance<CommandToken.Argument>().getOrNull(argIndex) ?: error("Too many parameters for command")

                val glyphArg = ArgumentRegistry.get(token.value)

                @Suppress("UNCHECKED_CAST")
                args += (glyphArg as NMSGlyphArgument<Any>).from(glyphContext, token.value)

                argIndex++
            }
        }

        method.invoke(def.instance, *args.toTypedArray())
    }
}

private fun literal(name: String): LiteralArgumentBuilder<CommandSourceStack> {
    return LiteralArgumentBuilder.literal(name)
}

private fun argument(name: String, argumentType: ArgumentType<*>): RequiredArgumentBuilder<CommandSourceStack, *> {
    return RequiredArgumentBuilder.argument(name, argumentType)
}

class PlayerListener(private val glyph: NMSGlyph) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        glyph.sendCommandsPacket(event.player)
    }
}