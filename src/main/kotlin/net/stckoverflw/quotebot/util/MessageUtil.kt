package net.stckoverflw.quotebot.util

import dev.kord.core.entity.Message

suspend fun Message.buildMessageLink() =
    "https://discord.com/channels/${data.guildId.value?.value ?: getGuild().id}/$channelId/$id"