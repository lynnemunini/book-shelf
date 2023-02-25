package com.grayseal.bookshelf.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Patterns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**

A composable function that returns a [ManagedActivityResultLauncher] to launch the Firebase authentication flow
with Google Sign-In provider. This function handles the result of the authentication flow and calls the
[onAuthComplete] callback if the authentication is successful, or [onAuthError] if an error occurs.
 * @param onAuthComplete a lambda function that will be called with the [AuthResult] if the authentication is successful.
 * @param onAuthError a lambda function that will be called with the [ApiException] if an error occurs during the authentication flow.
 * @return a [ManagedActivityResultLauncher] instance that can be used to launch the Firebase authentication flow with Google Sign-In provider.
 */

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

/**

Checks if the given email address is valid.
 * @param email the email address to validate
 * @return true if the email address is valid, false otherwise.
 */
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
Converts the given [data] to a mutable list of strings.
 * @param data the data to convert
 * @return a mutable list of strings or null if [data] is not of type MutableList<String>
 */
fun convertToMutableList(data: Any?): MutableList<String>? {
    return if (data is MutableList<*>) {
        data as MutableList<String>
    } else {
        null
    }
}

// Function to calculate the average color of a bitmap
fun calculateAverageColor(bitmap: Bitmap): Int {
    var red = 0
    var green = 0
    var blue = 0
    var pixelCount = 0

    for (x in 0 until bitmap.width) {
        for (y in 0 until bitmap.height) {
            val pixel = bitmap.getPixel(x, y)
            red +=
                Color.red(pixel)
            green += Color.green(pixel)
            blue += Color.blue(pixel)
            pixelCount++
        }
    }

    red /= pixelCount
    green /= pixelCount
    blue /= pixelCount

    return Color.rgb(red, green, blue)
}