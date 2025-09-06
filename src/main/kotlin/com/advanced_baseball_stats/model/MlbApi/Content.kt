package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("link")
    val link: String
)
{
}