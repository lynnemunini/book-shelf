package com.grayseal.bookshelf.network

import com.grayseal.bookshelf.model.BooksResource
import com.grayseal.bookshelf.model.Item
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

/**
An interface that represents an API for retrieving books.
This interface uses Retrofit to interact with a remote API that provides information on books.
This interface is annotated with the @Singleton annotation to indicate that a single instance of the object will be used across the entire application.
 */
@Singleton
interface BooksAPI {

    /**
    A suspend function that retrieves a list of books from the remote API that match a search query.
    * @param query the search query to use for finding matching books.
    * @return a [BooksResource] object containing information for the books.
     */
    @GET("volumes")
    suspend fun getAllBooks(@Query("q") query: String): BooksResource

    /**
    A suspend function that retrieves information for a specific book from the remote API.
    * @param bookId the ID of the book to retrieve information for.
    * @return an [Item] object containing information for the book.
     */
    @GET("volumes/{bookId}")
    suspend fun getBookInfo(@Path("bookId") bookId: String): Item
}