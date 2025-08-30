package com.surveys

import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

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
    }
}