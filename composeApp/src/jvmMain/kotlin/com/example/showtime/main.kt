package com.example.showtime

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.showtime.di.dataModule
import com.example.showtime.infrastructure.datastore.DATA_STORE_FILE_NAME
import com.example.showtime.infrastructure.datastore.createPreferencesDataStore
import com.example.showtime.infrastructure.room.AppDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.io.File

fun main() {
    val prefs = createPreferencesDataStore {
        val home = System.getProperty("user.home")
        "$home/$DATA_STORE_FILE_NAME"
    }

    val dbFile = File(System.getProperty("user.home"), "showtime.db")
    val database = Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .build()

    if (org.koin.core.context.GlobalContext.getOrNull() == null) {
        startKoin {
            modules(
                dataModule,
                module {
                    single<DataStore<Preferences>> { prefs }
                    single<AppDatabase> { database }
                }
            )
        }
    }

    application {
        Window(onCloseRequest = ::exitApplication, title = "Showtime") {
            App()
        }
    }
}
