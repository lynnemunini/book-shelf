package com.grayseal.bookshelf.screens.home

import androidx.activity.result.ActivityResult
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.screens.login.LoginScreenViewModel
import com.grayseal.bookshelf.utils.rememberFirebaseAuthLauncher

@Composable
fun HomeScreen(navController: NavHostController, viewModel: LoginScreenViewModel = hiltViewModel()) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            user = result.user
            Log.e("TAG", "HomeScreen: $user", )
        },
        onAuthError = {
            user = null
        }
    )
    if (user == null){
        LoginScreen(navController, launcher, viewModel)
    }
    else{
        // Main Screen Content
        Text("Welcome ${user!!.displayName}")
        Button(onClick = {
            // TODO sign out
            // Firebase.auth.signOut()
            }) {
            Text("Sign Out")
        }
    }
}