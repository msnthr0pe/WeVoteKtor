package com.users

import com.UserDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    val name = varchar("name",45)
    val email = varchar("email", 45)
    val dob = varchar("dob", 45)
    val city = varchar("city", 45)
    val password = varchar("password", 45)
    val access = varchar("access", 45)

    fun insertUser(userDTO: UserDTO) {
        transaction {
            insert {
                it[name] = userDTO.name
                it[email] = userDTO.email
                it[dob] = userDTO.dob
                it[city] = userDTO.city
                it[password] = userDTO.password
                it[access] = userDTO.access
            }
        }
    }
    fun fetchUser(login: String): UserDTO? {
        return try {
            val result: UserDTO? = transaction {
                Users
                    .select { email eq login }
                    .singleOrNull()
                    ?.let { row ->
                        UserDTO(
                            name = row[name],
                            email = row[email],
                            dob = row[dob],
                            city = row[city],
                            password = row[password],
                            access = row[access],
                        )
                    }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}