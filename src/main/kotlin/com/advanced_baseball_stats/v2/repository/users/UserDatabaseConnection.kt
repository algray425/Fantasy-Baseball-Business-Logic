package com.advanced_baseball_stats.v2.repository.users

import com.advanced_baseball_stats.v2.repository.users.helper.UserDatabaseConstants

import org.ktorm.database.Database

object UserDatabaseConnection
{
    val database = Database.connect(UserDatabaseConstants.url, user = UserDatabaseConstants.user, password = UserDatabaseConstants.password)
}