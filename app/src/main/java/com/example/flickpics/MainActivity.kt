package com.example.flickpics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.example.flickpics.ui.FlickrHomeScreen
import com.example.flickrapp.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = (application as App).photoViewModel

        enableEdgeToEdge()
        setContent {
            Box(Modifier.safeDrawingPadding()) {
                FlickrHomeScreen(viewModel)
            }
        }
    }
}
