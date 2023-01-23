package com.grayseal.bookshelf.model

data class Book (
    val bookID: Long,
    val title: String,
    val author: String,
    val summary: String,
    val coverImage: String? = null,
    val bookCoverImage: String? = null
)