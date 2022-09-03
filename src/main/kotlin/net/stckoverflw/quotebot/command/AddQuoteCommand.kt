package net.stckoverflw.quotebot.command

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.SlashCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.member
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.rest.builder.message.create.allowedMentions
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.safeMember
import net.stckoverflw.quotebot.Quote
import net.stckoverflw.quotebot.util.addQuote
import net.stckoverflw.quotebot.util.getGuildSettings
import net.stckoverflw.quotebot.util.memberPermissionCheck

suspend fun SlashCommand<*, *>.addQuoteCommand() = publicSubCommand(::AddQuoteCommandArguments) {
    name = "add"
    description = "Quote someone"

    action {
        val guildSettings = safeGuild.getGuildSettings()

        if (!memberPermissionCheck(guildSettings)) return@action

        val quoteText = arguments.quote
        val quotedMember = arguments.quoted

        val quote = Quote(
            quoteText,
            quotedMember.id,
            safeMember.id,
            safeGuild.id
        )

        addQuote(quote, guildSettings, safeGuild.asGuild())

        respond {
            content = "Quote `$quoteText` by ${quotedMember.mention} added!"
            allowedMentions {
                this.users.removeAll { true }
            }
        }
    }
}

class AddQuoteCommandArguments : Arguments() {
    val quote by string {
        name = "quote"
        description = "The quote you want to add"
    }
    val quoted by member {
        name = "quoted"
        description = "The person the quote is from"
    }
}
