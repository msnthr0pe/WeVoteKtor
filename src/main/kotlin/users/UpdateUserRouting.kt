package com.users

import com.TextDTO
import com.UserDTO
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Application.configureUpdateUserRouting() {
    routing {
        post("/updateuser") {
            val updateRequest = call.receive<UserDTO>()

            val rowsUpdated = transaction {
                Users.update({ Users.email eq updateRequest.email }) {
                    it[password] = updateRequest.password
                }
            }

            if (rowsUpdated > 0) {
                call.respond(TextDTO("Пароль изменён"))
            } else {
                call.respond(TextDTO("Пользователь не найден"))
            }
        }
    }
}
