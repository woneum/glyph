package io.github.woneum.glyph.plugin

import io.github.woneum.glyph.Args
import io.github.woneum.glyph.Command
import io.github.woneum.glyph.Glyph
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

@Command("glyph")
class GlyphPlugin : JavaPlugin() {
    override fun onEnable() {
        Glyph.compute(this, this)
        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onPlayerJoin(event: PlayerJoinEvent) {
                Glyph.sendCommandsPacket(event.player)
            }
        }, this)
    }

    @Args("hello world")
    fun helloWorld(sender: CommandSender) {
        sender.sendMessage("Hello Command Sender!")
    }

    @Args("hello player")
    fun helloPlayer(sender: Player) {
        sender.sendMessage("Hello Player!")
    }

    @Args("hello server")
    fun helloServer() {
        Bukkit.broadcast(text("Hello Server!"))
    }
}