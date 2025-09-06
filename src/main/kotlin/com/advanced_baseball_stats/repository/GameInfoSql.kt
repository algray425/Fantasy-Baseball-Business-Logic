package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.game.*
import com.advanced_baseball_stats.repository.tables.GameTable
import org.ktorm.dsl.*

object GameInfoSql
{
    fun getGames(team: String, startDate: String, endDate: String): MutableList<Game>
    {
        val dateRanges: ClosedRange<String> = startDate..endDate

        val games: MutableList<Game> = mutableListOf()

        DatabaseConnection.database.from(GameTable)
            .select(
                GameTable.gid, GameTable.temp, GameTable.windspeed, GameTable.winddir, GameTable.fieldcond, GameTable.precip,
                GameTable.daynight, GameTable.hometeam, GameTable.visteam)
            .where { (GameTable.hometeam eq team ) and (GameTable.date between dateRanges)}
            .forEach { game ->
                val gid             = game[GameTable.gid        ]
                val temp            = game[GameTable.temp       ]
                val windSpeed       = game[GameTable.windspeed  ]
                val windDir         = game[GameTable.winddir    ]
                val fieldCond       = game[GameTable.fieldcond  ]
                val precipitation   = game[GameTable.precip     ]
                val time            = game[GameTable.daynight   ]
                val homeTeam        = game[GameTable.hometeam   ]
                val awayTeam        = game[GameTable.visteam    ]

                if (gid != null && temp != null && windSpeed != null && windDir != null && fieldCond != null && precipitation != null && time != null
                    && homeTeam != null && awayTeam != null)
                {
                    val curGame = Game(gid, temp, windSpeed, WindDirection.fromString(windDir.uppercase()), Condition.valueOf(fieldCond.uppercase()),
                        Precipitation.valueOf(precipitation.uppercase()), TimeOfDay.valueOf(time.uppercase()), Team.valueOf(homeTeam.uppercase()),
                        Team.valueOf(awayTeam.uppercase()))

                    games.add(curGame)
                }
            }

        return games
    }
}