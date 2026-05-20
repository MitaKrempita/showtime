package com.example.showtime

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.di.dataModule
import com.example.showtime.infrastructure.datastore.DATA_STORE_FILE_NAME
import com.example.showtime.infrastructure.datastore.createPreferencesDataStore
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main() {
    val prefs = createPreferencesDataStore {
        val home = System.getProperty("user.home")
        "$home/$DATA_STORE_FILE_NAME"
    }

    if (org.koin.core.context.GlobalContext.getOrNull() == null) {
        startKoin {
            modules(
                dataModule,
                module {
                    single<DataStore<Preferences>> { prefs }
                }
            )
        }
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Showtime",
        ) {
            App()
        }
    }
}