package com.advanced_baseball_stats.cache

import redis.clients.jedis.Jedis
import java.time.Instant


object RedisCache
{
    //TODO: Jedis connection pool?
    private val jedis = Jedis()

    fun addToCache(key: String, value: String, expiryTime: Long)
    {
        this.jedis.setex(key, expiryTime, value)
    }

    fun retrieveFromCache(key: String): String?
    {
        val value = this.jedis.get(key)

        return value
    }

    //TODO: How to set and maintain TTL (for entire set, for individual entries)
    fun addToSet(key: String, date: String, value: String)
    {
        //TODO: Try catch
        val score = Instant.parse(date).epochSecond.toDouble()

        this.jedis.zadd(key, score, value)

        //TODO: Manage expiration based on a separate sorted set rather than on the key itself?
        this.jedis.expire(key, 60)
    }

    fun getFromSet(key: String, startDate: String, endDate: String): List<String?>
    {
        //TODO: Try catch
        val startScore  = Instant.parse(startDate   ).epochSecond.toDouble()
        val endScore    = Instant.parse(endDate     ).epochSecond.toDouble()

        val value = this.jedis.zrangeByScore(key, startScore, endScore)

        return value
    }
}