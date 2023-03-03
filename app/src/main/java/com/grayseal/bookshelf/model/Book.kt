package com.grayseal.bookshelf.model

/**
 * Class representing a Book.
 *
 * @author Lynne Munini
 */
data class Book(
    /** The unique identifier of the book */
    var bookID: String,
    /** A list of authors of the book */
    val authors: List<String>,
    /** The average rating of the book */
    val averageRating: Double,
    /** A list of categories the book belongs to */
    val categories: List<String>,
    /** A brief description of the book */
    val description: String,
    /** Image links related to the book */
    val imageLinks: ImageLinks,
    /** The language in which the book is written */
    val language: String,
    /** The number of pages the book has */
    val pageCount: Int,
    /** A list of industry identifiers for the book */
    val industryIdentifiers: List<IndustryIdentifier>,
    /** The date the book was published */
    val publishedDate: String,
    /** The publisher of the book */
    val publisher: String,
    /** The number of ratings the book has received */
    val ratingsCount: Int,
    /** The subtitle of the book */
    val subtitle: String,
    /** The title of the book */
    val title: String,
    /** Information related to a search query for the book */
    val searchInfo: String
) {
    // No-argument constructor required for Firestore deserialization
    constructor() : this(
        "",
        emptyList(),
        0.0,
        emptyList(),
        "",
        ImageLinks("", ""),
        "",
        0,
        emptyList(),
        "",
        "",
        0,
        "",
        "",
        ""
    )
}