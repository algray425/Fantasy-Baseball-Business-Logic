package com.advanced_baseball_stats.v2.repository.users

import com.advanced_baseball_stats.v2.model.users.UserInfo
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

    fun checkIfEmailAlreadyExists(email: String): String?
    {
        return UserDatabaseConnection.database.from(UserTable)
            .select(UserTable.email)
            .where { UserTable.email eq email }
            .map { row ->
                row[UserTable.email] ?: ""
            }.firstOrNull()
    }

    fun addUser(userId: String, email: String, hashedPassword: String, salt: String)
    {
        UserDatabaseConnection.database.insert(UserTable)
        {
            set(UserTable.id, userId)
            set(UserTable.email, email)
            set(UserTable.password, hashedPassword)
        }
    }

    fun getUserByEmail(email: String): UserInfo?
    {
        return UserDatabaseConnection.database.from(UserTable)
            .select(UserTable.id, UserTable.userName, UserTable.password)
            .where { UserTable.email eq email }
            .map { userRow ->
                UserInfo(
                    userRow[UserTable.id] ?:"",
                    userRow[UserTable.userName] ?: "",
                    userRow[UserTable.password] ?: ""
                )
            }.firstOrNull()
    }
}