package com.example.stripesnippet.repository

import com.example.stripesnippet.model.CreatePaymentIntentRequest
import com.example.stripesnippet.network.StripeBackendService
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val stripeBackendService: StripeBackendService
) {

    suspend fun createPaymentIntent(
        request: CreatePaymentIntentRequest
    ) = stripeBackendService.createPaymentIntent(request)
}