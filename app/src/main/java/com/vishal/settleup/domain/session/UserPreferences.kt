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
        private val GROUP_ID = stringPreferencesKey("group_id") // ✅ NEW
    }

    val userIdFlow: Flow<String?> =
        context.dataStore.data.map { it[USER_ID] }

    val userNameFlow: Flow<String?> =
        context.dataStore.data.map { it[USER_NAME] }

    val groupIdFlow: Flow<String?> =                      // ✅ NEW
        context.dataStore.data.map { it[GROUP_ID] }

    suspend fun saveUser(userId: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USER_NAME] = name
        }
    }

    suspend fun saveGroupId(groupId: String) {            // ✅ NEW
        context.dataStore.edit { prefs ->
            prefs[GROUP_ID] = groupId
        }
    }

    suspend fun clearGroup() {                             // ✅ OPTIONAL but useful
        context.dataStore.edit { prefs ->
            prefs.remove(GROUP_ID)
        }
    }
}
