package com.advanced_baseball_stats.model.player

import kotlinx.serialization.Serializable

//TODO: can probably find team/position from the most recently played game by the player
@Serializable
data class Player(var id: String, var name: String, var team: String = "", var position: String = "", var number: String = "", var age: String = "",
                var height: String, var weight: Int, var imageUrl: String = "")
{
}