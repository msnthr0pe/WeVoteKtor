package com.surveys

import com.SurveyDTO
import com.SurveyDTOWithId
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Surveys : Table("surveys") {
    val idSurvey = integer("id")
    val surveyTitle = varchar("title", 45)
    val firstChoice = varchar("first_choice", 45)
    val secondChoice = varchar("second_choice", 45)
    val thirdChoice = varchar("third_choice", 45)

    fun insertSurvey(surveyDTO: SurveyDTO) {
        transaction {
            insert {
                it[idSurvey] = (Surveys.selectAll().count() + 1).toInt()
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
}