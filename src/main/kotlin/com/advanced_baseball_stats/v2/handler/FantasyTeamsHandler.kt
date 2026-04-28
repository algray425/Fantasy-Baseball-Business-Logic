package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.data.source.MlbApiSource
import com.advanced_baseball_stats.model.MlbApi.Schedule
import com.advanced_baseball_stats.v2.data.EspnDataSource
import com.advanced_baseball_stats.v2.data.FanTraxDataSource
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyPlayerSummary
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyTeam
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyTeamSummary
import com.advanced_baseball_stats.v2.model.espn.matchup.EspnSchedule
import com.advanced_baseball_stats.v2.model.fantasy.HolisiticFantasyLeague
import com.advanced_baseball_stats.v2.model.fantasy.LineupOptimizedHitter
import com.advanced_baseball_stats.v2.repository.stats.PlayerBattingSql
import com.advanced_baseball_stats.v2.repository.stats.PlayerPitchingSql
import com.advanced_baseball_stats.v2.repository.stats.TeamPitchingSql
import com.advanced_baseball_stats.v2.repository.users.FantasyTeamsSql
import com.advanced_baseball_stats.v2.transformer.EspnToHolisticFantasyScheduleTransformer
import com.advanced_baseball_stats.v2.transformer.MlbTeamIdToAbbreviationTransformer
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*
import kotlin.time.measureTimedValue

class FantasyTeamsHandler
{
    private val mlbApiSource                            : MlbApiSource                              = MlbApiSource()
    private val espnDataSource                          : EspnDataSource                            = EspnDataSource()
    private val fanTraxDataSource                       : FanTraxDataSource                         = FanTraxDataSource()
    private val espnToHolisticFantasyScheduleTransformer: EspnToHolisticFantasyScheduleTransformer  = EspnToHolisticFantasyScheduleTransformer()

    fun getFantasyTeams(userId: String): MutableList<FantasyTeam>
    {
        return FantasyTeamsSql.getFantasyTeamsForUser(userId)
    }

