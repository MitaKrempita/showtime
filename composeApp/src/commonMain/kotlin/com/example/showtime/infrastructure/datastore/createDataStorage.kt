package com.example.showtime.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.showtime.domain.model.User
import kotlinx.coroutines.flow.first
import okio.Path.Companion.toPath
import java.time.Instant

fun createPreferencesDataStore(producePath: ()-> String) : DataStore<Preferences>
{
    return PreferenceDataStoreFactory.createWithPath (
        produceFile = {producePath().toPath()}
    )
}
val TOKEN_KEY = stringPreferencesKey("token")
val EXPIRATION_VALUE = longPreferencesKey("expr")
val USER_ID_KEY = longPreferencesKey("user_id")
val USERNAME_KEY = stringPreferencesKey("username")
val FULL_NAME_KEY = stringPreferencesKey("full_name")

suspend fun getToken(dataStore: DataStore<Preferences>): String? {
    return dataStore.data.first()[TOKEN_KEY]
}
suspend fun getValidToken(dataStore: DataStore<Preferences>): String? {
    val preferences = dataStore.data.first()
    val token = preferences[TOKEN_KEY]
    val expr = preferences[EXPIRATION_VALUE]
    if (token == null || expr == null || Instant.now().toEpochMilli() >= expr) {
        clearAuth(dataStore)
        return null
    }
    return token
}
suspend fun getExprValue(dataStore: DataStore<Preferences>) : Long?
{
    return dataStore.data.first()[EXPIRATION_VALUE]
}
suspend fun setExprValue(dataStore: DataStore<Preferences>,value : Long)
{
    dataStore.edit {
        preferences ->
        preferences[EXPIRATION_VALUE] = (value * 1000) + Instant.now().toEpochMilli()
    }
}
suspend fun setToken(dataStore : DataStore<Preferences>, value : String)
{
    dataStore.edit { preferences ->
        preferences[TOKEN_KEY] = (value)
    }
}
suspend fun setCachedUser(dataStore: DataStore<Preferences>, user: User)
{
    dataStore.edit { preferences ->
        preferences[USER_ID_KEY] = user.id
        preferences[USERNAME_KEY] = user.username
        preferences[FULL_NAME_KEY] = user.fullName
    }
}
suspend fun getCachedUser(dataStore: DataStore<Preferences>): User?
{
    val preferences = dataStore.data.first()
    val id = preferences[USER_ID_KEY] ?: return null
    val username = preferences[USERNAME_KEY] ?: return null
    val fullName = preferences[FULL_NAME_KEY] ?: return null
    return User(id, username, fullName)
}
suspend fun clearAuth(dataStore: DataStore<Preferences>)
{
    dataStore.edit { preferences ->
        preferences.remove(TOKEN_KEY)
        preferences.remove(EXPIRATION_VALUE)
        preferences.remove(USER_ID_KEY)
        preferences.remove(USERNAME_KEY)
        preferences.remove(FULL_NAME_KEY)
    }
}
suspend fun checkIfExpired(dataStore : DataStore<Preferences>, value: Instant) : Boolean?
{

    val check = getExprValue(dataStore)?.minus(value.toEpochMilli()) // negativno znaci isteklo
    if(check!=null)
    {
        return if(check<=0)
             true           //true isteklo false nije
        else return false
    }
    else return null
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"
