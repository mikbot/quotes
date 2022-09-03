package net.stckoverflw.quotebot.command

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.boolean
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.role
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.behavior.interaction.suggestString
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import kotlinx.datetime.TimeZone
import net.stckoverflw.quotebot.QuoteBotCommandModule
import net.stckoverflw.quotebot.util.getGuildSettings
import net.stckoverflw.quotebot.util.updateGuildSettings

suspend fun QuoteBotCommandModule.settingsCommand() = ephemeralSlashCommand {
    name = "quote-settings"
    description = "<not used>"
    guildAdminOnly()

    ephemeralSubCommand(::QuoteChannelArguments) {
        name = "quote-channel"
        description = "Set the channel where the bot will send new quotes"
        guildAdminOnly()

        action {
            val guildSettings = safeGuild.getGuildSettings()

            updateGuildSettings(safeGuild, guildSettings.copy(quoteChannel = arguments.channel.id))

            respond {
                content = "Quote channel set to ${arguments.channel.mention}"
            }
        }
    }

    ephemeralSubCommand(::TimezoneArguments) {
        name = "time-zone"
        description = "Set the time zone to display the days in"
        guildAdminOnly()

        action {
            val guildSettings = safeGuild.getGuildSettings()

            updateGuildSettings(safeGuild, guildSettings.copy(timezone = TimeZone.of(arguments.timezone)))

            respond {
                content = "The Server Time-Zone was set to `${arguments.timezone}`"
            }
        }
    }

    ephemeralSubCommand(::OnlySpecifiedRolesToggleArguments) {
        name = "only-specified-roles"
        description = "Set if only the specified roles can use the bot"
        guildAdminOnly()

        action {
            val guildSettings = safeGuild.getGuildSettings()

            if (guildSettings.onlySpecified == arguments.toggle) {
                respond {
                    content = "Only specified roles is already `${arguments.toggle}`"
                }
                return@action
            }

            updateGuildSettings(safeGuild, guildSettings.copy(onlySpecified = arguments.toggle))

            respond {
                content = "Only specified roles was set to `${arguments.toggle}`"
            }
        }
    }

    ephemeralSubCommand(::SpecifyRoleArguments) {
        name = "role"
        description = "Allow or disallow a role to use the bot"
        guildAdminOnly()

        action {
            val guildSettings = safeGuild.getGuildSettings()

            if (guildSettings.allowedRoles.contains(arguments.role.id)) {
                updateGuildSettings(
                    safeGuild,
                    guildSettings.copy(allowedRoles = guildSettings.allowedRoles - arguments.role.id)
                )
                respond {
                    content = "The Role ${arguments.role.mention} was disallowed to use the bot"
                }
                return@action
            }

            updateGuildSettings(safeGuild, guildSettings.copy(allowedRoles = guildSettings.allowedRoles + arguments.role.id))

            respond {
                content = "The Role ${arguments.role.mention} was allowed to use the bot"
            }
        }
    }
}

private class QuoteChannelArguments : Arguments() {
    val channel by channel {
        name = "channel"
        description = "The channel where the bot will send new quotes"
    }
}

private class TimezoneArguments : Arguments() {
    val timezone by string {
        name = "timezone"
        description = "The timezone the bot will use to display the day"

        autoComplete {interaction ->
            suggestString {

                TimeZone.availableZoneIds.filter {
                        id -> id.startsWith(interaction.interaction.focusedOption.value, true)
                }.take(15).map {
                    choice(it, it)
                }
            }
        }
    }
}

private class OnlySpecifiedRolesToggleArguments : Arguments() {
    val toggle by boolean {
        name = "toggle"
        description = "Whether only specified roles can use the bot"
    }
}

private class SpecifyRoleArguments : Arguments() {
    val role by role {
        name = "role"
        description = "The role to allow or disallow to use the bot"
    }
}
