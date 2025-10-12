package com.surveys

import com.SurveyDTO
import com.SurveyDTOWithId
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Surveys : Table("surveys") {
    val idSurvey = integer("id")
    val surveyTitle = varchar("title", 1000)
    val firstChoice = varchar("first_choice", 45)
    val secondChoice = varchar("second_choice", 45)
    val thirdChoice = varchar("third_choice", 45)
    val isArchived = bool("is_archived")

    fun insertSurvey(surveyDTO: SurveyDTO) {
        transaction {
            val maxId = slice(idSurvey.max())
                .selectAll()
                .singleOrNull()?.getOrNull(idSurvey.max()) ?: 0
            insert {
                it[idSurvey] = (maxId + 1)
                it[surveyTitle] = surveyDTO.title
                it[firstChoice] = surveyDTO.firstChoice
                it[secondChoice] = surveyDTO.secondChoice
                it[thirdChoice] = surveyDTO.thirdChoice
            }
        }
    }

    fun fetchSurvey(title: String): SurveyDTOWithId? {
        return try {
            val result: SurveyDTOWithId? = transaction {
                Surveys
                    .select { surveyTitle eq title }
                    .singleOrNull()
                    ?.let { row ->
                        SurveyDTOWithId(
                            id = row[idSurvey],
                            title = row[surveyTitle],
                            firstChoice = row[firstChoice],
                            secondChoice = row[secondChoice],
                            thirdChoice = row[thirdChoice],
                        )
                    }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun archiveSurveyByTitle(title: String): Boolean {
        return try {
            transaction {
                val updatedRows = Surveys.update({ surveyTitle eq title }) {
                    it[isArchived] = true
                }
                updatedRows > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteSurveyById(id: Int): Boolean {
        return try {
            transaction {
                val deletedRows = Surveys.deleteWhere { idSurvey eq id }
                deletedRows > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}