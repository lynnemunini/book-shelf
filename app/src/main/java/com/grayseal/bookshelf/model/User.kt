package com.grayseal.bookshelf.model

data class MyUser(
    val displayName: String,
    val userID: String,
    val avatar: String,
    val shelves: List<Shelf>,
    val searchHistory: List<String>,
    val reviews: List<Review>,
    val favourites: List<Book>
) {
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "userID" to this.userID,
            "displayName" to this.displayName,
            "avatar" to this.avatar,
            "shelves" to this.shelves,
            "reviews" to this.reviews,
            "favourites" to this.favourites,
            "searchHistory" to this.searchHistory
        )
    }
}

data class Review (
    val book: Book,
    val rating: Double,
    val reviewText: String
)

data class Shelf (
    val name: String,
    val books: List<Book>
)