package com

import com.users.configureGetUserRouting
import com.users.configureUpdateUserRouting
import com.login.configureLoginRouting
import com.register.configureRegisterRouting
import com.surveys.configureSurveyRouting
import com.usersSurveys.configureUsersSurveysRouting
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    Database.connect("jdbc:postgresql://localhost:5432/voting",
        "org.postgresql.Driver",
        "postgres",
        "root")

    configureRegisterRouting()
    configureLoginRouting()
    configureGetUserRouting()
    configureUpdateUserRouting()

    configureSurveyRouting()

    configureUsersSurveysRouting()

    configureSerialization()
}
