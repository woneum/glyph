package io.github.woneum.glyph

import io.github.woneum.glyph.loader.LibraryLoader
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

interface Glyph {
    companion object : Glyph by LibraryLoader.loadNMS(Glyph::class.java)

    fun compute(plugin: JavaPlugin, vararg handlers: Any)

    fun sendCommandsPacket(player: Player)
}