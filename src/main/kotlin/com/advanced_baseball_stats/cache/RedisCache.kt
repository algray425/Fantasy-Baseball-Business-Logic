package com.advanced_baseball_stats.cache

import redis.clients.jedis.Jedis


object RedisCache {
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
}