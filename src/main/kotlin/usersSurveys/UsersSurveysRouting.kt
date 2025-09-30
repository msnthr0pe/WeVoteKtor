package com.usersSurveys

import com.EmailRequest
import com.SurveyIdRequest
import com.SurveyVotesDTO
import com.SurveysByUserResponse
import com.UsersBySurveyResponse
import com.UsersSurveysDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Application.configureUsersSurveysRouting() {
    routing {
        post ("/addusersurvey") {
            val request = call.receive<UsersSurveysDTO>()
            val usersSurveysDTO = UsersSurveys.fetch(request.userEmail, request.surveyId)
            if (usersSurveysDTO != null) {
                UsersSurveys.deleteUsersSurvey(request.userEmail, request.surveyId)
            }
            try {
                UsersSurveys.insert(
                    UsersSurveysDTO (
                        userEmail = request.userEmail,
                        surveyId = request.surveyId,
                        vote = request.vote,
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

        post("getsurveysbyuser") {
            try {
                val request = call.receive<EmailRequest>()
                val surveyIds = UsersSurveys.getSurveysByUser(request.email)

                call.respond(SurveysByUserResponse(
                    email = request.email,
                    surveys = surveyIds,
                    count = surveyIds.size
                ))

            } catch (_: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error: Invalid JSON format or email missing")
            }
        }

        post("getusersbysurvey") {
            try {
                val request = call.receive<SurveyIdRequest>()
                val userEmails = UsersSurveys.getUsersBySurvey(request.id)

                call.respond(UsersBySurveyResponse(
                    id = request.id,
                    emails = userEmails,
                    count = userEmails.size
                ))
            } catch (_: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error: Invalid JSON format or survey ID missing")
            }
        }

        post("getsurveyvotes") {
            try {
                val request = call.receive<SurveyIdRequest>()
                val response = UsersSurveys.fetchSurveyVotes(request.id)

                call.respond(SurveyVotesDTO(response?.votes ?: mapOf(), response?.votesPercentage ?: mapOf()))
            } catch (_: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error: Invalid JSON format or survey ID missing")
            }
        }

    }
}