package com.advanced_baseball_stats.model.player

import kotlinx.serialization.Serializable

@Serializable
data class SearchablePlayer(var id: String, var firstName: String, var lastName: String, var nickName: String, var imageUrl: String = "") {
}