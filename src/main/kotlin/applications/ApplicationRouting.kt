package com.applications

import com.ApplicationDTO
import com.ApplicationStatusUpdateDTO
import com.EmailDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureApplicationRouting() {
    routing {
        get("/getapplications") {
            val applications = Applications.fetchAllApplications()
            call.respond(applications)
        }

        post("/getuserapplications") {
            val request = call.receive<EmailDTO>()
            val applications = Applications.fetchApplicationsByEmail(request.email)
            call.respond(applications)
        }

        post("/updateapplication") {
            val request = call.receive<ApplicationStatusUpdateDTO>()

            if (request.status != "ACCEPTED" && request.status != "REJECTED") {
                call.respond(HttpStatusCode.BadRequest, "Status must be ACCEPTED or REJECTED")
                return@post
            }

            if (!Applications.applicationExists(request.id)) {
                call.respond(HttpStatusCode.NotFound, "Application with id=${request.id} not found")
                return@post
            }

            Applications.updateStatus(request.id, request.status)
            call.respond(HttpStatusCode.OK)
        }

        post("/addapplication") {
            val dto = call.receive<ApplicationDTO>()
            Applications.insertApplication(dto)
            call.respond(HttpStatusCode.OK)
        }
    }
}
