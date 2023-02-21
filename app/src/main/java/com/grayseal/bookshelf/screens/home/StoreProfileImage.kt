package com.grayseal.bookshelf.screens.home

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
class StoreProfileImage(private val context: Context) {
    // to make sure there's only one instance
    companion object {
        /*
        * DataStore for storing the user's set profile picture.
        */
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("profileImage")

        /*
        * Key for storing and retrieving the user's set profile picture in the DataStore.
        */
        val USER_IMAGE_KEY = stringPreferencesKey("user_image")
    }

    /**
    Flow to get the saved image from the DataStore. If no image is saved, "Gray seal" is returned.
     */
    val getImage: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_IMAGE_KEY] ?: "Gray seal"
    }

    /**
    Function to save an image to the DataStore.
    @param image The image to save.
     */
    suspend fun saveImage(image: ByteArray) {
        context.dataStore.edit { preferences ->
            // Convert image byteArray to a string before saving it in datastore
            preferences[USER_IMAGE_KEY] = String(image)
        }
    }
}