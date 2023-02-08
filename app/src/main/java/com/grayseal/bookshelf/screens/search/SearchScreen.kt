package com.grayseal.bookshelf.screens.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.grayseal.bookshelf.components.SearchCard
import com.grayseal.bookshelf.components.SearchInputField
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchBookViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Search(navController = navController, viewModel = viewModel) { query ->
            viewModel.searchBooks(query)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Results(viewModel = viewModel)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Search(
    navController: NavController,
    viewModel: SearchBookViewModel,
    onSearch: (String) -> Unit = {}
) {
    val searchState = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(searchState.value) {
        searchState.value.trim().isNotEmpty()
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Search Icon",
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .clickable(enabled = true, onClick = {
                    navController.navigate(route = BookShelfScreens.HomeScreen.name)
                })
        )
        Text(
            "Search", fontFamily = poppinsFamily, color = MaterialTheme.colorScheme.onBackground
        )

    }
    Spacer(modifier = Modifier.height(30.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        SearchInputField(
            valueState = searchState,
            labelId = "Find a book 🧐",
            enabled = true,
            isSingleLine = false,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchState.value.trim())
                Log.d("Search Item", "SEARCH SCREEN: ${searchState.value}")
                searchState.value = ""
                keyboardController?.hide()
            }
        )
    }
    Column {
        Text(
            "Recent Searches",
            fontFamily = poppinsFamily,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun Results(viewModel: SearchBookViewModel) {
    val searchResults = viewModel.resultsState.value
    val listOfBooks = viewModel.listOfBooks.value

    if (searchResults.loading == true) {
        CircularProgressIndicator()
    }
    if (searchResults.loading == false && searchResults.e == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(items = listOfBooks) {item ->
                var title = ""
                var author = ""
                var image = ""
                if (item.imageLinks.toString().isNotEmpty() && item.title.isNotEmpty() && item.authors[0].isNotEmpty()) {
                    title = item.title
                    author = item.authors.toString()
                    image = item.imageLinks.smallThumbnail.toString().trim()
                }
                Log.d("MY IMAGE", "Results: $image")
                SearchCard(
                    bookTitle = title,
                    bookAuthor = author,
                    imageUrl = "http://books.google.com/books/content?id=ex-tDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api"
                )
            }
        }
    }
}