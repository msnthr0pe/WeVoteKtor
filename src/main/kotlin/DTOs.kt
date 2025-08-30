package com

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO (
    val name: String,
    val email: String,
    val dob: String,
    val city: String,
    val password: String,
    val access: String,
)

@Serializable
data class SurveyDTO (
    val title: String,
    val firstChoice: String,
    val secondChoice: String,
    val thirdChoice: String,
)

@Serializable
data class TitleDTO (
    val title: String
)

@Serializable
class SurveyDTOWithId (
    val id: Int,
    val title: String,
    val firstChoice: String,
    val secondChoice: String,
    val thirdChoice: String,
)

@Serializable
data class CredentialsDTO(
    val email: String,
    val password: String
)

@Serializable
data class TextDTO(
    val text: String,
)