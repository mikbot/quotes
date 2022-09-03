package net.stckoverflw.quotebot.util

import dev.kord.core.behavior.GuildBehavior
import net.stckoverflw.quotebot.QuoteDatabase
import net.stckoverflw.quotebot.QuoteSettings

suspend fun GuildBehavior.getGuildSettings(): QuoteSettings {

    var settings = QuoteDatabase.settingsCollection.findOneById(this.id)

    if (settings == null) {
        settings = QuoteSettings(this.id)
        QuoteDatabase.settingsCollection.save(settings)
    }

    return settings

}

suspend fun updateGuildSettings(guild: GuildBehavior, settings: QuoteSettings) {
    QuoteDatabase.settingsCollection.save(settings)
}
