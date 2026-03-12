package com.advanced_baseball_stats.v2.repository.users

import com.advanced_baseball_stats.v2.repository.users.tables.UserTable
import org.ktorm.dsl.*

object UserSql
{
    fun isValidUser(userId: String): Boolean
    {
        UserDatabaseConnection.database.from(UserTable)
            .select(UserTable.id)
            .where{UserTable.id eq userId}
            .forEach { user ->
                return true
            }
        return false
    }
}