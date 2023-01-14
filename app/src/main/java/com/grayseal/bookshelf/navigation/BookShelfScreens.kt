package com.grayseal.bookshelf.navigation

enum class BookShelfScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    HomeScreen,
    SearchScreen,
    ShelfScreen,
    BookScreen;

    companion object {
        fun fromRoute(route: String): BookShelfScreens
        = when(route.substringBefore("/")){
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            SearchScreen.name -> SearchScreen
            HomeScreen.name -> HomeScreen
            ShelfScreen.name -> ShelfScreen
            BookScreen.name -> BookScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}