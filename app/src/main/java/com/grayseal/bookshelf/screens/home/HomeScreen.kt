package com.grayseal.bookshelf.screens.home

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.screens.login.LoginScreenViewModel
import com.grayseal.bookshelf.screens.login.StoreUserName
import com.grayseal.bookshelf.utils.rememberFirebaseAuthLauncher

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                user = result.user
                Log.e("TAG", "HomeScreen: $user")
            },
            onAuthError = {
                user = null
            }
        )
    val context = LocalContext.current
    if (user == null) {
        LoginScreen(navController, launcher, viewModel)
    } else {
        val dataStore = StoreUserName(context)
        // Main Screen Content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val name = dataStore.getName.collectAsState(initial = "")
            Text("Welcome ${name.value}")
            Button(onClick = {
                // TODO sign out
                Firebase.auth.signOut()
                navController.navigate(BookShelfScreens.HomeScreen.name)
            }) {
                Text("Sign Out")
            }
        }
    }
}