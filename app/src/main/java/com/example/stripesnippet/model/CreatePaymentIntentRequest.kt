package com.example.stripesnippet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePaymentIntentRequest(
    val amount: Long, // Amount in cents (e.g., 1099 for $10.99)
    val currency: String
)