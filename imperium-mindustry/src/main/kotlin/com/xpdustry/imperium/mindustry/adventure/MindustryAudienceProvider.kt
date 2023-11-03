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
package com.xpdustry.imperium.mindustry.adventure

import com.xpdustry.imperium.common.application.ImperiumApplication
import com.xpdustry.imperium.common.misc.muuidToUuid
import fr.xpdustry.distributor.api.DistributorProvider
import fr.xpdustry.distributor.api.event.EventHandler
import fr.xpdustry.distributor.api.util.MUUID
import fr.xpdustry.distributor.api.util.Priority
import fr.xpdustry.distributor.api.util.Tristate
import java.util.UUID
import kotlin.jvm.optionals.getOrDefault
import mindustry.game.EventType
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.AudienceProvider
import net.kyori.adventure.text.flattener.ComponentFlattener

internal lateinit var IMPERIUM_AUDIENCE_PROVIDER: MindustryAudienceProvider

interface MindustryAudienceProvider : AudienceProvider {
    fun player(playerId: MUUID): MindustryAudience = player(muuidToUuid(playerId.uuid))

    override fun all(): MindustryAudience

    override fun console(): MindustryAudience

    override fun players(): MindustryAudience

    override fun player(playerId: UUID): MindustryAudience

    override fun permission(permission: String): MindustryAudience

    override fun world(world: Key): MindustryAudience

    override fun server(serverName: String): MindustryAudience
}

class SimpleMindustryAudienceProvider(private val flattener: ComponentFlattener) :
    MindustryAudienceProvider, ImperiumApplication.Listener {

    private val players = mutableMapOf<UUID, MindustryPlayerAudience>()
    private val console = MindustryConsoleAudience(flattener)

    @EventHandler(priority = Priority.HIGHEST)
    internal fun onPlayerJoin(event: EventType.PlayerJoin) {
        players[muuidToUuid(event.player.uuid())] = MindustryPlayerAudience(event.player, flattener)
    }

    @EventHandler(priority = Priority.LOWEST)
    internal fun onPlayerQuit(event: EventType.PlayerLeave) {
        players.remove(muuidToUuid(event.player.uuid()))
    }

    override fun all(): MindustryAudience = ForwardingMindustryAudience { players.values + console }

    override fun console(): MindustryAudience = console

    override fun players(): MindustryAudience = ForwardingMindustryAudience { players.values }

    override fun player(playerId: UUID): MindustryAudience =
        players[playerId] ?: MindustryAudience.EMPTY

    override fun permission(permission: String): MindustryAudience =
        players().filterMindustryAudience {
            it.pointers()
                .get(MUUID_POINTER)
                .map { muuid ->
                    DistributorProvider.get()
                        .permissionService
                        .getPlayerPermission(muuid, permission) == Tristate.TRUE
                }
                .getOrDefault(false)
        }

    override fun world(world: Key): MindustryAudience =
        if (world.namespace() == "mindustry" && world.value() == "local") players()
        else MindustryAudience.EMPTY

    override fun server(serverName: String): MindustryAudience =
        if (serverName == "local") players() else MindustryAudience.EMPTY

    override fun flattener(): ComponentFlattener = flattener

    override fun close() = Unit
}
