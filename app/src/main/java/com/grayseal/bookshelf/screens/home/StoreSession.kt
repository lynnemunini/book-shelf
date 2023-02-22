package com.grayseal.bookshelf.screens.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map


class StoreSession(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "firstLaunch"
        )

        val IS_FIRST_TIME_LAUNCH = booleanPreferencesKey("is_first_time_launch")
    }

    var isFirstTime by mutableStateOf(true)
        private set

    init {
        observeIsFirstTimeLaunch()
    }

    private fun observeIsFirstTimeLaunch() {
        dataStore.data.map { preferences ->
            preferences[IS_FIRST_TIME_LAUNCH] ?: true
        }.asLiveData().observeForever { value ->
            isFirstTime = value
        }
    }

    suspend fun setIsFirstTimeLaunch(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_LAUNCH] = isFirstTime
        }
    }
}