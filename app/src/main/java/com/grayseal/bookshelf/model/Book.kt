package com.grayseal.bookshelf.model

/**
 * Book class represents a book in the book shelf app.
 *
 * @property bookID the unique ID of the book
 * @property title the title of the book
 * @property author the author of the book
 * @property summary a brief summary of the book
 * @property coverImage the URL of the book's cover image
 * @property bookCoverImage the URL of the book's book cover image
 */
data class Book (
    val bookID: Long,
    val title: String,
    val author: String,
    val summary: String,
    val coverImage: String? = null,
    val bookCoverImage: String? = null
)