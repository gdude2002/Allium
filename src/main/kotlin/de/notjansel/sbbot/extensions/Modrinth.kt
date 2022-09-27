package de.notjansel.sbbot.extensions

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingInt
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondEphemeral
import dev.kord.common.annotation.KordPreview
import de.notjansel.sbbot.TEST_SERVER_ID
import de.notjansel.sbbot.utils.*
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.core.cache.data.ChatComponentData
import dev.kord.core.entity.component.ButtonComponent
import dev.kord.core.entity.component.Component
import dev.kord.rest.builder.component.ButtonBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.create.embed

@OptIn(KordPreview::class)
class Modrinth : Extension() {
    override val name = "modrinth"
    override suspend fun setup() {
        publicSlashCommand {
            name = "modrinth"
            description = "What is Modrinth?"
            guild(TEST_SERVER_ID)
            action {
                respond {
                    embed {
                        title = "What is Modrinth?"
                        description = """
                            Modrinth is a relatively new modding platform where you can publish 
                            your mods. It gained alot of popularity in the last year, especially 
                            in the **Open Source Fabric Modding Community**. A notible modder, 
                            who published releases to Modrinth was Jellysquid, author of Sodium, 
                            Lithium and Phosphor, 3 of the most popular fabric mods to date.
                            Modrinth itself gained a great userbase even because it is more
                            friendly and appealing in general in comparison to CurseForge.
                            Although you may miss the creator Payouts right now, they are
                            actively working to get things, asked by the community, done for
                            the community. Notable user-requested features were the ability
                            to upload plugins, resource packs and modpacks
                        """.trimIndent()
                    }
                }
            }
            publicSubCommand(::UserSearchQuery) {
                name = "user"
                description = "Search for a User"
                guild(TEST_SERVER_ID)
                action {
                    val url = "https://api.modrinth.com/v2/user/${arguments.query}"
                    if (arguments.query ==  "") {
                        respond { content = "No query was given, aborting search." }
                        return@action
                    }
                    val request = webRequest(url)
                    if (request.statusCode() == 404) {
                        respond { content = "No user found under query ${arguments.query}." }
                    }
                    val user = JsonParser.parseString(request.body()).asJsonObject
                    respond {
                        embed {
                            title = user["name"].asString
                            description = user["bio"].asString
                            thumbnail {
                                this.url = user["avatar_url"].asString
                            }
                        }
                    }
                }
            }
            publicSubCommand(::ModrinthSearchQuery) {
                name = "project"
                description = "Search for a mod/plugin"
                guild(TEST_SERVER_ID)
                action {
                    val url = "https://api.modrinth.com/v2/search?limit=${arguments.limit}&facets=[[%22project_type:mod%22]]&query=${arguments.query}"
                    if (arguments.query == "") {
                        respond { content = "No query was given, aborting search." }
                        return@action
                    }
                    val request = webRequest(url)
                    val response = JsonParser.parseString(request.body()).asJsonObject
                    val hits: JsonArray = response["hits"].asJsonArray
                    if (hits.isEmpty) {
                        respond {
                            content = "No results found."
                        }
                        return@action
                    }
                    if (response["total_hits"].asInt == 1) {
                        val hit = hits.get(0).asJsonObject
                        respond {
                            embed {
                                this.title = hit["title"].asString
                                this.url = "https://modrinth.com/mod/${hit["slug"]}"
                                thumbnail {
                                    this.url = hit["icon_url"].asString
                                }
                                this.description = hit["description"].asString
                                field("Latest Version", true) { hit["latest_version"].asString }
                                field("Client/Server Side", true) { "Client: ${hit["client_side"]}\nServer: ${hit["server_side"]}" }
                                field("Downloads", true) { hit["downloads"].asString }
                                field("Author", true) { hit["author"].asString }
                                // field("Last Update", true) { "<t:${parseIsoString(hit["date_modified"].asString).inWholeSeconds}>" }
                                field("License", true) { hit["license"].asString }
                                footer {
                                    this.text = "Modrinth | ${hit["author"]}"
                                }
                            }
                        }
                        return@action
                    }
                    respondEphemeral { content = "Paginator not implemented yet." }
                }
            }
            publicSubCommand(::ModrinthSearchQuery) {
                name = "resourcepack"
                description = "Search for a resource pack"
                guild(TEST_SERVER_ID)
                action {
                    val url = "https://api.modrinth.com/v2/search?limit=${arguments.limit}&facets=[[%22project_type:resourcepack%22]]&query=${arguments.query}"
                    if (arguments.query == "") {
                        respond { content = "No query was given, aborting search." }
                        return@action
                    }
                    respondEphemeral { content = "Not implemented yet." }
                }
            }
            publicSubCommand(::ModrinthSearchQuery) {
                name = "modpack"
                description = "Search for a Modpack"
                guild(TEST_SERVER_ID)
                action {
                    val url = "https://api.modrinth.com/v2/search?limit=${arguments.limit}&facets=[[%22project_type:modpack%22]]&query=${arguments.query}"
                    if (arguments.query == "") {
                        respond { content = "No query was given, aborting search." }
                        return@action
                    }
                    respondEphemeral { content = "Not implemented yet." }
                }
            }
        }
    }
    inner class ModrinthSearchQuery : Arguments() {
        val query by defaultingString {
            name = "query"
            description = "Query to search"
            defaultValue = ""
        }
        val limit by defaultingInt {
            name = "limit"
            description = "limit search results"
            defaultValue = 5
        }
    }
    inner class UserSearchQuery : Arguments() {
        val query by defaultingString {
            name = "query"
            description = "User to search up"
            defaultValue = ""
            require(true)
        }
    }
}
