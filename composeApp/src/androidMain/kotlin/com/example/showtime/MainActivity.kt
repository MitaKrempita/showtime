package com.example.showtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat.enableEdgeToEdge
import com.example.showtime.di.dataModule
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        startKoin {
            modules(dataModule)
        }
        setContent {
            App( prefs = remember{ createDataStore(applicationContext)})  //mora se singletonira
        }
    }
}
