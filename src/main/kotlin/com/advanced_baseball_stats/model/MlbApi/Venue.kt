package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("link")
    val link: String
)
{
}