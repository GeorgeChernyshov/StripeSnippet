package com.example.stripesnippet.ui.screen

import android.icu.math.BigDecimal
import androidx.annotation.StringRes
import com.example.stripesnippet.R

data class PaymentIntegrationScreenState(
    val paymentStatus: PaymentStatus,
    val clientSecret: String?,
    val amount: BigDecimal,
    val isLoading: Boolean
) {
    companion object {
        val DEFAULT = PaymentIntegrationScreenState(
            paymentStatus = PaymentStatus.READY,
            clientSecret = null,
            amount = BigDecimal.ZERO,
            isLoading = false
        )
    }
}

enum class PaymentStatus(@StringRes val displayRes: Int) {
    READY(R.string.payment_status_ready),
    OPENING(R.string.payment_status_opening),
    FETCHING( R.string.payment_status_fetching),
    FETCHED(R.string.payment_status_fetched),
    ERROR(R.string.payment_status_error),
    CANCELED(R.string.payment_status_canceled),
    SUCCESSFUL(R.string.payment_status_successful),
    FAILED(R.string.payment_status_failed);
}