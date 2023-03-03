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
    // Add a no-argument constructor
    constructor() : this(
        "",
        "",
        "",
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf()
    )
}


/**
 * Review class represents a book review.
 *
 * @property book the book being reviewed
 * @property rating the rating given to the book (0-5)
 * @property reviewText the text of the review
 */
data class Review(
    val book: Book,
    val rating: Double,
    val reviewText: String
) {
    // Add a no-argument constructor
    constructor() : this(Book(), 0.0, "")
}

/**
 * @property name the name of the shelf
 * @property books a list of books in the shelf
 */
data class Shelf(
    val name: String,
    var books: List<Book>
) {
    // No-argument constructor required for Firestore deserialization
    constructor() : this("", emptyList())
}