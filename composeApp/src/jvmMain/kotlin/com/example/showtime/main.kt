package com.example.showtime

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.showtime.di.dataModule
import com.example.showtime.infrastructure.datastore.DATA_STORE_FILE_NAME
import com.example.showtime.infrastructure.datastore.createPreferencesDataStore
import org.koin.core.context.startKoin

fun main() {
    val prefs = createPreferencesDataStore{
        DATA_STORE_FILE_NAME  //mpra da se sakrije kasnije
    }
    application {
        startKoin {
            modules(dataModule)
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "Showtime",
        ) {
            App(prefs = prefs)
        }
    }
}