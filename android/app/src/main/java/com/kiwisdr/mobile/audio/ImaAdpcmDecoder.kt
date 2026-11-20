package com.kiwisdr.mobile.audio

class ImaAdpcmDecoder {

    private var predictor: Int = 0
    private var stepIndex: Int = 0

    companion object {
        private val STEP_TABLE = intArrayOf(
            7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
            19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
            50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
            130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
            337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
            876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
            2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
            5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
            15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767
        )

        private val INDEX_TABLE = intArrayOf(
            -1, -1, -1, -1, 2, 4, 6, 8,
            -1, -1, -1, -1, 2, 4, 6, 8
        )
    }

    fun reset() {
        predictor = 0
        stepIndex = 0
    }

    fun decodeFrame(input: ByteArray, offset: Int, length: Int): ShortArray {
        if (length < 4) return ShortArray(0)

        // Read header if present (2 bytes predictor, 1 byte index, 1 byte reserved)
        predictor = (input[offset].toInt() and 0xFF) or ((input[offset + 1].toInt() and 0xFF) shl 8)
        if (predictor > 32767) predictor -= 65536
        stepIndex = input[offset + 2].toInt() and 0xFF
        if (stepIndex > 88) stepIndex = 88

        val sampleCount = (length - 4) * 2
        val pcm = ShortArray(sampleCount)
        var outIdx = 0

        for (i in (offset + 4) until (offset + length)) {
            val byteVal = input[i].toInt() and 0xFF

            // Low nibble first
            val nibble1 = byteVal and 0x0F
            pcm[outIdx++] = decodeNibble(nibble1)

            // High nibble
            val nibble2 = (byteVal shr 4) and 0x0F
            pcm[outIdx++] = decodeNibble(nibble2)
        }

        return pcm
    }

    private fun decodeNibble(nibble: Int): Short {
        val step = STEP_TABLE[stepIndex]
        var diff = step shr 3

        if ((nibble and 1) != 0) diff += step shr 2
        if ((nibble and 2) != 0) diff += step shr 1
        if ((nibble and 4) != 0) diff += step

        if ((nibble and 8) != 0) {
            predictor -= diff
        } else {
            predictor += diff
        }

        stepIndex += INDEX_TABLE[nibble]
        if (stepIndex < 0) stepIndex = 0
        if (stepIndex > 88) stepIndex = 88

        if (predictor > 32767) predictor = 32767
        if (predictor < -32768) predictor = -32768

        return predictor.toShort()
    }
}
