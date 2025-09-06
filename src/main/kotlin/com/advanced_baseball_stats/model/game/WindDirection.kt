package com.advanced_baseball_stats.model.game

enum class WindDirection(val value: String)
{
    FROM_CENTER_FIELD       ("FROMCF" ),
    FROM_LEFT_FIELD         ("FROMLF" ),
    FROM_RIGHT_FIELD        ("FROMRF" ),
    LEFT_TO_RIGHT           ("LTOR"   ),
    RIGHT_TO_LEFT           ("RTOL"   ),
    TOWARDS_CENTER_FIELD    ("TOCF"   ),
    TOWARDS_LEFT_FIELD      ("TOLF"   ),
    TOWARDS_RIGHT_FIELD     ("TORF"   ),
    UNKNOWN                 ("UNKNOWN");

    companion object {
        fun fromString(value: String): WindDirection {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}