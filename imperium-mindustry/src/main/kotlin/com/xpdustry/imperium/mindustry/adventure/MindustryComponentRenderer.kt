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

import net.kyori.adventure.text.flattener.FlattenerListener
import net.kyori.adventure.text.format.Style

internal class MindustryComponentRenderer : FlattenerListener {
    private val builder = StringBuilder()

    override fun pushStyle(style: Style) {
        style.color()?.asHexString()?.let { builder.append("[$it]") }
    }

    override fun component(text: String) {
        builder.append(text.replace("[", "[["))
    }

    override fun popStyle(style: Style) {
        if (style.color() != null) builder.append("[]")
    }

    override fun toString(): String = builder.toString()
}
