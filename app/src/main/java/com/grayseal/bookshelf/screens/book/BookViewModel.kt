package com.grayseal.bookshelf.screens.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.MyUser
import com.grayseal.bookshelf.model.Shelf
import com.grayseal.bookshelf.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
ViewModel class that provides data related to a specific book.
 * This class uses Hilt for dependency injection and relies on the [BookRepository] to provide book data.
 * @param repository the repository responsible for providing book data
 */
@HiltViewModel
class BookViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
    // A mutableLiveData to keep track of the book that is being moved
    private val _bookToMove = MutableLiveData<Book?>()
    val bookToMove: MutableLiveData<Book?> = _bookToMove

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

    // suspend function to move the book
    suspend fun moveBookToShelf(userId: String, shelfName: String, otherShelfName: String) {
        val db = FirebaseFirestore.getInstance().collection("users").document(userId)
        val documentSnapshot = db.get().await()
        if (documentSnapshot.exists()) {
            val shelves = documentSnapshot.toObject<MyUser>()?.shelves as MutableList<Shelf>
            val otherShelf: Shelf? = shelves.find { it.name == otherShelfName }
            val currentShelf: Shelf? = shelves.find { it.name == shelfName }
            if (otherShelf != null && currentShelf != null) {
                val otherBooks: MutableList<Book> = otherShelf.books as MutableList<Book>
                otherBooks.removeIf { it.bookID == bookToMove.value?.bookID }
                otherShelf.books = otherBooks
                val index1 = shelves.indexOfFirst { it.name == otherShelfName }
                shelves[index1] = otherShelf

                val currentBooks: MutableList<Book> = currentShelf.books as MutableList<Book>
                if (!currentBooks.any { it.bookID == bookToMove.value?.bookID }) {
                    bookToMove.value?.let { currentBooks.add(it) }
                }
                currentShelf.books = currentBooks
                val index2 = shelves.indexOfFirst { it.name == shelfName }
                shelves[index2] = currentShelf
                db.update("shelves", shelves).await()
            }
        }
    }

    // set the book that is being moved
    fun setBookToMove(book: Book) {
        _bookToMove.value = book
    }

    // clear the book that is being moved
    fun clearBookToMove() {
        _bookToMove.value = null
    }
}