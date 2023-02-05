package com.grayseal.bookshelf.screens.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Item
import com.grayseal.bookshelf.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchBookViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {
    var listOfBooks: MutableState<DataOrException<List<Item>, Boolean, Exception>> =
        mutableStateOf(DataOrException(null, true, Exception("")))

    init {
        searchBooks("android")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) {
                return@launch
            }
            listOfBooks.value.loading = true
            listOfBooks.value = repository.getBooks(query)
            if (listOfBooks.value.data.toString().isNotEmpty()) listOfBooks.value.loading = false
            Log.d("SEARCH BOOKS", "searchBooks: ${listOfBooks.value.data.toString()}")
        }

    }

}