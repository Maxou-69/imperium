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
package com.xpdustry.imperium.common.misc

import java.awt.Color
import java.nio.ByteBuffer
import java.util.Base64
import java.util.UUID
import java.util.zip.CRC32

typealias MindustryUUID = String

typealias MindustryUSID = String

val MINDUSTRY_ACCENT_COLOR = Color(0xffd37f)

fun CharSequence.stripMindustryColors(): String {
    val out = StringBuilder(length)
    var index = 0
    while (index < length) {
        val char = this[index]
        if (char == '[') {
            if (getOrNull(index + 1) == '[') {
                out.append(char)
                index += 2
            } else {
                while (index < length && this[index] != ']') {
                    index++
                }
                index++
            }
        } else {
            out.append(char)
            index++
        }
    }
    return out.toString()
}

fun muuidToUuid(muuid: String): UUID {
    val bytes = Base64.getDecoder().decode(muuid)
    val buffer = ByteBuffer.allocate(16)
    buffer.put(bytes, 0, 4) // First 4 bytes
    buffer.putShort(0) // Next 2 bytes
    buffer.putShort(0x8000.toShort()) // Version is 4 bits, put set to v8
    buffer.putShort(0x8000.toShort()) // Variant is 2 bits, put set to IETF
    buffer.putShort(0) // Next 2 bytes
    buffer.put(bytes, 4, 4) // Last 4 bytes
    buffer.flip()
    return UUID(buffer.long, buffer.long)
}

fun uuidToMuuid(uuid: UUID): String {
    val buffer = ByteBuffer.allocate(16)
    buffer.putLong(uuid.mostSignificantBits)
    buffer.putLong(uuid.leastSignificantBits)
    buffer.flip()

    val bytes = ByteArray(16)
    buffer.get(bytes, 0, 4)
    if (buffer.getShort() != 0.toShort())
        throw IllegalArgumentException("UUID is not filled with 0s")
    if (buffer.getShort() != 0x8000.toShort())
        throw IllegalArgumentException("UUID is not version 8")
    if (buffer.getShort() != 0x8000.toShort())
        throw IllegalArgumentException("UUID is not variant 2")
    if (buffer.getShort() != 0.toShort())
        throw IllegalArgumentException("UUID is not filled with 0s")
    buffer.get(bytes, 4, 4)
    buffer.flip()

    // Add the crc32 checksum
    val crc32 = CRC32()
    crc32.update(bytes, 0, 8)
    buffer.flip()
    buffer.putInt(crc32.value.toInt())
    buffer.flip()

    return Base64.getEncoder().encodeToString(bytes)
}
