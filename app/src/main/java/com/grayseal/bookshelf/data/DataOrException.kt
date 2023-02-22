package com.grayseal.bookshelf.data

/**
 * A generic data class that represents a data item, a loading state, and an exception.
 *
 * This class is used to encapsulate data that may be accompanied by a loading state or an exception. It contains three properties:
 *
 * - `data`: the data item of type T. It may be null.
 * - `loading`: a boolean value that indicates whether the data is currently being loaded. It may be null.
 * - `e`: an exception of type E that represents an error that occurred while loading the data. It may be null.
 *
 * The class is intended to be used as a return type from functions that may encounter errors while loading data. It allows the caller to handle the data, loading state, and exception as a single object, simplifying error handling and reducing boilerplate code.
 */
data class DataOrException<T, Boolean, E: Exception>(
    var data: T? = null,
    var loading: Boolean? = null,
    var e: E? = null
)
