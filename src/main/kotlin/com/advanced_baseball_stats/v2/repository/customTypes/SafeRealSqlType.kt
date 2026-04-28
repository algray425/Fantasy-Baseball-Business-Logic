package com.advanced_baseball_stats.v2.repository.customTypes

import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

object SafeRealSqlType : SqlType<Double>(Types.REAL, "real") {
    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: Double) {
        ps.setFloat(index, parameter.toFloat())
    }

    override fun doGetResult(rs: ResultSet, index: Int): Double? {
        // Read as string first to catch "Inf" before it hits toDouble()
        val raw = rs.getString(index) ?: return null
        return when (raw.lowercase()) {
            "inf", "infinity", "-inf", "-infinity", "nan" -> 0.0
            else -> raw.toDouble()
        }
    }
}