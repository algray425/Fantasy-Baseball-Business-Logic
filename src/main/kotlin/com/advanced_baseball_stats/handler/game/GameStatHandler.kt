package com.advanced_baseball_stats.handler.game

import com.advanced_baseball_stats.model.game.Game
import com.advanced_baseball_stats.repository.GameInfoSql
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object GameStatHandler
{
    fun getGames(team: String, startDate:String): MutableList<Game>
    {
        val curDate = getCurrentDate()

        return GameInfoSql.getGames(team, startDate, curDate)
    }

    private fun getCurrentDate(): String
    {
        val curDate             = LocalDate.now()
        val dateTimeFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dateTimeFormatter.format(curDate)
    }
}