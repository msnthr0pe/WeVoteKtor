package com.surveys

import com.SurveyDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException

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
}