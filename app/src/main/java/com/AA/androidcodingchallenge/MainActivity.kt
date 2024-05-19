package com.AA.androidcodingchallenge

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.AA.androidcodingchallenge.Utils.ImageViewModel
import com.AA.androidcodingchallenge.Utils.Navigation
import com.AA.androidcodingchallenge.ui.theme.AndroidCodingChallengeTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel by viewModels<ImageViewModel>() // viewModel initialization

            AndroidCodingChallengeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    Navigation(viewModel = viewModel)
                }
            }
        }
    }
}