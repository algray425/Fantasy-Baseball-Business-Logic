package com.advanced_baseball_stats.v2.model.users

import kotlinx.serialization.Serializable

@Serializable
class AuthorizedUserInfo(
    val userId: String,
    val userName: String
){
}