package com.grayseal.bookshelf.screens.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.Item
import com.grayseal.bookshelf.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchBookViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {
    var resultsState: MutableState<DataOrException<List<Book>, Boolean, Exception>> =
        mutableStateOf(DataOrException(listOf(), false, Exception("")))

    var listOfBooks: MutableState<List<Book>> = mutableStateOf(listOf())

    private fun loadBooks(query: String) {
        searchBooks(query)
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                return@launch
            }
            // Clear items from the previous search
            listOfBooks.value = listOf()
            resultsState.value.loading = true
            resultsState.value = repository.getBooks(query)

            listOfBooks.value = resultsState.value.data!!
            if (listOfBooks.value.toString().isNotEmpty()) resultsState.value.loading =
                false
            Log.d("LOADING", "searchBooks: ${resultsState.value.loading}")
            Log.d("RESULTS", "searchBooks: ${listOfBooks.value}")
        }
    }
}