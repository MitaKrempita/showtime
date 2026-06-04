package com.example.showtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.showtime.di.dataModule
import com.example.showtime.infrastructure.room.AppDatabase
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    private val dataStore by lazy { createDataStore(applicationContext) }

    private val database by lazy {
        Room.databaseBuilder<AppDatabase>(
            context = applicationContext,
            name = applicationContext.getDatabasePath("showtime.db").absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(
                    dataModule,
                    module {
                        single<DataStore<Preferences>> { dataStore }
                        single<AppDatabase> { database }
                    }
                )
            }
        }
        setContent { App() }
    }
}
