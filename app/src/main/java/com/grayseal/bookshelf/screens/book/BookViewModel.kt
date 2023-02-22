package com.grayseal.bookshelf.screens.book

import androidx.lifecycle.ViewModel
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
ViewModel class that provides data related to a specific book.
 * This class uses Hilt for dependency injection and relies on the [BookRepository] to provide book data.
 * @param repository the repository responsible for providing book data
 */
@HiltViewModel
class BookViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
    /**
    Retrieves information related to a specific book identified by [bookId].
     * @param bookId the unique identifier of the book to retrieve information for
     * @return [DataOrException] object containing the book data if successful, a boolean indicating if
     * the book is found but has no information associated with it, or an [Exception] if there is
     * an error while retrieving the data
     */
    suspend fun getBookInfo(bookId: String): DataOrException<Book, Boolean, Exception> {
        return repository.getBookInfo(bookId)
    }
}