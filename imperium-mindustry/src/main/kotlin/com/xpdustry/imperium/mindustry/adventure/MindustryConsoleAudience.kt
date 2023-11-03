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

import arc.util.Log
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.flattener.ComponentFlattener
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer
import net.kyori.ansi.ColorLevel

class MindustryConsoleAudience(flattener: ComponentFlattener) : MindustryAudience {
    private val renderer =
        ANSIComponentSerializer.builder()
            .flattener(flattener)
            .colorLevel(ColorLevel.TRUE_COLOR)
            .build()

    @Suppress("OVERRIDE_DEPRECATION", "UnstableApiUsage")
    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        Log.info(renderer.serialize(message))
    }
}
