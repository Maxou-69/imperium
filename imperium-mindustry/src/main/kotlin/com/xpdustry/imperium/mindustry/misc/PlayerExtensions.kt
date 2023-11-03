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
package com.xpdustry.imperium.mindustry.misc

import com.xpdustry.imperium.common.misc.toInetAddress
import com.xpdustry.imperium.common.security.Identity
import com.xpdustry.imperium.mindustry.adventure.IMPERIUM_AUDIENCE_PROVIDER
import fr.xpdustry.distributor.api.util.MUUID
import java.time.Instant
import mindustry.gen.Call
import mindustry.gen.Player
import net.kyori.adventure.audience.Audience

val Player.identity: Identity.Mindustry
    get() = Identity.Mindustry(info.plainLastName(), uuid(), usid(), con.address.toInetAddress())

val Player.joinTime: Instant
    get() = Instant.ofEpochMilli(con.connectTime)

val Player.audience: Audience
    get() = IMPERIUM_AUDIENCE_PROVIDER.player(MUUID.of(this))

fun Player.showInfoMessage(message: String): Unit = Call.infoMessage(con, message)
