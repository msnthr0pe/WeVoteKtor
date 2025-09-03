package com.usersSurveys

import com.UsersSurveysDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object UsersSurveys : Table("users_surveys") {
    val userEmail = varchar("user_email",45)
    val surveyId = integer("survey_id")
    val vote = integer("vote")

    fun insert(usersSurveysDTO: UsersSurveysDTO) {
        transaction {
            insert {
                it[userEmail] = usersSurveysDTO.userEmail
                it[surveyId] = usersSurveysDTO.surveyId
                it[vote] = usersSurveysDTO.vote
            }
        }
    }

    fun fetch(userEmail: String, surveyId: Int): UsersSurveysDTO? {
        return try {
            transaction {
                UsersSurveys
                    .select {
                        (UsersSurveys.userEmail eq userEmail) and
                                (UsersSurveys.surveyId eq surveyId)
                    }
                    .singleOrNull()
                    ?.let { row ->
                        UsersSurveysDTO(
                            userEmail = row[UsersSurveys.userEmail],
                            surveyId = row[UsersSurveys.surveyId],
                            vote = row[vote],
                        )
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteUsersSurvey(userEmail: String, surveyId: Int): Boolean {
        return try {
            transaction {
                UsersSurveys.deleteWhere { (UsersSurveys.userEmail eq userEmail) and
                        (UsersSurveys.surveyId eq surveyId) }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun getSurveysByUser(userEmail: String): List<Int> {
        return transaction {
            select { UsersSurveys.userEmail eq userEmail }
                .map { it[surveyId] }
        }
    }

    fun getUsersBySurvey(surveyId: Int): List<String> {
        return transaction {
            select { UsersSurveys.surveyId eq surveyId }
                .map { it[userEmail] }
        }
    }
}