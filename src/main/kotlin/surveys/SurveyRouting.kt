package com.surveys

import com.register.RegisterController
import io.ktor.server.application.Application
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureSurveyRouting() {
    routing {
        post("/addsurvey") {
            val surveyController = SurveyController(call)
            surveyController.addSurvey()
        }
    }
}