package com.users

import com.CredentialsDTO
import com.UserDTO
import com.users.Users.access
import com.users.Users.city
import com.users.Users.dob
import com.users.Users.name
import com.users.Users.password
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureGetUserRouting() {
    routing {
        post("/getuser") {
            val request = call.receive<CredentialsDTO>()
            val email = request.email
            try {
                val result: UserDTO? = transaction {
                    Users
                        .select { Users.email eq email }
                        .singleOrNull()
                        ?.let { row ->
                            UserDTO(
                                name = row[name],
                                email = row[Users.email],
                                dob = row[dob],
                                city = row[city],
                                password = row[password],
                                access = row[access],
                            )
                        }
                }
                call.respond(result ?: "User Not found")
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Server error", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
