package com.grayseal.bookshelf.network

import com.grayseal.bookshelf.model.Item
import com.grayseal.bookshelf.model.BooksResource
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface BooksAPI {

    @GET("volumes")
    suspend fun getAllBooks(@Query("q") query: String): BooksResource

    @GET("volumes/{bookID}")
    suspend fun getBookInfo(@Path("bookId") bookId: String): Item
}