package com.surveys

import com.SurveyDTO
import io.ktor.server.application.Application
import io.ktor.server.response.respond
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
                    .orderBy(Surveys.idSurvey, SortOrder.DESC)   // <-- сортировка по убыванию
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