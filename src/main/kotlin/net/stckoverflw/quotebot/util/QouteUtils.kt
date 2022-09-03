package net.stckoverflw.quotebot.util

import com.kotlindiscord.kord.extensions.commands.application.slash.EphemeralSlashCommandContext
import com.kotlindiscord.kord.extensions.commands.application.slash.PublicSlashCommandContext
import com.kotlindiscord.kord.extensions.commands.application.slash.SlashCommandContext
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import dev.schlaubi.mikbot.plugin.api.util.safeMember
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.stckoverflw.quotebot.Quote
import net.stckoverflw.quotebot.QuoteDatabase
import net.stckoverflw.quotebot.QuoteSettings

suspend fun addQuote(quote: Quote, guildSettings: QuoteSettings, guild: Guild) {

    QuoteDatabase.quoteCollection.save(quote)

    guildSettings.quoteChannel?.let {
        guild.getChannelOfOrNull<TextChannel>(it)?.let { channel ->
            val localDate = quote.timestamp.toLocalDateTime(guildSettings.timezone ?: TimeZone.UTC)
            channel.createMessage {
                content = "**\"${quote.text}\"**" + " - " + "<@${quote.quoted}> - " +
                        localDate.monthNumber + "/" + localDate.dayOfMonth + "/" + localDate.year
//                        if (quote.message != null) " - [Jump to message](<${quote.message}>)" else "" // idk why this doesn't work, I've seen it work in other bots
            }
        }
    }

}

suspend fun SlashCommandContext<*, *>.memberPermissionCheck(guildSettings: QuoteSettings): Boolean {

    if (!guildSettings.onlySpecified) {
        return true
    }

    var allowUserUse = false
    safeMember.asMember().roles.onEach {
        if (guildSettings.allowedRoles.contains(it.id)) {
            allowUserUse = true
        }
    }

    if (!allowUserUse) {
        val content = "You don't have the permission to use the Quote Feature!"

        if (this is PublicSlashCommandContext<*>) {
            respond {
                this.content = content
            }
        } else if (this is EphemeralSlashCommandContext<*>) {
            respond {
                this.content = content
            }
        }
    }

    return allowUserUse
}
