package com.grayseal.bookshelf.model

data class IndustryIdentifier(
    val identifier: String?,
    val type: String?
){
    // No-argument constructor required for Firestore deserialization
    constructor() : this("", "")
}