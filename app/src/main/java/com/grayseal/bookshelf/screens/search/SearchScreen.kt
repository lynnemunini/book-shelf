package com.grayseal.bookshelf.screens.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.components.HistoryCard
import com.grayseal.bookshelf.components.SearchCard
import com.grayseal.bookshelf.components.SearchInputField
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import com.grayseal.bookshelf.utils.convertToMutableList

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchBookViewModel
) {
    val userId = Firebase.auth.currentUser?.uid

    val previousSearches: MutableList<String> by remember {
        mutableStateOf(mutableListOf())
    }

    var displayPreviousHistory by remember {
        mutableStateOf(false)
    }

    // Get searchHistory from FireStore
    if (userId != null) {
        // Save to searchHistory
        val db = FirebaseFirestore.getInstance().collection("users").document(userId).get()
        db.addOnSuccessListener {
            val data = db.result.get("searchHistory")
            if (data != null) {
                for (i in data as MutableList<String>) {
                    previousSearches.add(i)
                }
                displayPreviousHistory = previousSearches.isNotEmpty()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Search(navController = navController, viewModel = viewModel) { query ->
            viewModel.searchBooks(query)
            // Get user and add searched value to fireStore
            if (userId != null) {
                // Save to searchHistory
                val db = FirebaseFirestore.getInstance().collection("users").document(userId).get()
                db.addOnSuccessListener {
                    val data = db.result.get("searchHistory")
                    val response = convertToMutableList(data)
                    response?.add(query)
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .update("searchHistory", response)
                }
            }
        }
        if (displayPreviousHistory) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Recent Searches",
                    fontFamily = poppinsFamily,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    val lastThree = if (previousSearches.size >= 4) {
                        previousSearches.subList(previousSearches.size - 3, previousSearches.size)
                    } else {
                        previousSearches
                    }
                    val items = lastThree.toSet().toList().asReversed()
                    items.forEach {
                        if (it != "") {
                            HistoryCard(text = it, onClick = {
                                viewModel.searchBooks(it)
                            })
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Results(viewModel = viewModel, navController = navController)
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
            contentDescription = "Close Icon",
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .clickable(enabled = true, onClick = {
                    navController.popBackStack()
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
                keyboardController?.hide()
                // set search value to empty string
                searchState.value = ""
            }
        )
    }
}


@Composable
fun Results(viewModel: SearchBookViewModel, navController: NavController) {
    val searchResults = viewModel.resultsState.value
    val listOfBooks = viewModel.listOfBooks.value

    if (searchResults.loading == true) {
        CircularProgressIndicator(color = Yellow)
    }
    if (searchResults.loading == null) {
        androidx.compose.material3.Text(
            "Error fetching data",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    if (searchResults.loading == false && searchResults.e == null) {
        if (listOfBooks.isEmpty()) {
            androidx.compose.material3.Text(
                "No results found for your search. Please try again with different keywords or try searching again.",
                fontFamily = poppinsFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(items = listOfBooks) { item ->
                var title = "Title information unavailable"
                var author = "Author names not on record"
                var imageUrl =
                    "https://media.istockphoto.com/id/1147544807/vector/thumbnail-image-vector-graphic.jpg?s=612x612&w=0&k=20&c=rnCKVbdxqkjlcs3xH87-9gocETqpspHFXu5dIGB4wuM="
                var previewText = "Preview information not provided"
                if (item.imageLinks.toString().isNotEmpty()) {
                    imageUrl = item.imageLinks.thumbnail.toString().trim()
                    imageUrl = imageUrl.replace("http", "https")
                }
                if (item.title.isNotEmpty()) {
                    title = item.title
                }
                if (item.authors[0].isNotEmpty()) {
                    author = item.authors.joinToString(separator = ", ")
                }
                if (item.description.isNotEmpty()) {
                    val cleanDescription = HtmlCompat.fromHtml(item.searchInfo, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    previewText = item.searchInfo
                }
                val bookId = item.bookID
                Log.d("BOOKID", "$bookId")
                SearchCard(
                    bookTitle = title,
                    bookAuthor = author,
                    previewText = previewText,
                    imageUrl = imageUrl,
                    onClick = {
                        navController.navigate(route = BookShelfScreens.BookScreen.name + "/$bookId")
                    }
                )
            }
        }
    }
}