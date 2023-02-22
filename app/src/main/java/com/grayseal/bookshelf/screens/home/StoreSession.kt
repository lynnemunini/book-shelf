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

/**
 * A class to store and manage the first time launch status of the application using DataStore.
 * @param context the context of the application.
 */
class StoreSession(private val context: Context) {
    /**
     * DataStore instance to handle storage and retrieval of application preferences.
     */
    private val dataStore = context.dataStore

    companion object {
        /**
         * Custom preferences dataStore property delegate for DataStore.
         * The name parameter specifies the name of the preferences file for this data store.
         */
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "firstLaunch"
        )

        /**
         * BooleanPreferencesKey representing whether the app is launched for the first time.
         */
        val IS_FIRST_TIME_LAUNCH = booleanPreferencesKey("is_first_time_launch")
    }

    /**
     * A mutable state variable to keep track of the first time launch status.
     */
    var isFirstTime by mutableStateOf(true)
        private set

    init {
        observeIsFirstTimeLaunch()
    }

    /**
     * Observe the isFirstTime variable and update its value when the value changes.
     */
    private fun observeIsFirstTimeLaunch() {
        dataStore.data.map { preferences ->
            preferences[IS_FIRST_TIME_LAUNCH] ?: true
        }.asLiveData().observeForever { value ->
            isFirstTime = value
        }
    }

    /**
     * Suspend function to set the isFirstTime value in the DataStore.
     * @param isFirstTime a boolean value to set the isFirstTime value.
     */
    suspend fun setIsFirstTimeLaunch(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_LAUNCH] = isFirstTime
        }
    }
}