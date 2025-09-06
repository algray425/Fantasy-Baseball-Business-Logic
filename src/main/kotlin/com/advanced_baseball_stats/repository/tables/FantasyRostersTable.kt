package com.advanced_baseball_stats.repository.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object FantasyRostersTable : Table<Nothing>("fantasy_rosters")
{
    val id = int("id")
    val name = varchar("name")
    val catcher = varchar("catcher")
    val first_base = varchar("first_base")
    val second_base = varchar("second_base")
    val third_base = varchar("third_base")
    val short_stop = varchar("short_stop")
    val second_short_stop = varchar("second_short_stop")
    val first_third_base = varchar("first_third_base")
    val outfield_one = varchar("outfield_one")
    val outfield_two = varchar("outfield_two")
    val outfield_three = varchar("outfield_three")
    val outfield_four = varchar("outfield_four")
    val outfield_five = varchar("outfield_five")
    val util_one = varchar("util_one")
    val util_two = varchar("util_two")
    val bench_one = varchar("bench_one")
    val bench_two = varchar("bench_two")
    val bench_three = varchar("bench_three")
    val bench_four = varchar("bench_four")
    val bench_five = varchar("bench_five")
    val bench_six = varchar("bench_six")
    val il_one = varchar("il_one")
    val il_two = varchar("il_two")
    val il_three = varchar("il_three")
    val pitcher_one = varchar("pitcher_one")
    val pitcher_two = varchar("pitcher_two")
    val starting_pitcher_one = varchar("starting_pitcher_one")
    val starting_pitcher_two = varchar("starting_pitcher_two")
    val starting_pitcher_three = varchar("starting_pitcher_three")
    val starting_pitcher_four = varchar("starting_pitcher_four")
    val relief_pitcher_one = varchar("relief_pitcher_one")
    val relief_pitcher_two = varchar("relief_pitcher_two")
}