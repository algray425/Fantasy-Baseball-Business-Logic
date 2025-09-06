package com.advanced_baseball_stats.repository.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object BioTable : Table<Nothing>("bios")
{
    val playerId        = varchar   ("PLAYERID"         )
    val last            = varchar   ("LAST"             )
    val first           = varchar   ("FIRST"            )
    val nickname        = varchar   ("NICKNAME"         )
    val birthDate       = varchar   ("BIRTHDATE"        )
    val birthCity       = varchar   ("BIRTH_CITY"       )
    val birthState      = varchar   ("BIRTH_STATE"      )
    val birthCountry    = varchar   ("BIRTH_COUNTRY"    )
    val playDebut       = varchar   ("PLAY_DEBUT"       )
    val playLastGame    = varchar   ("PLAY_LASTGAME"    )
    val managerDebut    = varchar   ("MGR_DEBUT"        )
    val managerLastGame = varchar   ("MGR_LASTGAME"     )
    val coachDebut      = varchar   ("COACH_DEBUT"      )
    val coachLastGame   = varchar   ("COACH_LASTGAME"   )
    val umpDebut        = varchar   ("UMP_DEBUT"        )
    val umpLastGame     = varchar   ("UMP_LASTGAME"     )
    val deathDate       = varchar   ("DEATHDATE"        )
    val deathCity       = varchar   ("DEATH_CITY"       )
    val deathState      = varchar   ("DEATH_STATE"      )
    val deathCountry    = varchar   ("DEATH_COUNTRY"    )
    val bats            = varchar   ("BATS"             )
    val throws          = varchar   ("THROWS"           )
    val height          = varchar   ("HEIGHT"           )
    val weight          = int       ("WEIGHT"           )
    val cemetery        = varchar   ("CEMETERY"         )
    val cemeteryCity    = varchar   ("CEME_CITY"        )
    val cemeteryState   = varchar   ("CEME_STATE"       )
    val cemeteryCountry = varchar   ("CEME_COUNTRY"     )
    val cemeteryNote    = varchar   ("CEME_NOTE"        )
    val birthName       = varchar   ("BIRTH_NAME"       )
    val nameChange      = varchar   ("NAME_CHG"         )
    val batChange       = varchar   ("BAT_CHG"          )
    val hallOfFame      = varchar   ("HOF"              )
}