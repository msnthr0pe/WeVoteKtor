package com.surveys

import com.SurveyDTO
import com.SurveyIdRequest
import com.usersSurveys.UsersSurveys
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureSurveyRouting() {
    routing {
        post("/addsurvey") {
            val surveyController = SurveyController(call)
            surveyController.addSurvey()
        }
        post("/deletesurveyinfo") {

            val request = call.receive<SurveyIdRequest>()
            val id = request.id

            if (!Surveys.deleteSurveyById(id)) {
                call.respondText("Survey with id=$id not found", status = HttpStatusCode.NotFound)
            }

            if (UsersSurveys.deleteSurveyInfo(id)) {
                call.respond(HttpStatusCode.OK, "Survey info deleted successfully")
            } else {
                call.respondText("Survey info with id=$id not found", status = HttpStatusCode.NotFound)
            }
        }
        post("/getsurvey") {
            val surveyController = SurveyController(call)
            surveyController.getSurvey()
        }
        post("/archivesurvey") {
            val surveyController = SurveyController(call)
            surveyController.archiveSurvey()
        }
        get("/getsurveys") {
            val surveys = transaction {
                Surveys
                    .select {Surveys.isArchived eq false}
                    .orderBy(Surveys.idSurvey, SortOrder.DESC)
                    .map { row ->
                    SurveyDTO(
                        id = row[Surveys.idSurvey],
                        title = row[Surveys.surveyTitle],
                        firstChoice = row[Surveys.firstChoice],
                        secondChoice = row[Surveys.secondChoice],
                        thirdChoice = row[Surveys.thirdChoice],
                    )
                }
            }
            call.respond(surveys)
        }
        get("/getarchivedsurveys") {
            val surveys = transaction {
                Surveys
                    .select {Surveys.isArchived eq true}
                    .orderBy(Surveys.idSurvey, SortOrder.DESC)
                    .map { row ->
                        SurveyDTO(
                            id = row[Surveys.idSurvey],
                            title = row[Surveys.surveyTitle],
                            firstChoice = row[Surveys.firstChoice],
                            secondChoice = row[Surveys.secondChoice],
                            thirdChoice = row[Surveys.thirdChoice],
                        )
                    }
            }
            call.respond(surveys)
        }
    }
}