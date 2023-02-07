package com.grayseal.bookshelf.repository

import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.Item
import com.grayseal.bookshelf.model.ResourceConverter
import com.grayseal.bookshelf.network.BooksAPI
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksAPI) {
    private val dataOrException = DataOrException<List<Book>, Boolean, Exception>()
    suspend fun getBooks(searchQuery: String): DataOrException<List<Book>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            val bookResource = api.getAllBooks(searchQuery)
            dataOrException.data = ResourceConverter().toBook(bookResource)
            if (dataOrException.data!!.isNotEmpty()) dataOrException.loading = false
        } catch (e: Exception) {
            dataOrException.e = e
        }
        return dataOrException
    }

    private val bookInfoDataOrException = DataOrException<Item, Boolean, Exception>()
    suspend fun getBookInfo(bookId: String): DataOrException<Item, Boolean, Exception> {
        val response = try {
            bookInfoDataOrException.loading = true
            bookInfoDataOrException.data = api.getBookInfo(bookId)
            if (bookInfoDataOrException.data.toString()
                    .isNotEmpty()
            ) bookInfoDataOrException.loading = false
            else {}
        } catch (e: java.lang.Exception) {
            bookInfoDataOrException.e = e
        }
        return bookInfoDataOrException
    }
}