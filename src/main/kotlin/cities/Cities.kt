package com.cities

import com.CityDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Cities : Table("cities") {
    val name = varchar("name", 100)

    fun existsByName(cityName: String): Boolean {
        return transaction {
            Cities.select { name eq cityName }.count() > 0
        }
    }

    fun findByName(cityName: String): CityDto? {
        return transaction {
            Cities.select { name eq cityName }
                .singleOrNull()
                ?.let { row -> CityDto(name = row[name]) }
        }
    }

    fun fetchAll(): List<CityDto> {
        return transaction {
            Cities.selectAll().map { row -> CityDto(name = row[name]) }
        }
    }

    fun insertCity(dto: CityDto) {
        transaction {
            Cities.insert { it[name] = dto.name }
        }
    }

    fun deleteCity(dto: CityDto) {
        transaction {
            Cities.deleteWhere { name eq dto.name }
        }
    }
}
