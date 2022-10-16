/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package de.notjansel.sbbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import de.notjansel.sbbot.extensions.*
import dev.kord.common.entity.Snowflake
import java.util.*

val TEST_SERVER_ID = Snowflake(
    env("TEST_SERVER").toLong()  // Get the test server ID from the env vars or a .env file
)

val TEST_SERVER_CHANNEL_ID = Snowflake(
    env("TEST_CHANNEL").toLong() // Get the test channel ID from the env vars or a .env file
)

private val TOKEN = env("TOKEN")   // Get the bot' token from the env vars or a .env file
suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        extensions {
            add(::Skyblock)
            add(::Startup)
            add(::Modrinth)
            add(::About)
            add(::Build)
        }
        i18n {
            defaultLocale = Locale.ENGLISH
        }
    }
    bot.start()
}
