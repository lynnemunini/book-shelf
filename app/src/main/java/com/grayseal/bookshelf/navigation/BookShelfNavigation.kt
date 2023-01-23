package com.grayseal.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grayseal.bookshelf.screens.SplashScreen
import com.grayseal.bookshelf.screens.book.BookScreen
import com.grayseal.bookshelf.screens.home.HomeScreen
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.screens.login.StoreUserName
import com.grayseal.bookshelf.screens.search.SearchScreen
import com.grayseal.bookshelf.screens.shelf.ShelfScreen

/**

Composable function to handle navigation between screens in the BookShelf app.
This function uses the rememberNavController() function to create a NavController and
NavHost to handle navigation between different screens using the provided startDestination
*/
@Composable
fun BookShelfNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BookShelfScreens.SplashScreen.name){
        composable(BookShelfScreens.SplashScreen.name){
            SplashScreen(navController = navController)
        }
        composable(BookShelfScreens.HomeScreen.name,){
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