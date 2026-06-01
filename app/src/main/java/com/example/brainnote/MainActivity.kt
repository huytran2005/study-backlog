package com.example.brainnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.brainnote.feature.splash.SplashScreen
import com.example.brainnote.ui.theme.BrainNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrainNoteTheme {
                SplashScreen()
            }
        }
    }
}