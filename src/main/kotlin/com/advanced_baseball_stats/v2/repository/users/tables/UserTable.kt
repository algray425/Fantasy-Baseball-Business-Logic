package com.advanced_baseball_stats.v2.repository.users.tables

import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object UserTable: Table<Nothing>("Users")
{
    val id = varchar("id")
    val userName = varchar("userName")
}