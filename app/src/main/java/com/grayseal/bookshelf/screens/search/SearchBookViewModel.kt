package com.grayseal.bookshelf.screens.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
View model responsible for handling book search functionality and displaying the search results.
 * @property repository the repository that provides access to book data
 * @property resultsState the current state of the search results
 * @property listOfBooks the list of books matching the search query
 * @property loading the loading state of the search process
 */
@HiltViewModel
class SearchBookViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {
    var resultsState: MutableState<DataOrException<List<Book>, Boolean, Exception>> =
        mutableStateOf(DataOrException(listOf(), false, Exception("")))

    var listOfBooks: MutableState<List<Book>> = mutableStateOf(listOf())

    var loading: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Launches a coroutine to search for books based on the provided query.
     * @param query the query to search for
     */
    fun searchBooks(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                return@launch
            }
            // Clear items from the previous search
            listOfBooks.value = listOf()
            resultsState.value = repository.getBooks(query)

            if (resultsState.value.data != null) {
                listOfBooks.value = resultsState.value.data!!
            }
        }
    }
}