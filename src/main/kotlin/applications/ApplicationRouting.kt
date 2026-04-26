package com.applications

import com.ApplicationDTO
import com.ApplicationIdRequest
import com.ApplicationStatusUpdateDTO
import com.EmailDTO
import com.users.Users
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureApplicationRouting() {
    routing {
        post("/getapplications") {
            val request = call.receive<EmailDTO>()
            val user = Users.fetchUser(request.email)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "User not found")
                return@post
            }
            val applications = if (user.access == "developer") {
                Applications.fetchAllApplications()
            } else {
                Applications.fetchApplicationsByCity(user.city)
            }
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

        // POST /deleteapplication
        // Полностью удаляет заявку из базы данных по её ID.
        //
        // Тело запроса (JSON):
        //   { "id": <Int> }   — идентификатор заявки (поле id таблицы applications)
        //
        // Логика:
        //   1. Принять тело запроса и извлечь поле id.
        //   2. Выполнить DELETE FROM applications WHERE id = :id
        //   3. Вернуть 200 OK (без тела) если строка удалена.
        //   4. Вернуть 404 если заявки с таким id не существует.
        //
        // После удаления:
        //   — /getuserapplications больше не вернёт эту заявку.
        //   — /getapplications (для администратора) тоже её не вернёт.
        //
        // Пример запроса:
        //   POST /deleteapplication
        //   Content-Type: application/json
        //   { "id": 42 }
        post("/deleteapplication") {
            val request = call.receive<ApplicationIdRequest>()
            if (!Applications.deleteApplication(request.id)) {
                call.respond(HttpStatusCode.NotFound, "Application with id=${request.id} not found")
                return@post
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
