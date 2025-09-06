package com.advanced_baseball_stats.model.player

import kotlinx.serialization.Serializable

@Serializable
data class MinimalPlayer (var id: String, var name: String, var imageUrl: String)
{
}