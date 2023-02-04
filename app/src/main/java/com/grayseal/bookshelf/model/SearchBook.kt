package com.grayseal.bookshelf.model

data class SearchBook(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)