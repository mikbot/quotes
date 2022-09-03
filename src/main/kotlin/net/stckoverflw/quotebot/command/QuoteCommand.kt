package net.stckoverflw.quotebot.command

import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import net.stckoverflw.quotebot.QuoteBotCommandModule

suspend fun QuoteBotCommandModule.quoteCommand() = publicSlashCommand {
    name = "quote"
    description = "<not used>"

    addQuoteCommand()
    addMessageAsQuoteCommand()
}