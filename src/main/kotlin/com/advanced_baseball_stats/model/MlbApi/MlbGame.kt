package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MlbGame(
    @SerialName("gamePk")
    val gamePk: Int,
    @SerialName("gameGuid")
    val gameGuid: String,
    @SerialName("link")
    val link: String,
    @SerialName("gameType")
    val gameType: String,
    @SerialName("season")
    val season: String,
    @SerialName("gameDate")
    val gameDate: String,
    @SerialName("officialDate")
    val officialDate: String,
    @SerialName("status")
    val status: Status,
    @SerialName("teams")
    val teams: Teams,
    @SerialName("venue")
    val venue: Venue,
    @SerialName("content")
    val content: Content,
    @SerialName("gameNumber")
    val gameNumber: Int,
    @SerialName("publicFacing")
    val publicFacing: Boolean,
    @SerialName("doubleHeader")
    val doubleHeader: String,
    @SerialName("gamedayType")
    val gamedayType: String,
    @SerialName("tieBreaker")
    val tieBreaker: String? = null,
    @SerialName("calendarEventID")
    val calendarEventID: String,
    @SerialName("seasonDisplay")
    val seasonDisplay: String,
    @SerialName("dayNight")
    val dayNight: String,
    @SerialName("scheduledInnings")
    val scheduledInnings: Int,
    @SerialName("reverseHomeAwayStatus")
    val reverseHomeAwayStatus: Boolean,
    @SerialName("inningBreakLength")
    val inningBreakLength: Int,
    @SerialName("gamesInSeries")
    val gamesInSeries: Int,
    @SerialName("seriesGameNumber")
    val seriesGameNumber: Int,
    @SerialName("seriesDescription")
    val seriesDescription: String,
    @SerialName("recordSource")
    val recordSource: String,
    @SerialName("ifNecessary")
    val ifNecessary: String,
    @SerialName("ifNecessaryDescription")
    val ifNecessaryDescription: String
)
{
}