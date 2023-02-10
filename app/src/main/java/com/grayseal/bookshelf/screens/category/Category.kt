package com.grayseal.bookshelf.screens.category

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.grayseal.bookshelf.screens.search.Results
import com.grayseal.bookshelf.screens.search.SearchBookViewModel

@Composable
fun CategoryScreen(navController: NavController, viewModel: SearchBookViewModel, category: String?){

    if(category != null){
        viewModel.searchBooks(category)
    }
    Results(viewModel = viewModel)
}