package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.player.Player
import com.advanced_baseball_stats.model.player.SearchablePlayer
import com.advanced_baseball_stats.repository.tables.BatterTable
import com.advanced_baseball_stats.repository.tables.BioTable
import com.advanced_baseball_stats.repository.tables.ChadwickTable
import org.ktorm.dsl.*

object PlayerBioSql
{
    fun getRetroIdFromMlbId(id: Int): String
    {
        var retroId = ""

        DatabaseConnection.database.from(ChadwickTable)
            .select(ChadwickTable.key_retro)
            .where { ChadwickTable.key_mlbam eq id.toString() }
            .limit(1)
            .forEach { row ->
                retroId = row[ChadwickTable.key_retro]!!
            }

        return retroId
    }

    fun getPlayers(searchTerm: String) : MutableSet<SearchablePlayer>
    {
        val delimiter = " "

        val searchTerms = searchTerm.split(delimiter)

        val players: MutableSet<SearchablePlayer> = mutableSetOf()

        DatabaseConnection.database.from(BioTable)
            .select(
                BioTable.playerId, BioTable.first, BioTable.last, BioTable.nickname
            )
            .whereWithConditions {
                for (term in searchTerms)
                {
                    it += (BioTable.first like "$term%") or (BioTable.last like "$term%") or (BioTable.nickname like "$term%")
                }
            }
            .forEach { player ->
                val id          = player[BioTable.playerId   ]
                val firstName   = player[BioTable.first      ]
                val lastName    = player[BioTable.last       ]
                val nickName    = player[BioTable.nickname   ]

                if (id != null && firstName != null && lastName != null && nickName != null) {
                    val curPlayer = SearchablePlayer(id, firstName, lastName, nickName)

                    players.add(curPlayer)
                }
            }

        return players
    }

    fun getPlayer(id: String): Player
    {
        val player = Player(id = id, name = "", height = "", weight = 0)

        DatabaseConnection.database.from(BioTable)
            .select(BioTable.playerId, BioTable.first, BioTable.nickname, BioTable.last, BioTable.height, BioTable.weight)
            .where { BioTable.playerId eq id }
            .limit(1)
            .forEach { row ->
                val firstName   = row[BioTable.first     ]!!
                val nickName    = row[BioTable.nickname  ]!!
                val lastName    = row[BioTable.last      ]!!
                val height      = row[BioTable.height    ]!!
                val weight      = row[BioTable.weight    ]!!

                player.name = "$firstName $nickName $lastName"
                player.height = height
                player.weight = weight
            }
        return player
    }

    fun getPlayerTeams(playerIds: List<String>, startDate: String, endDate: String): Map<String, String>
    {
        val playerTeams = mutableMapOf<String, String>()

        val dateRanges: ClosedRange<String> = startDate..endDate

        DatabaseConnection.database.from(BatterTable)
            .select(BatterTable.id, BatterTable.team)
            .whereWithOrConditions {
                for (playerId in playerIds)
                {
                    it += ((BatterTable.date between dateRanges) and (playerId eq BatterTable.id))
                }
            }
            .orderBy(BatterTable.date.desc())
            .forEach { row ->
                val id      = row[BatterTable.id    ]!!
                val team    = row[BatterTable.team  ]!!

                if (!playerTeams.containsKey(id))
                {
                    playerTeams[id] = team
                }
            }
        return playerTeams
    }
}