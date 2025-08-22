package com.example.stripesnippet.ui.screen

import android.icu.math.BigDecimal
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stripesnippet.Config.STRIPE_PUBLISHABLE_KEY
import com.example.stripesnippet.R
import com.example.stripesnippet.ui.components.AppBar
import com.example.stripesnippet.ui.theme.StripeSnippetTheme
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheet.Builder
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback

@Composable
fun PaymentIntegrationScreen() {
    val context = LocalContext.current
    val viewModel: PaymentIntegrationViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    val paymentResultCallback = object : PaymentSheetResultCallback {
        override fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
            viewModel.handlePaymentSheetResult(paymentSheetResult)
        }
    }

    val paymentSheet = remember(paymentResultCallback) {
        Builder(paymentResultCallback)
    }.build()

    LaunchedEffect(Unit) {
        try {
            PaymentConfiguration.init(context, STRIPE_PUBLISHABLE_KEY)
            Log.d(TAG, "Stripe PaymentConfiguration initialized with $STRIPE_PUBLISHABLE_KEY")
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Error initializing Stripe PaymentConfiguration: ${e.message}", e)
        }
    }

    LaunchedEffect(uiState.value.paymentClientSecret) {
        uiState.value.paymentClientSecret?.let { secret ->
            Log.d(TAG, "Client secret received from ViewModel. Presenting PaymentSheet.")
            viewModel.onPaymentSheetPresented()

            paymentSheet.presentWithPaymentIntent(
                secret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "Your Company Name",
                    allowsDelayedPaymentMethods = true,
                )
            )
        }
    }

    LaunchedEffect(uiState.value.setupClientSecret) {
        uiState.value.setupClientSecret?.let { secret ->
            Log.d(TAG, "SetupIntent client secret received from ViewModel. Presenting PaymentSheet for saving method.")
            viewModel.onPaymentSheetPresented()
            paymentSheet.presentWithSetupIntent(
                secret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "Your Company Name", // Displayed in the sheet
                    allowsDelayedPaymentMethods = true,
                )
            )
        }
    }

    PaymentIntegrationScreenContent(
        uiState = uiState.value,
        onPriceChanged = { viewModel.setAmount(it) },
        onPaymentButtonClicked = { viewModel.fetchPaymentIntent( "usd") },
        onSaveSetupButtonClicked = { viewModel.fetchSetupIntent() }
    )
}

@Composable
fun PaymentIntegrationScreenContent(
    uiState: PaymentIntegrationScreenState,
    onPriceChanged: (BigDecimal) -> Unit,
    onPaymentButtonClicked: () -> Unit,
    onSaveSetupButtonClicked: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = { AppBar(name = stringResource(R.string.label_payment_integration)) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.payment_integration_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }

            Text(
                text = stringResource(
                    id = R.string.payment_integration_status,
                    stringResource(uiState.paymentStatus.displayRes)
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(stringResource(R.string.payment_integration_amount_hint))

            TextField(
                value = uiState.amount.toString(),
                onValueChange = { value: String ->
                    val amount = try {
                        BigDecimal(value)
                    } catch (_ : Exception) {
                        BigDecimal.ZERO
                    }

                    onPriceChanged(amount)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Button(
                onClick = {
                    if (uiState.isLoading) return@Button

                    onPaymentButtonClicked()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading // Disable button when loading
            ) {
                Text(stringResource(R.string.payment_integration_button))
            }

            Text(
                text = stringResource(R.string.payment_integration_button_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onSaveSetupButtonClicked,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(R.string.payment_integration_save_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentIntegrationScreenContentPreview() {
    StripeSnippetTheme {
        PaymentIntegrationScreenContent(
            uiState = PaymentIntegrationScreenState.DEFAULT,
            onPriceChanged = {},
            onPaymentButtonClicked = {},
            onSaveSetupButtonClicked = {}
        )
    }
}

private const val TAG = "PaymentIntegrationScreen"