package com.advanced_baseball_stats

import com.advanced_baseball_stats.exception.UnknownBattingStatException
import com.advanced_baseball_stats.exception.UnknownPeriodException
import com.advanced_baseball_stats.handler.batting.BattingStatHandler
import com.advanced_baseball_stats.handler.game.GameStatHandler
import com.advanced_baseball_stats.handler.grade.GradeHandler
import com.advanced_baseball_stats.handler.math.MathStatHandler
import com.advanced_baseball_stats.handler.pitching.PitchingStatHandler
import com.advanced_baseball_stats.handler.player.PlayerStatHandler
import com.advanced_baseball_stats.handler.schedule.ScheduleHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
        battingStatHandler  : BattingStatHandler
    ,   pitchingStatHandler : PitchingStatHandler
){
    routing {
        get("/")
        {
            call.respond("Hello World!")
        }

        get("/schedule/{team}/{startDate}/{endDate}")
        {
            val team        = call.parameters["team"        ].toString()
            val startDate   = call.parameters["startDate"   ].toString()
            val endDate     = call.parameters["endDate"     ].toString()

            call.respond(ScheduleHandler.getUpcomingSchedule(team, startDate, endDate))
        }

        get("/v2/stat/batting/{id}/{period}/{startDate}")
        {
            val id          = call.parameters["id"          ].toString()
            val period      = call.parameters["period"      ].toString()
            val startDate   = call.parameters["startDate"   ].toString()

            val queryParameters = call.queryParameters

            val endDate             = queryParameters["endDate"             ] ?: ""
            val pitcherId           = queryParameters["pitcherId"           ] ?: ""
            val pitcherHandedness   = queryParameters["pitcherHandedness"   ] ?: ""
            val stats               = queryParameters["stats"               ] ?: ""

            val statList = stats.split(",")

            try
            {
                val playerStats = battingStatHandler.getBattingStats(id, pitcherId, pitcherHandedness, period, startDate, endDate, statList)

                call.respond(playerStats)
            }
            catch (ex: UnknownBattingStatException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
            catch (ex: UnknownPeriodException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/v2/stat/pitching/{id}/{period}/{startDate}")
        {
            val id          = call.parameters["id"          ].toString()
            val period      = call.parameters["period"      ].toString()
            val startDate   = call.parameters["startDate"   ].toString()

            val queryParameters = call.queryParameters

            val endDate             = queryParameters["endDate"             ] ?: ""
            val pitcherId           = queryParameters["batterId"            ] ?: ""
            val pitcherHandedness   = queryParameters["batterSide"          ] ?: ""
            val stats               = queryParameters["stats"               ] ?: ""


            val statList = stats.split(",")

            try
            {
                val playerStats = pitchingStatHandler.getPitchingStats(id, pitcherId, pitcherHandedness, period, startDate, endDate, statList)

                call.respond(playerStats)
            }
            catch (ex: UnknownPeriodException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/players/{name}")
        {
            val name: String = call.parameters["name"].toString()

            val uri = call.request.uri

            println(uri)

            call.respond(PlayerStatHandler.getPlayersByName(name, uri))
        }

        get("/player/{id}")
        {
            val id: String = call.parameters["id"].toString()

            val uri = call.request.uri

            println(uri)

            call.respond(PlayerStatHandler.getPlayerFromId(id, uri));
        }

        get("/games/{team}/{startDate}")
        {
            val team        = call.parameters["team"        ].toString()
            val startDate   = call.parameters["startDate"   ].toString()

            call.respond(GameStatHandler.getGames(team, startDate))
        }

        get("/grades/batting/{id}/{period}/{startWeekNumber}")
        {
            val id                  = call.parameters["id"                  ].toString()
            val period              = call.parameters["period"              ].toString()
            val startWeekNumber     = call.parameters["startWeekNumber"     ].toString()

            val queryParameters = call.queryParameters

            val endWeekNumber   = queryParameters["endWeekNumber"       ] ?: ""
            val stat            = queryParameters["stat"                ] ?: ""

            val stats = stat.split(",")

            try
            {
                val playerGrades = GradeHandler.getGradesByPlayer(id, period, startWeekNumber, endWeekNumber, stats)

                call.respond(playerGrades)
            }
            catch (ex: UnknownPeriodException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
            catch (ex: UnknownBattingStatException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/grades/batting/{stat}/{period}/{percentileStart}/{weekNumber}/{season}")
        {
            val stat            = call.parameters["stat"                ].toString()
            val period          = call.parameters["period"              ].toString()
            val percentileStart = call.parameters["percentileStart"     ].toString()
            val weekNumber      = call.parameters["weekNumber"          ].toString()
            val season          = call.parameters["season"              ].toString()

            val queryParameters = call.queryParameters

            val showAvailable = queryParameters["showAvailable"] ?: "false"

            try
            {
                val playerGrades = GradeHandler.getBattingGradesByPercentile(stat, period, percentileStart, weekNumber, season, showAvailable)

                call.respond(playerGrades)
            }
            catch (ex: UnknownPeriodException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
            catch (ex: UnknownBattingStatException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/grades/pitching/{stat}/{period}/{percentileStart}/{weekNumber}/{season}")
        {
            val stat            = call.parameters["stat"                ].toString()
            val period          = call.parameters["period"              ].toString()
            val percentileStart = call.parameters["percentileStart"     ].toString()
            val weekNumber      = call.parameters["weekNumber"          ].toString()
            val season          = call.parameters["season"              ].toString()

            val queryParameters = call.queryParameters

            val showAvailable = queryParameters["showAvailable"] ?: "false"

            try
            {
                val playerGrades = GradeHandler.getPitchingGradesByPercentile(stat, period, percentileStart, weekNumber, season, showAvailable)

                call.respond(playerGrades)
            }
            catch (ex: UnknownPeriodException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/stat/batting/linearRegression/{id}/{xStat}/{yStat}/{startDate}")
        {
            val id          = call.parameters["id"          ].toString()
            val xStat       = call.parameters["xStat"       ].toString()
            val yStat       = call.parameters["yStat"       ].toString()
            val startDate   = call.parameters["startDate"   ].toString()

            try
            {
                val linearRegression = MathStatHandler.getLinearRegression(id, xStat, yStat, startDate)

                call.respond(linearRegression)
            }
            catch (ex: UnknownBattingStatException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/stat/batting/mean/{id}/{xStat}/{yStat}/{startDate}")
        {
            val id          = call.parameters["id"          ].toString()
            val xStat       = call.parameters["xStat"       ].toString()
            val yStat       = call.parameters["yStat"       ].toString()
            val startDate   = call.parameters["startDate"   ].toString()

            try
            {
                val meanValues = MathStatHandler.getMeanValues(id, xStat, yStat, startDate)

                call.respond(meanValues)
            }
            catch (ex: UnknownBattingStatException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }
    }
}
