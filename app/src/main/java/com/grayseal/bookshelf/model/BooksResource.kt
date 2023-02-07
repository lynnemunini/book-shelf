package com.grayseal.bookshelf.model

data class BooksResource(
    val items: List<Item?>?,
    val kind: String?,
    val totalItems: Int?
)