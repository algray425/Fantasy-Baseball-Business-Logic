package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.repository.helper.database.DatabaseConstants
import org.ktorm.database.Database

object DatabaseConnection
{
    val database = Database.connect(DatabaseConstants.url, user = DatabaseConstants.user, password = DatabaseConstants.password)
}