package com.grayseal.bookshelf.screens.home

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.screens.login.LoginScreen

@Composable
fun HomeScreen(navController: NavHostController) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    if (user == null){
        LoginScreen()
    }
    else{
        // Main Screen Content
    }
}