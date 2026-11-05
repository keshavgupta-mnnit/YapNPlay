package com.kglabs28.yapnplay.appengine.features.nearby.transport

/**
 * Splits a message into radio-sized chunks and puts them back together.
 * Each chunk starts with an 8-byte header: message id (4 bytes), chunk
 * index and chunk count (2 bytes each, big-endian).
 */
object PayloadChunks {
    const val HEADER_SIZE = 8

    fun split(messageId: Int, bytes: ByteArray, maxChunk: Int): List<ByteArray> {
        val body = maxChunk - HEADER_SIZE
        val count = maxOf(1, (bytes.size + body - 1) / body)
        return List(count) { index ->
            val from = index * body
            val to = minOf(bytes.size, from + body)
            val chunk = ByteArray(HEADER_SIZE + (to - from))
            writeInt(chunk, 0, messageId)
            writeShort(chunk, 4, index)
            writeShort(chunk, 6, count)
            bytes.copyInto(chunk, HEADER_SIZE, from, to)
            chunk
        }
    }

    /** Collects one sender's chunks; [add] answers the whole message once its last chunk is in. */
    class Reassembler {
        private val parts = mutableMapOf<Int, Array<ByteArray?>>()

        fun add(chunk: ByteArray): ByteArray? {
            if (chunk.size < HEADER_SIZE) return null
            val id = readInt(chunk, 0)
            val index = readShort(chunk, 4)
            val count = readShort(chunk, 6)
            if (count <= 0 || index !in 0 until count) return null
            if (count == 1) return chunk.copyOfRange(HEADER_SIZE, chunk.size)
            val slots = parts.getOrPut(id) { arrayOfNulls(count) }
            if (slots.size != count) return null
            slots[index] = chunk.copyOfRange(HEADER_SIZE, chunk.size)
            if (slots.any { it == null }) return null
            parts.remove(id)
            val whole = ByteArray(slots.sumOf { it!!.size })
            var at = 0
            slots.forEach { part ->
                part!!.copyInto(whole, at)
                at += part.size
            }
            return whole
        }
    }

    private fun writeInt(target: ByteArray, at: Int, value: Int) {
        for (i in 0 until INT_BYTES) target[at + i] = (value shr (BYTE_BITS * (INT_BYTES - 1 - i))).toByte()
    }

    private fun writeShort(target: ByteArray, at: Int, value: Int) {
        target[at] = (value shr BYTE_BITS).toByte()
        target[at + 1] = value.toByte()
    }

    private fun readInt(source: ByteArray, at: Int): Int {
        var value = 0
        for (i in 0 until INT_BYTES) value = (value shl BYTE_BITS) or (source[at + i].toInt() and BYTE_MASK)
        return value
    }

    private fun readShort(source: ByteArray, at: Int): Int =
        ((source[at].toInt() and BYTE_MASK) shl BYTE_BITS) or (source[at + 1].toInt() and BYTE_MASK)

    private const val INT_BYTES = 4
    private const val BYTE_BITS = 8
    private const val BYTE_MASK = 0xFF
}
