package com.surveys

import com.SurveyDTO
import com.SurveyDTOWithId
import com.TitleDTO
import com.surveys.Surveys.firstChoice
import com.surveys.Surveys.idSurvey
import com.surveys.Surveys.secondChoice
import com.surveys.Surveys.surveyTitle
import com.surveys.Surveys.thirdChoice
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class SurveyController(val call: ApplicationCall) {
    suspend fun addSurvey() {
        val surveyReceiveRemote = call.receive<SurveyDTO>()
        val surveyDTO = Surveys.fetchSurvey(surveyReceiveRemote.title)
        if (surveyDTO != null) {
            call.respond(HttpStatusCode.Conflict, "Survey already exists")
        } else {
            try {
                Surveys.insertSurvey(
                    SurveyDTO(
                        id = surveyReceiveRemote.id,
                        title = surveyReceiveRemote.title,
                        firstChoice = surveyReceiveRemote.firstChoice,
                        secondChoice = surveyReceiveRemote.secondChoice,
                        thirdChoice = surveyReceiveRemote.thirdChoice,
                    )
                )
                call.respond(HttpStatusCode.OK)
            } catch (e: ExposedSQLException) {
                e.printStackTrace()
                call.respond(HttpStatusCode.Conflict, "SQL error: ${e.localizedMessage}")
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getSurvey() {
        val request = call.receive<TitleDTO>()
        val email = request.title
        try {
            val result: SurveyDTOWithId? = transaction {
                Surveys
                    .select { surveyTitle eq email }
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
            call.respond(result ?: "User Not found")
        } catch (e: Exception) {
            e.printStackTrace()
            call.respondText("Server error", status = HttpStatusCode.InternalServerError)
        }
    }

    suspend fun archiveSurvey() {
        val request = call.receive<TitleDTO>()
        val title = request.title
        if (Surveys.archiveSurveyByTitle(title)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText("Survey doesn't exist", status = HttpStatusCode.Conflict)
        }
    }
}