package com.grayseal.bookshelf.model

/**
 * MyUser class represents a user of the book shelf app.
 *
 * @property displayName the user's display name
 * @property userID the user's unique ID
 * @property avatar the user's avatar URL
 * @property shelves a list of shelves created by the user
 * @property searchHistory a list of book IDs representing the user's search history
 * @property reviews a list of reviews written by the user
 * @property favourites a list of book IDs representing the user's favourite books
 */
data class MyUser(
    val displayName: String,
    val userID: String,
    val avatar: String,
    val shelves: List<Shelf>,
    val searchHistory: List<String>,
    val reviews: List<Review>,
    val favourites: List<Book>
) {
    /**
     * Convert the MyUser object to a Map object.
     *
     * @return a mutable map containing the user's information
     */
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


/**
 * Review class represents a book review.
 *
 * @property book the book being reviewed
 * @property rating the rating given to the book (0-5)
 * @property reviewText the text of the review
 */
data class Review (
    val book: Book,
    val rating: Double,
    val reviewText: String
)

/**
* @property name the name of the shelf
* @property books a list of books in the shelf
*/
data class Shelf (
    val name: String,
    val books: List<Book>
)