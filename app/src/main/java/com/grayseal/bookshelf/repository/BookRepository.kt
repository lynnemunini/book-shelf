package com.grayseal.bookshelf.repository

import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.ResourceConverter
import com.grayseal.bookshelf.network.BooksAPI
import javax.inject.Inject

/**
A class that represents a repository for retrieving books from a remote API.
 * @param api the [BooksAPI] object used to interact with the remote API.
 */
class BookRepository @Inject constructor(private val api: BooksAPI) {
    /**
    A [DataOrException] object that holds a list of books, a loading flag, and an exception, if any.
     */
    private val dataOrException = DataOrException<List<Book>, Boolean, Exception>()

    /**
    A suspend function that retrieves a list of books that match a search query from the remote API.
     * @param searchQuery the search query to use for finding matching books.
     * @return a [DataOrException] object containing a list of books, a loading flag, and an exception, if any.
     */
    suspend fun getBooks(searchQuery: String): DataOrException<List<Book>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            val bookResource = api.getAllBooks(searchQuery)
            dataOrException.data = ResourceConverter().toBookList(bookResource)
            if (dataOrException.data!!.isNotEmpty()) dataOrException.loading = false
        } catch (e: Exception) {
            dataOrException.e = e
        }
        return dataOrException
    }

    /**
    A [DataOrException] object that holds a book, a loading flag, and an exception, if any.
     */
    private val bookInfoDataOrException = DataOrException<Book, Boolean, Exception>()

    /**
    A suspend function that retrieves information for a specific book from the remote API.
     * @param bookId the ID of the book to retrieve information for.
     * @return a [DataOrException] object containing information for a book, a loading flag, and an exception, if any.
     */
    suspend fun getBookInfo(bookId: String): DataOrException<Book, Boolean, Exception> {
        val response = try {
            bookInfoDataOrException.loading = true

            val book = api.getBookInfo(bookId)
            bookInfoDataOrException.data = ResourceConverter().toBook(book)
            if (bookInfoDataOrException.data.toString()
                    .isNotEmpty()
            ) bookInfoDataOrException.loading = false
            else {
            }
        } catch (e: Exception) {
            bookInfoDataOrException.e = e
        }
        return bookInfoDataOrException
    }
}