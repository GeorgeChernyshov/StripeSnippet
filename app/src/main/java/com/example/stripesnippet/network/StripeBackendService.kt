package com.example.stripesnippet.network

import com.example.stripesnippet.model.CreatePaymentIntentRequest
import com.example.stripesnippet.model.CreatePaymentIntentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface StripeBackendService {

    @POST("/createPaymentIntent")
    suspend fun createPaymentIntent(
        @Body request: CreatePaymentIntentRequest
    ): CreatePaymentIntentResponse
}