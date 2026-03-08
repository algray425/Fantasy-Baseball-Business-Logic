package com.advanced_baseball_stats.v2.helper

object MlbIdToTeamAbbreviationConverter
{
    private val mlbIdToTeamAbbreviation: Map<Int, String> = mapOf(
        136 to "SEA",
        111 to "BOS",
        158 to "MIL",
        141 to "TOR",
        112 to "CHN",
        113 to "CIN",
        115 to "COL",
        140 to "TEX",
        109 to "ARI",
        144 to "ATL",
        117 to "HOU",
        142 to "MIN",
        133 to "OAK",
        118 to "KCA",
        110 to "BAL",
        147 to "NYA",
        120 to "WAS",
        108 to "ANA",
        145 to "CHA",
        146 to "MIA",
        139 to "TBA",
        143 to "PHI",
        116 to "DET",
        121 to "NYN",
        119 to "LAN",
        134 to "PIT",
        137 to "SFN",
        138 to "SLN",
        135 to "SDN",
        114 to "CLE"
    )

    fun convertMlbIdToTeamAbbreviation(mlbId: Int): String
    {
        val teamAbbreviation = mlbIdToTeamAbbreviation[mlbId] ?: ""

        return teamAbbreviation
    }
}