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

import arc.audio.Sound as MindustrySound
import com.xpdustry.imperium.common.misc.camelToKebabCase
import com.xpdustry.imperium.common.misc.muuidToUuid
import fr.xpdustry.distributor.api.util.MUUID
import fr.xpdustry.distributor.api.util.Players
import kotlin.reflect.full.staticProperties
import mindustry.game.Team
import mindustry.gen.Call
import mindustry.gen.Player
import mindustry.gen.Sounds
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.key.Key
import net.kyori.adventure.pointer.Pointer
import net.kyori.adventure.pointer.Pointers
import net.kyori.adventure.sound.Sound as AdventureSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.flattener.ComponentFlattener

val MUUID_POINTER = Pointer.pointer(MUUID::class.java, Key.key("distributor", "muuid"))
val TEAM_POINTER = Pointer.pointer(Team::class.java, Key.key("mindustry", "team"))

class MindustryPlayerAudience(
    internal val player: Player,
    internal val flattener: ComponentFlattener
) : Audience {
    private val pointers: Pointers =
        Pointers.builder()
            .withStatic(MUUID_POINTER, MUUID.of(player))
            .withStatic(Identity.UUID, muuidToUuid(player.uuid()))
            .withDynamic(TEAM_POINTER) { player.team() }
            .withDynamic(Identity.NAME) { player.info.lastName }
            .withDynamic(Identity.DISPLAY_NAME) { Component.text(player.name()) }
            .withDynamic(Identity.LOCALE) { Players.getLocale(player) }
            .build()

    @Suppress("OVERRIDE_DEPRECATION", "UnstableApiUsage")
    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        val renderer = MindustryComponentRenderer()
        flattener.flatten(message, renderer)
        player.sendMessage(renderer.toString())
    }

    override fun sendActionBar(message: Component) {
        val renderer = MindustryComponentRenderer()
        flattener.flatten(message, renderer)
        Call.setHudTextReliable(player.con, renderer.toString())
    }

    override fun playSound(sound: AdventureSound, emitter: AdventureSound.Emitter) {
        if (emitter == AdventureSound.Emitter.self()) {
            playSound(sound)
        }
    }

    override fun playSound(sound: AdventureSound) {
        val result = SOUND_LOOKUP[sound.name()] ?: return
        Call.sound(player.con, result, sound.volume(), sound.pitch(), 1F)
    }

    override fun playSound(sound: AdventureSound, x: Double, y: Double, z: Double) {
        val result = SOUND_LOOKUP[sound.name()] ?: return
        Call.soundAt(player.con, result, sound.volume(), sound.pitch(), x.toFloat(), y.toFloat())
    }

    override fun pointers(): Pointers = pointers

    companion object {
        private val SOUND_LOOKUP = buildMap {
            Sounds::class.staticProperties.forEach {
                if (it.returnType.classifier != MindustrySound::class) return@forEach
                put(Key.key("mindustry", it.name.camelToKebabCase()), it.get() as MindustrySound)
            }
        }
    }
}
