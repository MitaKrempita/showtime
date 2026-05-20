package com.example.showtime.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import okio.Path.Companion.toPath

fun createPreferencesDataStore(producePath: ()-> String) : DataStore<Preferences>
{
    return PreferenceDataStoreFactory.createWithPath (
        produceFile = {producePath().toPath()}
    )
}
val TOKEN_KEY = stringPreferencesKey("token")
val EXPIRATION_VALUE = longPreferencesKey("expr") //treba expires_in + Instant/Date  da ima validno poredilo

suspend fun getToken(dataStore: DataStore<Preferences>): String? {
    return dataStore.data.first()[TOKEN_KEY]
}
suspend fun getExprValue(dataStore: DataStore<Preferences>) : Long?
{
    return dataStore.data.first()[EXPIRATION_VALUE]
}
suspend fun setExprValue(dataStore: DataStore<Preferences>,value : Long) // za sad expr i token ne vracaju nista, mogli bi nesto infomativno
{
    System.out.println(value)
    dataStore.edit {
        preferences ->
        preferences[EXPIRATION_VALUE] = (value)
    }
    System.out.println(getExprValue(dataStore))
}
suspend fun setToken(dataStore : DataStore<Preferences>, value : String)
{
    dataStore.edit { preferences ->
        preferences[TOKEN_KEY] = (value)
    }
    System.out.println(getToken(dataStore))
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"