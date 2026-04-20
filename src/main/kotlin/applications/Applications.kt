package com.applications

import com.ApplicationDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Applications : Table("applications") {
    val appId = integer("id")
    val appTitle = varchar("title", 500)
    val firstChoice = varchar("first_choice", 255)
    val secondChoice = varchar("second_choice", 255)
    val thirdChoice = varchar("third_choice", 255)
    val appStatus = varchar("status", 45)
    val appUserEmail = varchar("user_email", 255)

    fun insertApplication(dto: ApplicationDTO) {
        transaction {
            val maxId = slice(appId.max()).selectAll().singleOrNull()?.getOrNull(appId.max()) ?: 0
            insert {
                it[appId] = maxId + 1
                it[appTitle] = dto.title
                it[firstChoice] = dto.firstChoice
                it[secondChoice] = dto.secondChoice
                it[thirdChoice] = dto.thirdChoice
                it[appStatus] = "PENDING"
                it[appUserEmail] = dto.userEmail
            }
        }
    }

    fun fetchAllApplications(): List<ApplicationDTO> {
        return transaction {
            selectAll().map { row ->
                ApplicationDTO(
                    id = row[appId],
                    title = row[appTitle],
                    firstChoice = row[firstChoice],
                    secondChoice = row[secondChoice],
                    thirdChoice = row[thirdChoice],
                    status = row[appStatus],
                    userEmail = row[appUserEmail]
                )
            }
        }
    }

    fun fetchApplicationsByEmail(email: String): List<ApplicationDTO> {
        return transaction {
            select { appUserEmail eq email }.map { row ->
                ApplicationDTO(
                    id = row[appId],
                    title = row[appTitle],
                    firstChoice = row[firstChoice],
                    secondChoice = row[secondChoice],
                    thirdChoice = row[thirdChoice],
                    status = row[appStatus],
                    userEmail = row[appUserEmail]
                )
            }
        }
    }

    fun applicationExists(id: Int): Boolean {
        return transaction {
            select { appId eq id }.count() > 0
        }
    }

    fun updateStatus(id: Int, newStatus: String): Boolean {
        return try {
            transaction {
                val updated = Applications.update({ appId eq id }) {
                    it[appStatus] = newStatus
                }
                updated > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
