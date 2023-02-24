package com.grayseal.bookshelf.model

data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
){
    // No-argument constructor required for Firestore deserialization
    constructor() : this("", "")
}