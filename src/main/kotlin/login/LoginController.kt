package com.login

import com.CredentialsDTO
import com.users.Users
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class LoginController (private val call: ApplicationCall) {

    suspend fun performLogin() {
        val receive = call.receive<CredentialsDTO>()
        val userDTO = Users.fetchUser(receive.email)

        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        } else {
            if (userDTO.password != receive.password) {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
                return
            }
            call.respond(HttpStatusCode.OK)
        }
    }

}