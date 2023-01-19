package com.grayseal.bookshelf.navigation

enum class BookShelfScreens {
    SplashScreen,
    HomeScreen,
    SearchScreen,
    ShelfScreen,
    BookScreen;

    companion object {
        fun fromRoute(route: String): BookShelfScreens
        = when(route.substringBefore("/")){
            SplashScreen.name -> SplashScreen
            SearchScreen.name -> SearchScreen
            HomeScreen.name -> HomeScreen
            ShelfScreen.name -> ShelfScreen
            BookScreen.name -> BookScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}