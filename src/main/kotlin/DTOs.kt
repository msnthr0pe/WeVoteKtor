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
data class SurveyDTOWithId (
    val id: Int,
    val title: String,
    val firstChoice: String,
    val secondChoice: String,
    val thirdChoice: String,
)
@Serializable
data class EmailRequest(val email: String)
@Serializable
data class SurveyIdRequest(val id: Int)

@Serializable
data class SurveysByUserResponse(
    val email: String,
    val surveys: List<Int>,
    val count: Int
)

@Serializable
data class UsersBySurveyResponse(
    val id: Int,
    val emails: List<String>,
    val count: Int
)


@Serializable
data class UsersSurveysDTO (
    val userEmail: String,
    val surveyId: Int,
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