package com.advanced_baseball_stats.v2.helper.derserializer

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object InfinityAwareDoubleSerializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("Double", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Double {
        val value = decoder.decodeString()
        return when (value) {
            "Infinity", "+Infinity" -> 0.0
            "-Infinity"             -> Double.NEGATIVE_INFINITY
            "NaN"                   -> Double.NaN
            else                    -> value.toDouble()
        }
    }

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeString(value.toString())
    }
}