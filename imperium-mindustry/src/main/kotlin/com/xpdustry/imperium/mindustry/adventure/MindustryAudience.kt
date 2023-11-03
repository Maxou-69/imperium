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

import com.google.common.collect.Iterables
import java.util.function.Consumer
import java.util.function.Predicate
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.kyori.adventure.text.ComponentLike

interface MindustryAudience : Audience {

    fun sendInfoMessage(message: ComponentLike) = Unit

    fun showHUD(message: ComponentLike) = Unit

    fun hideHUD() = Unit

    fun sendAlert(message: ComponentLike) = Unit

    fun sendToast(icon: Char, message: ComponentLike) = Unit

    fun forEachMindustryAudience(action: Consumer<MindustryAudience>) {
        action.accept(this)
    }

    fun filterMindustryAudience(filter: Predicate<MindustryAudience>): MindustryAudience {
        return if (filter.test(this)) this else EMPTY
    }

    companion object {
        val EMPTY: MindustryAudience = ForwardingMindustryAudience { emptyList() }
    }
}

internal fun interface ForwardingMindustryAudience : MindustryAudience, ForwardingAudience {

    override fun audiences(): Iterable<MindustryAudience>

    override fun sendInfoMessage(message: ComponentLike) {
        for (audience in audiences()) audience.sendInfoMessage(message)
    }

    override fun showHUD(message: ComponentLike) {
        for (audience in audiences()) audience.showHUD(message)
    }

    override fun hideHUD() {
        for (audience in audiences()) audience.hideHUD()
    }

    override fun sendAlert(message: ComponentLike) {
        for (audience in audiences()) audience.sendAlert(message)
    }

    override fun sendToast(icon: Char, message: ComponentLike) {
        for (audience in audiences()) audience.sendToast(icon, message)
    }

    override fun forEachMindustryAudience(action: Consumer<MindustryAudience>) {
        for (audience in audiences()) action.accept(audience)
    }

    override fun filterMindustryAudience(filter: Predicate<MindustryAudience>): MindustryAudience =
        ForwardingMindustryAudience {
            Iterables.filter(audiences(), filter::test)
        }
}
