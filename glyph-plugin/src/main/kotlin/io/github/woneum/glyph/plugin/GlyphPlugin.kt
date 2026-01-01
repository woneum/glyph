package io.github.woneum.glyph.plugin

import io.github.woneum.glyph.Args
import io.github.woneum.glyph.Command
import io.github.woneum.glyph.Glyph
import io.github.woneum.glyph.argument.ArgumentRegistry
import io.github.woneum.glyph.argument.GlyphArgument.Companion.bool
import io.github.woneum.glyph.argument.GlyphArgument.Companion.double
import io.github.woneum.glyph.argument.GlyphArgument.Companion.entities
import io.github.woneum.glyph.argument.GlyphArgument.Companion.entity
import io.github.woneum.glyph.argument.GlyphArgument.Companion.float
import io.github.woneum.glyph.argument.GlyphArgument.Companion.int
import io.github.woneum.glyph.argument.GlyphArgument.Companion.long
import io.github.woneum.glyph.argument.GlyphArgument.Companion.player
import io.github.woneum.glyph.argument.GlyphArgument.Companion.players
import io.github.woneum.glyph.argument.GlyphArgument.Companion.string
import io.github.woneum.glyph.argument.StringType
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

@Command("glyph")
class GlyphPlugin : JavaPlugin() {
    init {
        ArgumentRegistry.register("player", player())
        ArgumentRegistry.register("players", players())
        ArgumentRegistry.register("entity", entity())
        ArgumentRegistry.register("entities", entities())
        ArgumentRegistry.register("int", int(0, 3))
        ArgumentRegistry.register("float", float(0.0f, 3.0f))
        ArgumentRegistry.register("double", double(0.0, 1.0))
        ArgumentRegistry.register("long", long(135, 13590))
        ArgumentRegistry.register("string", string(StringType.SINGLE_WORD))
        ArgumentRegistry.register("bool", bool())
    }
    override fun onEnable() {
        Glyph.compute(this, this)
        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onPlayerJoin(event: PlayerJoinEvent) {
                Glyph.sendCommandsPacket(event.player)
            }
        }, this)
    }

    @Args("p <int>")
    fun int(sender: CommandSender, int: Int) {
        sender.sendMessage("Hello $int")
    }

    @Args("p <float>")
    fun float(sender: CommandSender, float: Float) {
        sender.sendMessage("Hello $float")
    }

    @Args("p <double>")
    fun double(sender: CommandSender, double: Double) {
        sender.sendMessage("Hello $double")
    }

    @Args("p <long>")
    fun long(sender: CommandSender, long: Long) {
        sender.sendMessage("Hello $long")
    }

    @Args("bool <bool>")
    fun bool(sender: CommandSender, bool: Boolean) {
        sender.sendMessage("Hello $bool")
    }

    @Args("player <player>")
    fun player(sender: CommandSender, player: Player) {
        sender.sendMessage("Hello ${player.name}")
    }

    @Args("players <players>")
    fun players(sender: CommandSender, players: Collection<Player>) {
        players.forEach {
            sender.sendMessage("Hello ${it.name}")
        }
    }

    @Args("entity <entity>")
    fun entity(sender: CommandSender, entity: Entity) {
        sender.sendMessage("Hello ${entity.name}, type: ${entity.type.name}")
    }

    @Args("entities <entities>")
    fun entities(sender: CommandSender, entities: Collection<Entity>) {
        entities.forEach {
            sender.sendMessage("Hello ${it.name}, type: ${it.type.name}")
        }
    }
}