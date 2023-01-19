package com.grayseal.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grayseal.bookshelf.screens.SplashScreen
import com.grayseal.bookshelf.screens.book.BookScreen
import com.grayseal.bookshelf.screens.home.HomeScreen
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.screens.search.SearchScreen
import com.grayseal.bookshelf.screens.shelf.ShelfScreen

@Composable
fun BookShelfNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BookShelfScreens.SplashScreen.name){
        composable(BookShelfScreens.SplashScreen.name){
            SplashScreen(navController = navController)
        }
        composable(BookShelfScreens.HomeScreen.name){
            HomeScreen(navController = navController)
        }
        composable(BookShelfScreens.BookScreen.name){
            BookScreen(navController = navController)
        }
        composable(BookShelfScreens.SearchScreen.name){
            SearchScreen(navController = navController)
        }
        composable(BookShelfScreens.ShelfScreen.name){
            ShelfScreen(navController = navController)
        }
    }
}