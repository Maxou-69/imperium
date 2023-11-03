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

import mindustry.gen.Call
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.ComponentLike

fun Audience.sendInfoMessage(message: ComponentLike) = forEachAudience {
    if (it is MindustryPlayerAudience) {
        val renderer = MindustryComponentRenderer()
        it.flattener.flatten(message.asComponent(), renderer)
        Call.infoMessage(it.player.con, renderer.toString())
    }
}

fun Audience.showHUD(message: ComponentLike) = forEachAudience {
    if (it is MindustryPlayerAudience) {
        val renderer = MindustryComponentRenderer()
        it.flattener.flatten(message.asComponent(), renderer)
        Call.setHudTextReliable(it.player.con, renderer.toString())
    }
}

fun Audience.hideHUD() = forEachAudience {
    if (it is MindustryPlayerAudience) {
        Call.hideHudText(it.player.con)
    }
}

fun Audience.sendAnnouncement(message: ComponentLike) = forEachAudience {
    if (it is MindustryPlayerAudience) {
        val renderer = MindustryComponentRenderer()
        it.flattener.flatten(message.asComponent(), renderer)
        Call.announce(renderer.toString())
    }
}

fun Audience.sendWarningToast(icon: Char, message: ComponentLike) = forEachAudience {
    if (it is MindustryPlayerAudience) {
        val renderer = MindustryComponentRenderer()
        it.flattener.flatten(message.asComponent(), renderer)
        Call.warningToast(it.player.con, icon.code, renderer.toString())
    }
}
