package com.example.stripesnippet.ui.screen

import android.icu.math.BigDecimal
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stripesnippet.model.CreatePaymentIntentRequest
import com.example.stripesnippet.model.CreateSetupIntentRequest
import com.example.stripesnippet.model.EmptyMapWrapper
import com.example.stripesnippet.repository.PaymentRepository
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PaymentIntegrationViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PaymentIntegrationScreenState.DEFAULT)
    val uiState = _uiState.asStateFlow()

    fun setAmount(amount: BigDecimal) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(
            amount = amount
        ))
    }

    fun fetchPaymentIntent(currency: String) = viewModelScope.launch {
        _uiState.emit(
            _uiState.value.copy(
                paymentStatus = PaymentStatus.FETCHING,
                setupClientSecret = null,
                paymentClientSecret = null,
                isLoading = true
            )
        )

        try {
            Log.d(
                TAG,
                "Requesting PaymentIntent from backend via Repository: " +
                        "amount=${uiState.value.amount}, currency=$currency"
            )

            val longAmount = uiState.value
                .amount
                .movePointRight(2)
                .toLong()

            val response = paymentRepository.createPaymentIntent(
                CreatePaymentIntentRequest(
                    amount = longAmount,
                    currency = currency
                )
            )

            Log.d(TAG, "Client secret received successfully (not logging full secret).")

            _uiState.emit(uiState.value.copy(
                paymentClientSecret = response.clientSecret,
                paymentStatus = PaymentStatus.FETCHED
            ))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = "Backend Error: HTTP ${e.code()} - ${errorBody ?: "Unknown"}"
            Log.e(TAG, errorMessage, e)
            _uiState.emit(uiState.value.copy(
                paymentStatus = PaymentStatus.ERROR
            ))
        } catch (e: Exception) {
            val errorMessage = "Network or JSON error: ${e.message ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            _uiState.emit(uiState.value.copy(
                paymentStatus = PaymentStatus.ERROR
            ))
        } catch (e: Exception) {
            val errorMessage = "Network or JSON error: ${e.message ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            _uiState.emit(uiState.value.copy(
                paymentStatus = PaymentStatus.ERROR
            ))
        } finally {
            if (uiState.value.paymentClientSecret == null) {
                _uiState.emit(uiState.value.copy(
                    isLoading = false
                ))
            }
        }
    }

    fun fetchSetupIntent() {
        viewModelScope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    paymentStatus = PaymentStatus.FETCHING,
                    setupClientSecret = null,
                    paymentClientSecret = null,
                    isLoading = true
                )
            )

            try {
                Log.d(TAG, "Requesting SetupIntent from backend via Repository.")
                val response = paymentRepository.createSetupIntent(
                    CreateSetupIntentRequest(EmptyMapWrapper())
                )

                Log.d(TAG, "SetupIntent client secret received successfully.")
                _uiState.emit(uiState.value.copy(
                    setupClientSecret = response?.clientSecret
                ))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = "Backend Error: HTTP ${e.code()} - ${errorBody ?: "Unknown"}"
                Log.e(TAG, errorMessage, e)
            } catch (e: Exception) {
                val errorMessage = "Network or JSON error: ${e.message ?: "Unknown error"}"
                Log.e(TAG, errorMessage, e)
            } finally {
                if (uiState.value.setupClientSecret == null) {
                    _uiState.emit(uiState.value.copy(
                        isLoading = false
                    ))
                }
            }
        }
    }

    fun handlePaymentSheetResult(result: PaymentSheetResult) {
        val paymentStatus = when (result) {
            is PaymentSheetResult.Canceled -> PaymentStatus.CANCELED
            is PaymentSheetResult.Completed -> PaymentStatus.SUCCESSFUL
            is PaymentSheetResult.Failed -> PaymentStatus.FAILED
        }

        viewModelScope.launch {
            _uiState.emit(uiState.value.copy(
                paymentStatus = paymentStatus,
                paymentClientSecret = null,
                isLoading = false
            ))
        }
    }

    fun onPaymentSheetPresented() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(
            paymentStatus = PaymentStatus.OPENING,
            isLoading = true
        ))
    }

    companion object {
        private const val TAG = "PaymentIntegrationViewModel"
    }
}