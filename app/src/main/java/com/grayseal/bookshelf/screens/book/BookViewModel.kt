package com.grayseal.bookshelf.screens.book

import androidx.lifecycle.ViewModel
import com.grayseal.bookshelf.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(private val repository: BookRepository):ViewModel(){
}