    fun getFantasyTeamSummary(userId: String, leagueType: String, leagueId: String, teamId: String, weekNumber: Int): FantasyTeamSummary
    {
        var fantasyRosters: HolisiticFantasyLeague? = null

        if (leagueType.equals("ESPN"))
        {
            runBlocking {
                fantasyRosters = espnDataSource.getFantasyTeamRosters(leagueId)
            }
        }
        else if (leagueType.equals("FANTRAX"))
        {
            runBlocking {
                fantasyRosters = fanTraxDataSource.getFantasyTeamRosters(leagueId)
            }
        }

        val fantasySchedule: EspnSchedule?

        runBlocking {
            fantasySchedule = espnDataSource.getFantasyTeamMatchup(leagueId)
        }

        if (fantasySchedule != null && fantasyRosters != null)
        {
            val weekNumbers: Set<Int> = setOf(weekNumber, weekNumber + 1)

            val fantasyTeamMatchups = espnToHolisticFantasyScheduleTransformer.transform(fantasySchedule, teamId.toInt(), weekNumbers)

            val curMatchup      = fantasyTeamMatchups[0]
            val upcomingMatchup = fantasyTeamMatchups[1]

            val opposingTeamUpcomingMatchup = if (upcomingMatchup.awayTeamSummary.teamId == teamId.toInt()) upcomingMatchup.homeTeamSummary else upcomingMatchup.awayTeamSummary

            val opposingTeamUpcomingMatchupPlayers = fantasyRosters!!.rosters.find { it.teamId == opposingTeamUpcomingMatchup.teamId.toString() }

            val opposingTeamPlayerIds = mutableListOf<String>()

            if (opposingTeamUpcomingMatchupPlayers != null)
            {
                for (player in opposingTeamUpcomingMatchupPlayers.playerIds)
                {
                    opposingTeamPlayerIds.add(player)
                }
            }

            val opposingPlayerSummaries = PlayerBattingSql.getEspnFantasyPlayerSummaries(opposingTeamPlayerIds, 2026)

            val userTeam = fantasyRosters!!.rosters.find { it.teamId == teamId }

            var playerSummaries: MutableList<FantasyPlayerSummary> = mutableListOf()

            if (userTeam != null)
            {
                val playerIds: MutableList<String> = mutableListOf()

                for (player in userTeam.playerIds)
                {
                    playerIds.add(player)
                }

                val(playerSummariesResponse, playerSummariesDuration) = measureTimedValue {
                    PlayerBattingSql.getEspnFantasyPlayerSummaries(playerIds, 2026)
                }

                playerSummaries = playerSummariesResponse

                println("Player summaries duration: $playerSummariesDuration")
            }

            val fullLeagueIds: MutableList<String> = mutableListOf()

            for (fantasyTeam in fantasyRosters!!.rosters)
            {
                for (player in fantasyTeam.playerIds)
                {
                    fullLeagueIds.add(player)
                }
            }

            //get best available hitters
            val (bestAvailableHitters, hitterDuration) = measureTimedValue {
                PlayerBattingSql.getBestAvailableBattersOverallFromEspn(fullLeagueIds, 2026)
            }

            println("Hitter rank time duration: $hitterDuration")


            val (bestAvailableStartingPitchers, startingPitcherDuration) = measureTimedValue {
                PlayerPitchingSql.getBestAvailableStartingPitchersFromEspn(fullLeagueIds, 2026)
            }

            println("Starting Pitcher rank time duration: $startingPitcherDuration")

            //get best available relief pitchers
            val (bestAvailableReliefPitchers, reliefPitcherDuration) = measureTimedValue {
                PlayerPitchingSql.getBestAvailableReliefPitchersFromEspn(fullLeagueIds, 2026)
            }

            println("Relief Pitcher rank time duration: $reliefPitcherDuration")

            return FantasyTeamSummary(playerSummaries, curMatchup, opposingPlayerSummaries, bestAvailableHitters, bestAvailableStartingPitchers, bestAvailableReliefPitchers)
        }

        return FantasyTeamSummary(mutableListOf(), null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    }

    fun getOptimizedLineup(userId: String, leagueType: String, leagueId: String, teamId: String): Map<String, MutableList<LineupOptimizedHitter>>
    {
        var fantasyRosters: HolisiticFantasyLeague? = null

        if (leagueType.equals("ESPN"))
        {
            runBlocking {
                fantasyRosters = espnDataSource.getFantasyTeamRosters(leagueId)
            }
        }
        else if (leagueType.equals("FANTRAX"))
        {
            runBlocking {
                fantasyRosters = fanTraxDataSource.getFantasyTeamRosters(leagueId)
            }
        }

        val currentDate = LocalDate.now()
        val prevWeekDate = currentDate.minusWeeks(1)

        val schedule: Schedule?

        runBlocking {
            schedule = mlbApiSource.getSchedulePerDateRange(prevWeekDate.toString(), currentDate.toString())
        }

        if (fantasyRosters != null && schedule != null)
        {
            val teamHitterIds = mutableListOf<String>()

            val userTeam = fantasyRosters!!.rosters.find { it.teamId == teamId }

            for (player in userTeam!!.playerIds)
            {
                teamHitterIds.add(player)
            }

            val fantasyHitters = PlayerBattingSql.getLineupOptimizedHitters(teamHitterIds, 2026, prevWeekDate.toString(), currentDate.toString())

            //create grades from last 7 days from list of hitters (by position or overall?)
            val playerToRecentPercentile = getHitterGradesPastWeek(fantasyHitters)

            //get list of teams from hitters
            val teams = mutableSetOf<String>()

            for (hitter in fantasyHitters)
            {
                teams.add(hitter.currentTeam)
            }

            val teamToOpp           = mutableMapOf<String, String>()
            val opposingPitchers    = mutableSetOf<String>()

            for (date in schedule.dates)
            {
                for (game in date.games)
                {
                    val awayTeamAbbr = MlbTeamIdToAbbreviationTransformer.transform(game.teams.awayTeam.team.id)
                    val homeTeamAbbr = MlbTeamIdToAbbreviationTransformer.transform(game.teams.homeTeam.team.id)

                    if (teams.contains(awayTeamAbbr) && game.teams.homeTeam.probablePitcher != null)
                    {
                        opposingPitchers.add(game.teams.homeTeam.probablePitcher.id.toString())
                        teamToOpp[awayTeamAbbr] = homeTeamAbbr
                    }
                    if (teams.contains(homeTeamAbbr)  && game.teams.awayTeam.probablePitcher != null)
                    {
                        opposingPitchers.add(game.teams.awayTeam.probablePitcher.id.toString())
                        teamToOpp[homeTeamAbbr] = awayTeamAbbr
                    }
                }
            }

            //get pitching matchup grades and probable pitcher grades from database
            val matchupGrades = TeamPitchingSql.getPitchingGradesByPitcher(opposingPitchers, 2026)

            //map of position to priority queues sorted by grade (overall grade + grade last 7 days + inverse pitcher grade + matchup grade / 4)
            val positionToHitterRankings = mutableMapOf<String, PriorityQueue<Pair<LineupOptimizedHitter, Double>>>()

            for (hitter in fantasyHitters)
            {
                val hitterPercentileTeam    = playerToRecentPercentile[hitter.playerId] ?: 0.0
                val hitterPercentileOverall = hitter.percentileOverall

                val hitterOpposingTeam  = teamToOpp[hitter.currentTeam] ?: ""
                val hitterMatchupGrades = matchupGrades[hitterOpposingTeam] ?: Pair(0.0, 0.0)

                val matchupGradeOverall = hitterMatchupGrades.first
                val matchupGradePitcher = hitterMatchupGrades.second

                val gradeAverage = (hitterPercentileTeam + hitterPercentileOverall + matchupGradeOverall + matchupGradePitcher) / 4.0

                val position = hitter.currentPosition

                if (position.equals("1B"))
                {
                    if (!positionToHitterRankings.containsKey("1B"))
                    {
                        positionToHitterRankings["1B"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }
                    if (!positionToHitterRankings.containsKey("1B/3B"))
                    {
                        positionToHitterRankings["1B/3B"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }

                    positionToHitterRankings["1B"   ]?.add(Pair(hitter, gradeAverage))
                    positionToHitterRankings["1B/3B"]?.add(Pair(hitter, gradeAverage))
                }
                else if (position.equals("C"))
                {
                    if (!positionToHitterRankings.containsKey("C"))
                    {
                        positionToHitterRankings["C"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }

                    positionToHitterRankings["C"]?.add(Pair(hitter, gradeAverage))
                }
                else if (position.equals("2B"))
                {
                    if (!positionToHitterRankings.containsKey("2B"))
                    {
                        positionToHitterRankings["2B"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }
                    if (!positionToHitterRankings.containsKey("2B/SS"))
                    {
                        positionToHitterRankings["2B/SS"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }

                    positionToHitterRankings["2B"   ]?.add(Pair(hitter, gradeAverage))
                    positionToHitterRankings["2B/SS"]?.add(Pair(hitter, gradeAverage))
                }
                else if (position.equals("3B"))
                {
                    if (!positionToHitterRankings.containsKey("3B"))
                    {
                        positionToHitterRankings["3B"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }
                    if (!positionToHitterRankings.containsKey("1B/3B"))
                    {
                        positionToHitterRankings["1B/3B"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }

                    positionToHitterRankings["3B"   ]?.add(Pair(hitter, gradeAverage))
                    positionToHitterRankings["1B/3B"]?.add(Pair(hitter, gradeAverage))
                }
                else if (position.equals("SS"))
                {
                    if (!positionToHitterRankings.containsKey("SS"))
                    {
                        positionToHitterRankings["SS"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }
                    if (!positionToHitterRankings.containsKey("2B/SS"))
                    {
                        positionToHitterRankings["2B/SS"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }

                    positionToHitterRankings["SS"   ]?.add(Pair(hitter, gradeAverage))
                    positionToHitterRankings["2B/SS"]?.add(Pair(hitter, gradeAverage))
                }
                else if (position.equals("LF") || position.equals("RF") || position.equals("CF"))
                {
                    if (!positionToHitterRankings.containsKey("OF"))
                    {
                        positionToHitterRankings["OF"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                    }

                    positionToHitterRankings["OF"]?.add(Pair(hitter, gradeAverage))
                }

                if (!positionToHitterRankings.containsKey("UTIL"))
                {
                    positionToHitterRankings["UTIL"] = PriorityQueue<Pair<LineupOptimizedHitter, Double>>(compareByDescending<Pair<LineupOptimizedHitter, Double>> { it.second })
                }

                positionToHitterRankings["UTIL" ]?.add(Pair(hitter, gradeAverage))
            }

            //take top n players from each position to determine optimal lineup

            val optimizedLineup = mutableMapOf<String, MutableList<LineupOptimizedHitter>>()

            optimizedLineup["C"     ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["1B"    ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["2B"    ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["3B"    ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["SS"    ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["1B/3B" ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["2B/SS" ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["OF"    ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["UTIL"  ] = mutableListOf<LineupOptimizedHitter>()
            optimizedLineup["BENCH" ] = mutableListOf<LineupOptimizedHitter>()

            val seenPlayers = mutableSetOf<String>()

            if (positionToHitterRankings.containsKey("C"))
            {
                while (positionToHitterRankings["C"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["C"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["C"]!!.poll()
                }

                if (positionToHitterRankings["C"]!!.isNotEmpty())
                {
                    val bestCatcher = positionToHitterRankings["C"]!!.poll()

                    seenPlayers.add(bestCatcher.first.playerId)

                    optimizedLineup["C"]!!.add(bestCatcher.first)
                }
            }
            if (positionToHitterRankings.containsKey("1B"))
            {
                while (positionToHitterRankings["1B"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["1B"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["1B"]!!.poll()
                }

                if (positionToHitterRankings["1B"]!!.isNotEmpty())
                {
                    val bestFirstBaseman = positionToHitterRankings["1B"]!!.poll()

                    seenPlayers.add(bestFirstBaseman.first.playerId)

                    optimizedLineup["1B"]!!.add(bestFirstBaseman.first)
                }
            }
            if (positionToHitterRankings.containsKey("2B"))
            {
                while (positionToHitterRankings["2B"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["1B"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["2B"]!!.poll()
                }

                if (positionToHitterRankings["2B"]!!.isNotEmpty())
                {
                    val bestSecondBaseman = positionToHitterRankings["2B"]!!.poll()

                    seenPlayers.add(bestSecondBaseman.first.playerId)

                    optimizedLineup["2B"]!!.add(bestSecondBaseman.first)
                }
            }
            if (positionToHitterRankings.containsKey("3B"))
            {
                while (positionToHitterRankings["3B"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["3B"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["3B"]!!.poll()
                }

                if (positionToHitterRankings["3B"]!!.isNotEmpty())
                {
                    val bestThirdBaseman = positionToHitterRankings["3B"]!!.poll()

                    seenPlayers.add(bestThirdBaseman.first.playerId)

                    optimizedLineup["3B"]!!.add(bestThirdBaseman.first)
                }
            }
            if (positionToHitterRankings.containsKey("SS"))
            {
                while (positionToHitterRankings["SS"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["3B"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["SS"]!!.poll()
                }

                if (positionToHitterRankings["SS"]!!.isNotEmpty())
                {
                    val bestShortStop = positionToHitterRankings["SS"]!!.poll()

                    seenPlayers.add(bestShortStop.first.playerId)

                    optimizedLineup["SS"]!!.add(bestShortStop.first)
                }
            }
            if (positionToHitterRankings.containsKey("1B/3B"))
            {
                while (positionToHitterRankings["1B/3B"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["1B/3B"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["1B/3B"]!!.poll()
                }

                if (positionToHitterRankings["1B/3B"]!!.isNotEmpty())
                {
                    val bestFirstOrThirdBaseman = positionToHitterRankings["1B/3B"]!!.poll()

                    seenPlayers.add(bestFirstOrThirdBaseman.first.playerId)

                    optimizedLineup["1B/3B"]!!.add(bestFirstOrThirdBaseman.first)
                }
            }
            if (positionToHitterRankings.containsKey("2B/SS"))
            {
                while (positionToHitterRankings["2B/SS"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["2B/SS"]!!.peek().first.playerId))
                {
                    positionToHitterRankings["2B/SS"]!!.poll()
                }

                if (positionToHitterRankings["2B/SS"]!!.isNotEmpty())
                {
                    val bestShortStopOrSecondBaseman = positionToHitterRankings["2B/SS"]!!.poll()

                    seenPlayers.add(bestShortStopOrSecondBaseman.first.playerId)

                    optimizedLineup["2B/SS"]!!.add(bestShortStopOrSecondBaseman.first)
                }
            }
            if (positionToHitterRankings.containsKey("OF"))
            {
                while (positionToHitterRankings["OF"]!!.isNotEmpty() && optimizedLineup["OF"]!!.size < 5)
                {
                    while (positionToHitterRankings["OF"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["OF"]!!.peek().first.playerId))
                    {
                        positionToHitterRankings["OF"]!!.poll()
                    }

                    if (positionToHitterRankings["OF"]!!.isNotEmpty())
                    {
                        val bestOutfielder = positionToHitterRankings["OF"]!!.poll()

                        seenPlayers.add(bestOutfielder.first.playerId)

                        optimizedLineup["OF"]!!.add(bestOutfielder.first)
                    }
                }
            }
            if (positionToHitterRankings.containsKey("UTIL"))
            {
                while (positionToHitterRankings["UTIL"]!!.isNotEmpty() && optimizedLineup["UTIL"]!!.size < 2)
                {
                    while (positionToHitterRankings["UTIL"]!!.isNotEmpty() && seenPlayers.contains(positionToHitterRankings["UTIL"]!!.peek().first.playerId))
                    {
                        positionToHitterRankings["UTIL"]!!.poll()
                    }

                    if (positionToHitterRankings["UTIL"]!!.isNotEmpty())
                    {
                        val bestOfTheRest = positionToHitterRankings["UTIL"]!!.poll()

                        seenPlayers.add(bestOfTheRest.first.playerId)

                        optimizedLineup["UTIL"]!!.add(bestOfTheRest.first)
                    }
                }
            }

            return optimizedLineup
        }

        return mapOf()
    }

    private fun getHitterGradesPastWeek(hitterStats: List<LineupOptimizedHitter>): Map<String, Double>
    {
        val runs        = mutableListOf<Int>    ()
        val homeRuns    = mutableListOf<Int>    ()
        val rbis        = mutableListOf<Int>    ()
        val stolenBases = mutableListOf<Int>    ()
        val obp         = mutableListOf<Double> ()

        for (hitter in hitterStats)
        {
            runs        .add(hitter.runs)
            homeRuns    .add(hitter.homeRuns)
            rbis        .add(hitter.rbis)
            stolenBases .add(hitter.stolenBases)
            obp         .add(hitter.onBasePercentage)
        }

        runs        .sort()
        homeRuns    .sort()
        rbis        .sort()
        stolenBases .sort()
        obp         .sort()

        val playerToGrade   = mutableMapOf<String, Double>()
        val grades          = mutableListOf<Double>()

        for (hitter in hitterStats)
        {
            val normalizedRuns        = normalizeIntStat(hitter.runs, runs[0], runs[runs.size - 1])
            val normalizedHomeRuns    = normalizeIntStat(hitter.homeRuns, homeRuns[0], homeRuns[homeRuns.size - 1])
            val normalizedRbis        = normalizeIntStat(hitter.rbis, rbis[0], rbis[homeRuns.size - 1])
            val normalizedStolenBases = normalizeIntStat(hitter.stolenBases, stolenBases[0], stolenBases[homeRuns.size - 1])
            val normalizedObp         = normalizeDoubleStat(hitter.onBasePercentage, obp[0], obp[homeRuns.size - 1])

            val grade = normalizedRuns + normalizedHomeRuns + normalizedRbis + normalizedStolenBases + normalizedObp

            grades.add(grade)
            playerToGrade[hitter.playerId] = grade
        }

        grades.sort()

        val playerToPercentile = mutableMapOf<String, Double>()

        for (hitter in hitterStats)
        {
            val grade = playerToGrade[hitter.playerId] ?: 0.0

            val percentile = getGradePercentile(grades, grade)

            playerToPercentile[hitter.playerId] = percentile
        }

        return playerToPercentile
    }

    private fun normalizeIntStat(stat: Int, minStat: Int, maxStat: Int): Double
    {
        if (maxStat - minStat != 0)
        {
            return (stat - minStat).toDouble() / (maxStat - minStat).toDouble()
        }

        return 0.0
    }

    private fun normalizeDoubleStat(stat: Double, minStat: Double, maxStat: Double): Double
    {
        if (maxStat - minStat != 0.0)
        {
            return (stat - minStat) / (maxStat - minStat)
        }

        return 0.0
    }

    private fun getGradePercentile(grades: List<Double>, grade: Double): Double
    {
        if (grades.isEmpty())
        {
            return 0.0
        }
        val countAtOrBelow = grades.count { it <= grade }

        return (countAtOrBelow.toDouble() / grades.size) * 100.0
    }
}