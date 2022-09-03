package net.stckoverflw.quotebot.command

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalMember
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondingPaginator
import dev.kord.rest.builder.message.create.allowedMentions
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.safeMember
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.stckoverflw.quotebot.Quote
import net.stckoverflw.quotebot.QuoteBotCommandModule
import net.stckoverflw.quotebot.QuoteDatabase
import net.stckoverflw.quotebot.util.getGuildSettings
import net.stckoverflw.quotebot.util.memberPermissionCheck
import org.litote.kmongo.eq

suspend fun QuoteBotCommandModule.listQuoteCommand() = publicSlashCommand(::ListQuotesArguments) {
    name = "list"
    description = "List all quotes of a specific Member"
    allowInDms = false

    action {
        val guildSettings = safeGuild.getGuildSettings()

        if (!memberPermissionCheck(guildSettings)) return@action

        val member = (arguments.member ?: safeMember).asMember()

        val quotes = QuoteDatabase.quoteCollection.find(Quote::quoted eq member.id).toList()

        if (quotes.isNotEmpty()) {
            val strings = quotes.map {quote: Quote ->
                val localDate = quote.timestamp.toLocalDateTime(guildSettings.timezone ?: TimeZone.UTC)
                "**\"${quote.text}\"** - " +
                        localDate.monthNumber + "/" + localDate.dayOfMonth + "/" + localDate.year +
                        if (quote.message != null) " - [Jump to message](<${quote.message}>)" else ""
            }.asReversed()
            respondingPaginator {
                strings.chunked(4).forEach { quotes ->
                    page {
                        title = "Quotes of ${member.mention}"
                        description = quotes.joinToString("\n\n")
                    }
                }
            }.send()
        } else {
            respond {
                content = "No quotes found for ${member.mention}"
                allowedMentions {
                    users.removeAll { true }
                }
            }
        }
    }
}

class ListQuotesArguments : Arguments() {
    val member by optionalMember {
        name = "member"
        description = "The member you want to list the quotes of"
    }
}
