package com.vishal.settleup.domain.session

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val userIdFlow: Flow<String?> =
        context.dataStore.data.map { it[USER_ID] }

    val userNameFlow: Flow<String?> =
        context.dataStore.data.map { it[USER_NAME] }

    suspend fun saveUser(userId: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USER_NAME] = name
        }
    }
}
