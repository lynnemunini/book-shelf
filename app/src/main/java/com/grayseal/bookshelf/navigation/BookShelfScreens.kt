package com.grayseal.bookshelf.navigation

/**

An enumeration that represents the different screens of the BookShelf app.
This enumeration defines the different screens of the BookShelf app and provides a utility function to convert a route string to the corresponding screen.
 */
enum class BookShelfScreens {
    SplashScreen,
    HomeScreen,
    SearchScreen,
    ShelfScreen,
    BookScreen,
    CategoryScreen,
    FavouriteScreen,
    ReviewScreen;

    /**
    A companion object that provides a utility function to convert a route string to the corresponding screen.
    * @param route the route string to convert to a screen.
    * @return the corresponding [BookShelfScreens] value.
    * @throws IllegalArgumentException if the specified route string is not recognized.
     */
    companion object {
        fun fromRoute(route: String): BookShelfScreens = when (route.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            SearchScreen.name -> SearchScreen
            HomeScreen.name -> HomeScreen
            ShelfScreen.name -> ShelfScreen
            BookScreen.name -> BookScreen
            FavouriteScreen.name -> FavouriteScreen
            ReviewScreen.name -> ReviewScreen
            CategoryScreen.name -> CategoryScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}