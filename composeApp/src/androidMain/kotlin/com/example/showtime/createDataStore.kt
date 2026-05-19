package com.example.showtime

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.showtime.infrastructure.datastore.DATA_STORE_FILE_NAME
import com.example.showtime.infrastructure.datastore.createPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take

fun createDataStore(context : Context) : DataStore<Preferences>
{
        return createPreferencesDataStore{
            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
}
