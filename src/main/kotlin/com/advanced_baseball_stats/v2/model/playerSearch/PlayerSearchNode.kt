package com.advanced_baseball_stats.v2.model.playerSearch

class PlayerSearchNode
{
    private var playerSearchInfo    : MutableList<PlayerSearchInfo> = mutableListOf();
    private val children            : Array<PlayerSearchNode?>      = arrayOfNulls(256)

    fun isLeafNode(): Boolean
    {
        return this.playerSearchInfo.isNotEmpty()
    }

    fun getPlayerSearchInfo(): List<PlayerSearchInfo>
    {
        return this.playerSearchInfo
    }

    fun setPlayerSearchInfo(playerSearchInfo: PlayerSearchInfo)
    {
        this.playerSearchInfo.add(playerSearchInfo)
    }

    fun getChildren(): Array<PlayerSearchNode?>
    {
        return this.children
    }

    fun doesChildExist(curChar: Char): Boolean
    {
        val index = curChar.code

        if (index < 0 || index > 255)
        {
            return false
        }

        return this.children[index] != null
    }

    fun getChild(curChar: Char): PlayerSearchNode?
    {
        val index = curChar.code

        if (index < 0 || index > 255)
        {
            return null
        }

        return this.children[index]
    }

    fun createChild(curChar: Char): PlayerSearchNode?
    {
        val index = curChar.code

        if (index < 0 || index > 255)
        {
            return null
        }
        else if (this.children[index] != null)
        {
            return this.children[index]
        }
        else
        {
            val searchNode = PlayerSearchNode()

            this.children[index] = searchNode

            return searchNode
        }
    }
}