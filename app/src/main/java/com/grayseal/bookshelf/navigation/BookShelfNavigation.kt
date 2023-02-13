package com.grayseal.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grayseal.bookshelf.screens.SplashScreen
import com.grayseal.bookshelf.screens.book.BookScreen
import com.grayseal.bookshelf.screens.book.BookViewModel
import com.grayseal.bookshelf.screens.category.CategoryScreen
import com.grayseal.bookshelf.screens.home.HomeScreen
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.screens.login.StoreUserName
import com.grayseal.bookshelf.screens.search.SearchBookViewModel
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
    val searchViewModel: SearchBookViewModel = hiltViewModel()
    val bookViewModel: BookViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = BookShelfScreens.SplashScreen.name) {
        composable(BookShelfScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(BookShelfScreens.HomeScreen.name,) {
            HomeScreen(navController = navController)
        }

        val bookRoute = BookShelfScreens.BookScreen.name
        composable("$bookRoute/{bookId}", arguments = listOf(navArgument(name = "bookId") {
            type = NavType.StringType
        })) { navBack ->
            navBack.arguments?.getString("bookId").let { bookId ->
                BookScreen(navController = navController, bookViewModel = bookViewModel, bookId = bookId)
            }
        }
        composable(BookShelfScreens.SearchScreen.name) {
            SearchScreen(navController = navController, searchViewModel)
        }
        composable(BookShelfScreens.ShelfScreen.name) {
            ShelfScreen(navController = navController)
        }

        val route = BookShelfScreens.CategoryScreen.name
        composable("$route/{query}", arguments = listOf(navArgument(name = "query") {
            type = NavType.StringType
        })) { navBack ->
            navBack.arguments?.getString("query").let { query ->
                CategoryScreen(navController = navController, searchViewModel, category = query)
            }
        }
    }
}