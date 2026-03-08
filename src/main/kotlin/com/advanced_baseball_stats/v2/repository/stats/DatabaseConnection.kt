package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.repository.stats.helper.DatabaseConstants
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel

object DatabaseConnection
{
    val database = Database.connect(DatabaseConstants.url, user = DatabaseConstants.user, password = DatabaseConstants.password, logger = ConsoleLogger(threshold = LogLevel.DEBUG))
}