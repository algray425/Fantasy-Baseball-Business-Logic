package com.advanced_baseball_stats.model.player

import kotlinx.serialization.Serializable

@Serializable
data class MinimalPlayer(val id: String, val firstName: String, val lastName: String, val team: String, val position: String, val status: String)
{
}