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
import com.advanced_baseball_stats.v2.exception.InvalidPlayerIdException
import com.advanced_baseball_stats.v2.exception.InvalidTeamStatsHittingRequest
import com.advanced_baseball_stats.v2.exception.InvalidTeamStatsPitchingRequest
import com.advanced_baseball_stats.v2.exception.InvalidUserException
import com.advanced_baseball_stats.v2.handler.FantasyTeamsHandler
import com.advanced_baseball_stats.v2.handler.FavoritePlayersHandler
import com.advanced_baseball_stats.v2.handler.PlayerStatsHandler
import com.advanced_baseball_stats.v2.handler.TeamStatsHandler
import com.advanced_baseball_stats.v2.model.batters.FavoritePlayers.FavoritePlayerInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
        battingStatHandler      : BattingStatHandler
    ,   pitchingStatHandler     : PitchingStatHandler
    ,   gradeHandler            : GradeHandler
    ,   playerStatsHandler      : PlayerStatsHandler
    ,   favoritePlayersHandler  : FavoritePlayersHandler
    ,   fantasyTeamsHandler     : FantasyTeamsHandler
    ,   teamStatsHandler        : TeamStatsHandler
){
    routing {
        get("/")
        {
            call.respond("Hello World!")
        }

        get("/api/v2/players/hitting/stats/{season}")
        {
            val season: Int = call.parameters["season"]?.toInt() ?: 0

            val queryParameters = call.queryParameters

            val sortBy              = queryParameters["sortBy"          ] ?: "PERCENTILE_OVERALL"
            val position            = queryParameters["position"        ] ?: ""
            val startDate           = queryParameters["startDate"       ] ?: ""
            val endDate             = queryParameters["endDate"         ] ?: ""
            val leagueTypeFilter    = queryParameters["leagueTypeFilter"] ?: ""
            val leagueIdFilter      = queryParameters["leagueIdFilter"  ] ?: ""
            val limit               = queryParameters["limit"           ]?.toInt()  ?: 10
            val page                = queryParameters["page"            ]?.toInt()  ?: 0

            call.respond(playerStatsHandler.getRankedHittersBySeason(season, sortBy, position, startDate, endDate, leagueTypeFilter, leagueIdFilter, limit, page))
        }

        get("/api/v2/players/startingPitchers/stats/{season}")
        {
            val season: Int = call.parameters["season"]?.toInt() ?: 0

            val queryParameters = call.queryParameters

            val sortBy              = queryParameters["sortBy"          ] ?: "PERCENTILE_OVERALL"
            val startDate           = queryParameters["startDate"       ] ?: ""
            val endDate             = queryParameters["endDate"         ] ?: ""
            val leagueTypeFilter    = queryParameters["leagueTypeFilter"] ?: ""
            val leagueIdFilter      = queryParameters["leagueIdFilter"  ] ?: ""
            val limit               = queryParameters["limit"           ]?.toInt()  ?: 10
            val page                = queryParameters["page"            ]?.toInt()  ?: 0

            call.respond(playerStatsHandler.getRankedStartingPitchersBySeason(season, sortBy, startDate, endDate, leagueTypeFilter, leagueIdFilter, limit, page))
        }

        get("/api/v2/players/reliefPitchers/stats/{season}")
        {
            val season: Int = call.parameters["season"]?.toInt() ?: 0

            val queryParameters = call.queryParameters

            val sortBy              = queryParameters["sortBy"          ] ?: "PERCENTILE_OVERALL"
            val startDate           = queryParameters["startDate"       ] ?: ""
            val endDate             = queryParameters["endDate"         ] ?: ""
            val leagueTypeFilter    = queryParameters["leagueTypeFilter"] ?: ""
            val leagueIdFilter      = queryParameters["leagueIdFilter"  ] ?: ""
            val limit               = queryParameters["limit"           ]?.toInt()  ?: 10
            val page                = queryParameters["page"            ]?.toInt()  ?: 0

            call.respond(playerStatsHandler.getRankedReliefPitchersBySeason(season, sortBy, startDate, endDate, leagueTypeFilter, leagueIdFilter, limit, page))
        }

        get("/api/v2/players/hitting/projections")
        {
            val queryParameters = call.queryParameters

            val sortBy              = queryParameters["sortBy"      ]              ?: "PERCENTILE_OVERALL"
            val qualified           = queryParameters["qualified"   ]?.toBoolean() ?: false
            val position            = queryParameters["position"    ]              ?: ""
            val leagueTypeFilter    = queryParameters["leagueType"  ]              ?: ""
            val leagueIdFilter      = queryParameters["leagueId"    ]              ?: ""
            val limit               = queryParameters["limit"       ]?.toInt()     ?: 10
            val page                = queryParameters["page"        ]?.toInt()     ?: 0

            call.respond(playerStatsHandler.getBatterProjections(sortBy, qualified, position, leagueTypeFilter, leagueIdFilter, limit, page))
        }

        get("/api/v2/players/startingPitchers/projections")
        {
            val queryParameters = call.queryParameters

            val sortBy              = queryParameters["sortBy"      ]              ?: "PERCENTILE_OVERALL"
            val leagueTypeFilter    = queryParameters["leagueType"  ]              ?: ""
            val leagueIdFilter      = queryParameters["leagueId"    ]              ?: ""
            val limit               = queryParameters["limit"       ]?.toInt()     ?: 10
            val page                = queryParameters["page"        ]?.toInt()     ?: 0

            call.respond(playerStatsHandler.getStartingPitcherProjections(sortBy, leagueTypeFilter, leagueIdFilter, limit, page))
        }

        get("/api/v2/players/hitting/summary/{playerId}")
        {
            val playerId: String = call.parameters["playerId"].toString()

            val playerSummary = playerStatsHandler.getHitterSummary(playerId)

            if (playerSummary == null)
            {
                call.respond(HttpStatusCode.BadRequest, "invalid playerId!")
            }
            else
            {
                call.respond(playerSummary)
            }
        }

        get("/api/v2/players/pitching/summary/{playerId}")
        {
            val playerId: String = call.parameters["playerId"].toString()

            val playerSummary = playerStatsHandler.getPitcherSummary(playerId)

            if (playerSummary == null)
            {
                call.respond(HttpStatusCode.BadRequest, "invalid playerId!")
            }
            else
            {
                call.respond(playerSummary)
            }
        }

        get("/api/v2/players/hitting/stats/seasonSummaries/{playerId}")
        {
            val playerId: String = call.parameters["playerId"].toString()

            val queryParameters = call.queryParameters

            val startSeason = queryParameters["startSeason"] ?: "2025"

            call.respond(playerStatsHandler.getHitterSeasonSummaries(playerId, startSeason))
        }

        get("/api/v2/players/pitching/stats/seasonSummaries/{playerId}")
        {
            val playerId: String = call.parameters["playerId"].toString()

            val queryParameters = call.queryParameters

            val startSeason = queryParameters["startSeason"] ?: "2025"

            call.respond(playerStatsHandler.getPitcherSeasonSummaries(playerId, startSeason))
        }

        get("/api/v2/players/hitting/stats/perGame/{playerId}/{season}/{stat}")
        {
            val playerId: String    = call.parameters["playerId"].toString()
            val season  : Int       = call.parameters["season"  ]?.toInt() ?: -1
            val stat    : String    = call.parameters["stat"    ].toString()

            call.respond(playerStatsHandler.getHittingStatPerGame(playerId, season, stat))
        }

        get("/api/v2/players/pitching/stats/perGame/{playerId}/{season}/{stat}")
        {
            val playerId: String    = call.parameters["playerId"].toString()
            val season  : Int       = call.parameters["season"  ]?.toInt() ?: -1
            val stat    : String    = call.parameters["stat"    ].toString()

            call.respond(playerStatsHandler.getPitchingStatPerGame(playerId, season, stat))
        }

        get("/api/v2/teams/hitting/stats/{teamId}/{season}")
        {
            val teamId: String  = call.parameters["teamId"].toString()
            val season: Int     = call.parameters["season"]?.toInt() ?: 2026

            try
            {
                call.respond(teamStatsHandler.getTeamHittingStatsPerSeason(teamId, season))
            }
            catch (ex: InvalidTeamStatsHittingRequest)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/api/v2/teams/pitching/stats/{teamId}/{season}")
        {
            val teamId: String  = call.parameters["teamId"].toString()
            val season: Int     = call.parameters["season"]?.toInt() ?: 2026

            try
            {
                call.respond(teamStatsHandler.getTeamPitchingStatsPerSeason(teamId, season))
            }
            catch (ex: InvalidTeamStatsPitchingRequest)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        get("/api/v2/users/getFavoritePlayers/{userId}")
        {
            val userId: String = call.parameters["userId"].toString()

            call.respond(favoritePlayersHandler.getFavoritePlayers(userId))
        }

        post("/api/v2/users/addFavoritePlayer/")
        {
            val favoritePlayerInfo = call.receive<FavoritePlayerInfo>()

            try
            {
                favoritePlayersHandler.addFavoritePlayer(favoritePlayerInfo)

                call.respond(HttpStatusCode.Created)
            }
            catch (ex: InvalidUserException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
            catch (ex: InvalidPlayerIdException)
            {
                call.respond(HttpStatusCode.BadRequest, ex.message ?: "invalid input")
            }
        }

        delete("/api/v2/users/deleteFavoritePlayer/{userId}/{playerId}")
        {
            val userId  : String = call.parameters["userId"     ].toString()
            val playerId: String = call.parameters["playerId"   ].toString()

            favoritePlayersHandler.removeFavoritePlayer(userId, playerId)

            call.respond(HttpStatusCode.NoContent)
        }

        get("/api/v2/users/getFantasyTeams/{userId}")
        {
            val userId: String = call.parameters["userId"].toString()

            call.respond(fantasyTeamsHandler.getFantasyTeams(userId))
        }

        get("/api/v2/users/fantasyTeamSummary/{userId}/{leagueType}/{leagueId}/{teamId}")
        {
            val userId      : String = call.parameters["userId"     ].toString()
            val leagueType  : String = call.parameters["leagueType" ].toString()
            val leagueId    : String = call.parameters["leagueId"   ].toString()
            val teamId      : String = call.parameters["teamId"     ].toString()

            val queryParameters = call.queryParameters

            val weekNumber = queryParameters["weekNumber"]?.toInt() ?: 1

            call.respond(fantasyTeamsHandler.getFantasyTeamSummary(userId, leagueType, leagueId, teamId, weekNumber))
        }

        get("/schedule/{team}/{startDate}/{endDate}")
        {
            val team        = call.parameters["team"        ].toString()
            val startDate   = call.parameters["startDate"   ].toString()
            val endDate     = call.parameters["endDate"     ].toString()

            call.respond(ScheduleHandler.getUpcomingSchedule(team, startDate, endDate))
        }

        get("/stat/batting/{id}/{period}/{startDate}")
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

        get("/stat/pitching/{id}/{period}/{startDate}")
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

        get("/grades/batting/{id}/{period}/{startWeekNumber}/{season}")
        {
            val id                  = call.parameters["id"                  ].toString()
            val period              = call.parameters["period"              ].toString()
            val startWeekNumber     = call.parameters["startWeekNumber"     ].toString()
            val season              = call.parameters["season"              ].toString()

            val queryParameters = call.queryParameters

            val endWeekNumber   = queryParameters["endWeekNumber"       ] ?: ""
            val stat            = queryParameters["stat"                ] ?: ""

            val stats = stat.split(",")

            try
            {
                val playerGrades = gradeHandler.getGradesByPlayer(id, period, startWeekNumber, endWeekNumber, season, stats)

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
                val playerGrades = gradeHandler.getBattingGradesByPercentile(stat, period, percentileStart, weekNumber, season, showAvailable)

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
                val playerGrades = gradeHandler.getPitchingGradesByPercentile(stat, period, percentileStart, weekNumber, season, showAvailable)

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
