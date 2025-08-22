package com.example.stripesnippet.network

import com.example.stripesnippet.model.CreatePaymentIntentRequest
import com.example.stripesnippet.model.CreatePaymentIntentResponse
import com.example.stripesnippet.model.CreateSetupIntentRequest
import com.example.stripesnippet.model.CreateSetupIntentResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface StripeBackendService {

    @POST("/createPaymentIntent")
    suspend fun createPaymentIntent(
        @Body request: CreatePaymentIntentRequest
    ): CreatePaymentIntentResponse

    @POST("/createSetupIntent")
    suspend fun createSetupIntent(
        @Body request: CreateSetupIntentRequest
    ): CreateSetupIntentResponseDTO
}