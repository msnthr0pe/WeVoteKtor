package com.register

import com.UserDTO
import com.users.Users
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException

class RegisterController (val call: ApplicationCall) {
    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<UserDTO>()
        val userDTO = Users.fetchUser(registerReceiveRemote.email)
        if (userDTO != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
        } else {
            try {
                Users.insertUser(
                    UserDTO(
                        name = registerReceiveRemote.name,
                        email = registerReceiveRemote.email,
                        dob = registerReceiveRemote.dob,
                        city = registerReceiveRemote.city,
                        password = registerReceiveRemote.password,
                        access = registerReceiveRemote.access
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
    }
}