package com.example.stripesnippet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateSetupIntentRequest(
    val data: EmptyMapWrapper
)

@JsonClass(generateAdapter = true)
class EmptyMapWrapper