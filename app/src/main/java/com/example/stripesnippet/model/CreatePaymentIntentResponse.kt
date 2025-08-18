package com.example.stripesnippet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePaymentIntentResponse(
    val clientSecret: String
)