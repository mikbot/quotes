package net.stckoverflw.quotebot.command

import com.kotlindiscord.kord.extensions.DiscordRelayedException
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.SlashCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.message
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.getJumpUrl
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.allowedMentions
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import dev.schlaubi.mikbot.plugin.api.util.safeMember
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import net.stckoverflw.quotebot.Quote
import net.stckoverflw.quotebot.util.addQuote
import net.stckoverflw.quotebot.util.buildMessageLink
import net.stckoverflw.quotebot.util.getGuildSettings
import net.stckoverflw.quotebot.util.memberPermissionCheck

suspend fun SlashCommand<*, *>.addMessageAsQuoteCommand() = publicSubCommand(::AddMessageAsQuoteArguments) {
    name = "message-as-quote"
    description = "Add a message as a quote"

    action {
        val guildSettings = safeGuild.getGuildSettings()

        if (!memberPermissionCheck(guildSettings)) return@action

        val message = arguments.message

        val quoteText = message.content
        val quotedMember = message.author ?: throw DiscordRelayedException("Couldn't find the author of the message")

        val quote = Quote(
            quoteText,
            quotedMember.id,
            safeMember.id,
            safeGuild.id,
            message.buildMessageLink(),
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

class AddMessageAsQuoteArguments : Arguments() {
    val message by message {
        name = "message"
        description = "The message to add as a quote"
        autoComplete {
            val lastMessage = it.interaction.channel.asChannel().getLastMessage()

            if (lastMessage != null) {
                val lastFiveMessages = it.interaction.channel.getMessagesBefore(lastMessage.id, 5)

                suggestString {
                    val tag = lastMessage.author?.tag
                    choice(
                        lastMessage.author?.tag + ": " +
                                lastMessage.content.take(100 - (tag?.length?.plus(2) ?: 6)),
                        lastMessage.buildMessageLink()
                    )
                    lastFiveMessages.onEach { message ->
                        val currentTag = message.author?.tag
                        choice(
                            currentTag + ": " +
                                    message.content.take(100 - (currentTag?.length?.plus(2) ?: 6)),
                            message.buildMessageLink()
                        )
                    }.collect()
                }
            }
        }
    }
}