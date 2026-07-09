package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.v2.model.playerSearch.PlayerSearchInfo
import com.advanced_baseball_stats.v2.model.playerSearch.PlayerSearchNode
import com.advanced_baseball_stats.v2.repository.players.PlayerSql
import io.ktor.util.*

class PlayerSearchHandler
{
    private var rootSearchNode: PlayerSearchNode = PlayerSearchNode()

    init {
        val batters     = PlayerSql.getSearchableBatters    (2026)
        val pitchers    = PlayerSql.getSearchablePitchers   (2026)

        val searchablePlayers = batters + pitchers

        for (player in searchablePlayers)
        {
            this.insertPlayer(player)
        }
    }

    private fun insertPlayer(playerSearchInfo: PlayerSearchInfo)
    {
        val playerName = playerSearchInfo.playerName.toLowerCasePreservingASCIIRules()

        var curSearchNode = this.rootSearchNode

        for ((index, curChar) in playerName.withIndex())
        {
            if (curSearchNode.doesChildExist(curChar))
            {
                curSearchNode = curSearchNode.getChild(curChar)!!
            }
            else
            {
                curSearchNode = curSearchNode.createChild(curChar)!!
            }

            if (index == playerName.length - 1)
            {
                curSearchNode.setPlayerSearchInfo(playerSearchInfo)
            }
        }
    }

    fun getPlayersFromSearchQuery(searchQuery: String): List<PlayerSearchInfo>
    {
        val convertedSearchQuery = searchQuery.toLowerCasePreservingASCIIRules()

        var curSearchNode = this.rootSearchNode

        for (curChar in convertedSearchQuery)
        {
            if (!curSearchNode.doesChildExist(curChar))
            {
                return listOf()
            }

            curSearchNode = curSearchNode.getChild(curChar)!!
        }

        val playerList = mutableListOf<PlayerSearchInfo>()

        this.playerDfs(curSearchNode, playerList)

        return playerList
    }

    private fun playerDfs(curSearchNode: PlayerSearchNode?, playerList: MutableList<PlayerSearchInfo>)
    {
        if (curSearchNode == null)
        {
            return
        }

        if (curSearchNode.isLeafNode())
        {
            playerList.addAll(curSearchNode.getPlayerSearchInfo())
        }

        for (child in curSearchNode.getChildren())
        {
            if (child != null)
            {
                this.playerDfs(child, playerList)
            }
        }
    }
}