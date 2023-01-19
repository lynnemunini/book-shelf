package com.grayseal.bookshelf.screens.home

import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.utils.rememberFirebaseAuthLauncher

@Composable
fun HomeScreen(navController: NavHostController) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            user = result.user
        },
        onAuthError = {
            user = null
        }
    )
    if (user == null){
        LoginScreen(launcher)
    }
    else{
        // Main Screen Content
        Text("Welcome ${user!!.displayName}")
        Button(onClick = {
            // TODO sign out
            }) {
            Text("Sign Out")
        }
    }
}