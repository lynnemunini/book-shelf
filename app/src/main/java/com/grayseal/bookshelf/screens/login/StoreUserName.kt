package com.grayseal.bookshelf.screens.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
Class to store and retrieve a user's name using DataStore.

 * @param context The context of the application.
 */
class StoreUserName(private val context: Context) {
    // to make sure there's only one instance
    companion object {
        /**
         * DataStore for storing the user's name.
         */
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userName")

        /**
        * Key for storing and retrieving the user's name in the DataStore.
        */
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    /**
    Flow to get the saved name from the DataStore. If no name is saved, "Gray seal" is returned.
     */
    val getName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: "Gray seal"
    }

    /**
    Function to save a name to the DataStore.
    @param name The name to save.
     */
    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }
}