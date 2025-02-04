/*
 * Imperium, the software collection powering the Xpdustry network.
 * Copyright (C) 2023  Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.imperium.discord.commands

import com.xpdustry.imperium.common.application.ImperiumApplication
import com.xpdustry.imperium.common.bridge.PlayerTracker
import com.xpdustry.imperium.common.command.Command
import com.xpdustry.imperium.common.inject.InstanceManager
import com.xpdustry.imperium.common.inject.get
import com.xpdustry.imperium.common.network.Discovery
import com.xpdustry.imperium.discord.command.InteractionSender
import com.xpdustry.imperium.discord.command.annotation.NonEphemeral
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale
import org.javacord.api.entity.message.embed.EmbedBuilder

class ServerCommand(instances: InstanceManager) : ImperiumApplication.Listener {
    private val discovery = instances.get<Discovery>()
    private val tracker = instances.get<PlayerTracker>()

    @Command(["server", "list"])
    @NonEphemeral
    suspend fun onServerList(actor: InteractionSender) =
        actor.respond(
            EmbedBuilder()
                .setTitle("Server List")
                .setDescription(
                    discovery.servers.values.joinToString(separator = "\n") { "- ${it.name}" }),
        )

    @Command(["server", "player", "joins"])
    @NonEphemeral
    suspend fun onServerPlayerJoin(actor: InteractionSender, server: String) {
        val joins = tracker.getPlayerJoins(server)
        if (joins == null) {
            actor.respond("Server not found.")
            return
        }
        onServerPlayerList(actor, joins, "Join")
    }

    @Command(["server", "player", "quits"])
    @NonEphemeral
    suspend fun onServerPlayerQuit(actor: InteractionSender, server: String) {
        val quits = tracker.getPlayerQuits(server)
        if (quits == null) {
            actor.respond("Server not found.")
            return
        }
        onServerPlayerList(actor, quits, "Quit")
    }

    @Command(["server", "player", "online"])
    @NonEphemeral
    suspend fun onServerPlayerOnline(actor: InteractionSender, server: String? = null) {
        val online =
            if (server != null) {
                tracker.getOnlinePlayers(server)
            } else {
                discovery.servers.flatMap { (name, server) ->
                    if (server.data is Discovery.Data.Mindustry)
                        tracker.getOnlinePlayers(name) ?: emptyList()
                    else emptyList()
                }
            }
        if (online == null) {
            actor.respond("Server not found.")
            return
        }
        onServerPlayerList(actor, online, "Online", time = false)
    }

    private suspend fun onServerPlayerList(
        actor: InteractionSender,
        list: List<PlayerTracker.Entry>,
        name: String,
        time: Boolean = true,
    ) {
        val text = buildString {
            append("```\n")
            if (list.isEmpty()) {
                append("No players found.\n")
            }
            for (entry in list) {
                if (time) {
                    append(TIME_FORMAT.format(entry.timestamp.atOffset(ZoneOffset.UTC)))
                    append(" ")
                }
                append("#")
                append(entry.snowflake)
                append(" ")
                append(entry.player.name)
                append("\n")
            }
            append("```")
        }

        actor.respond(EmbedBuilder().setTitle("Player $name List").setDescription(text))
    }

    companion object {
        private val TIME_FORMAT =
            DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .toFormatter(Locale.ENGLISH)
    }
}
