package io.github.woneum.glyph.v1_21_10

import com.mojang.brigadier.context.CommandContext
import io.github.woneum.glyph.GlyphContext
import io.github.woneum.glyph.ref.getValue
import io.github.woneum.glyph.ref.weak
import net.minecraft.commands.CommandSourceStack
import org.bukkit.command.CommandSender
import java.util.WeakHashMap

class NMSGlyphContext(
    sender: CommandSender,
    handle: CommandContext<CommandSourceStack>
): GlyphContext(sender) {
    internal val handle by weak(handle)
}