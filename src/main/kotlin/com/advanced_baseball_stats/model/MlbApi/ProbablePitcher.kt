package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProbablePitcher(
    @SerialName("id")
    val id: Int,
    @SerialName("fullName")
    val fullName: String,
    @SerialName("link")
    val link: String
)
{
}