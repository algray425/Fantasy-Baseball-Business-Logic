package com.advanced_baseball_stats.v2.model.users

import kotlinx.serialization.Serializable

@Serializable
class UserIdentifiers(
    val email: String,
    val password: String
){
}