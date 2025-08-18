package com.example.stripesnippet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stripesnippet.ui.screen.PaymentIntegrationScreen
import com.example.stripesnippet.ui.theme.StripeSnippetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StripeSnippetTheme {
                PaymentIntegrationScreen()
            }
        }
    }
}