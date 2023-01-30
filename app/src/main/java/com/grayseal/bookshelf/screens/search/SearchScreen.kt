package com.grayseal.bookshelf.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.grayseal.bookshelf.components.SearchInputField
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun SearchScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Search(navController = navController)
        Spacer(modifier = Modifier.height(20.dp))
        Recents()

    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Search(navController: NavController, onSearch: (String) -> Unit = {}) {
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
                .clickable(enabled = true, onClick = {
                    navController.navigate(route = BookShelfScreens.HomeScreen.name)
                })
        )
        Text(
            "Search", fontFamily = poppinsFamily,
        )

    }
    Spacer(modifier = Modifier.height(30.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        SearchInputField(
            valueState = searchState,
            labelId = "Find a book 🧐",
            enabled = true,
            isSingleLine = false,
            onAction = KeyboardActions{
                if (!valid) return@KeyboardActions
                onSearch(searchState.value.trim())
                searchState.value = ""
                keyboardController?.hide()
            }
        )
    }
}

@Composable
fun Recents(){
    Column() {
        Text(
            "Recent Searches", fontFamily = poppinsFamily,
        )
    }
}