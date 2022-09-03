package net.stckoverflw.quotebot

import com.kotlindiscord.kord.extensions.koin.KordExKoinComponent
import dev.kord.common.entity.Snowflake
import dev.schlaubi.mikbot.plugin.api.io.getCollection
import dev.schlaubi.mikbot.plugin.api.util.database
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

object QuoteDatabase : KordExKoinComponent {

    val quoteCollection = database.getCollection<Quote>("quotes")
    val settingsCollection = database.getCollection<QuoteSettings>("quote_settings")

}

@Serializable
data class Quote(
    val text: String,
    val quoted: Snowflake,
    val addedBy: Snowflake,
    val guild: Snowflake,
    val message: String? = null,
    val timestamp: Instant = Clock.System.now(),
    @Contextual @SerialName("_id") val id: Id<Quote> = newId()
)

@Serializable
data class QuoteSettings(
    @SerialName("_id") val guild: Snowflake,
    val timezone: TimeZone? = null,
    val quoteChannel: Snowflake? = null,
    val onlySpecified: Boolean = false,
    val allowedRoles: List<Snowflake> = emptyList(),
)
