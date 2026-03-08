package com.advanced_baseball_stats

import com.advanced_baseball_stats.handler.batting.AggregateBattingStatHandler
import com.advanced_baseball_stats.handler.batting.BattingStatHandler
import com.advanced_baseball_stats.handler.batting.PerGameBattingStatHandler
import com.advanced_baseball_stats.handler.batting.TotalBattingStatHandler
import com.advanced_baseball_stats.handler.grade.AggregatePercentileGradesHandler
import com.advanced_baseball_stats.handler.grade.AggregatePlayerGradesHandler
import com.advanced_baseball_stats.handler.grade.GradeHandler
import com.advanced_baseball_stats.handler.grade.PerGamePercentileGradesHandler
import com.advanced_baseball_stats.handler.pitching.AggregatePitchingStatHandler
import com.advanced_baseball_stats.handler.pitching.PerGamePitchingStatHandler
import com.advanced_baseball_stats.handler.pitching.PitchingStatHandler
import com.advanced_baseball_stats.v2.handler.FantasyTeamsHandler
import com.advanced_baseball_stats.v2.handler.FavoritePlayersHandler
import com.advanced_baseball_stats.v2.handler.PlayerStatsHandler
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

    val perGamePitchingStatHandler      = PerGamePitchingStatHandler    ()
    val aggregatePitchingStatHandler    = AggregatePitchingStatHandler  ()

    val pitchingStatHandler = PitchingStatHandler(perGamePitchingStatHandler, aggregatePitchingStatHandler)

    val perGamePercentileGradesHandler      = PerGamePercentileGradesHandler    ()
    val aggregatePercentileGradesHandler    = AggregatePercentileGradesHandler  ()
    val aggregatePlayerGradesHandler        = AggregatePlayerGradesHandler      ()

    val gradeHandler = GradeHandler(perGamePercentileGradesHandler, aggregatePercentileGradesHandler, aggregatePlayerGradesHandler)

    //V2
    val playerStatsHandler      = PlayerStatsHandler()
    val favoritePlayersHandler  = FavoritePlayersHandler()
    val fantasyTeamsHandler     = FantasyTeamsHandler()

    configureSerialization  ()
    configureCors           ()
    configureRouting        (battingStatHandler, pitchingStatHandler, gradeHandler, playerStatsHandler, favoritePlayersHandler, fantasyTeamsHandler)
}
