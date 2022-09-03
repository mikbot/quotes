package net.stckoverflw.quotebot

import com.kotlindiscord.kord.extensions.builders.ExtensibleBotBuilder
import dev.schlaubi.mikbot.plugin.api.Plugin
import dev.schlaubi.mikbot.plugin.api.PluginMain
import dev.schlaubi.mikbot.plugin.api.PluginWrapper
import net.stckoverflw.quotebot.command.listQuoteCommand
import net.stckoverflw.quotebot.command.quoteCommand
import net.stckoverflw.quotebot.command.settingsCommand
import com.kotlindiscord.kord.extensions.extensions.Extension as KordExtension

@PluginMain
class QuotePlugin(wrapper: PluginWrapper) : Plugin(wrapper) {

    override fun ExtensibleBotBuilder.ExtensionsBuilder.addExtensions() {
        add(::QuoteBotCommandModule)
    }

}

class QuoteBotCommandModule : KordExtension() {
    override val name: String = "quote bot command module"

    override suspend fun setup() {
        settingsCommand()

        quoteCommand()
        listQuoteCommand()
    }
}