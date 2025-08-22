package com.example.stripesnippet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateSetupIntentResponseDTO(
    val result: CreateSetupIntentResponse
)

@JsonClass(generateAdapter = true)
data class CreateSetupIntentResponse(
    val clientSecret: String
)