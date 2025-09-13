package com.advanced_baseball_stats

import com.advanced_baseball_stats.handler.batting.AggregateBattingStatHandler
import com.advanced_baseball_stats.handler.batting.BattingStatHandler
import com.advanced_baseball_stats.handler.batting.PerGameBattingStatHandler
import com.advanced_baseball_stats.handler.batting.TotalBattingStatHandler
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module()
{
    val perGameBattingStatHandler   = PerGameBattingStatHandler     ()
    val aggregateBattingStatHandler = AggregateBattingStatHandler   ()
    val totalBattingStatHandler     = TotalBattingStatHandler       ()

    val battingStatHandler = BattingStatHandler(perGameBattingStatHandler, aggregateBattingStatHandler, totalBattingStatHandler)

    configureSerialization  ()
    configureCors           ()
    configureRouting        (battingStatHandler)
}
