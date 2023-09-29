package com.grayseal.bookshelf.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.screens.search.Results
import com.grayseal.bookshelf.screens.search.SearchBookViewModel
import com.grayseal.bookshelf.ui.theme.Gray200
import com.grayseal.bookshelf.ui.theme.poppinsFamily

/**
Composable function to display books by category.
* @param navController The NavController used for navigation.
* @param viewModel The [SearchBookViewModel] used to retrieve books by category.
* @param category The category of books to be displayed.
*/
@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: SearchBookViewModel,
    category: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
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
                        navController.navigate(route = BookShelfScreens.HomeScreen.name)
                    })
            )
            Text(
                category.toString(),
                fontFamily = poppinsFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

        }
        if (category != null) {
            viewModel.loading.value = true
            viewModel.searchBooks(category)
        }
        Divider(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), color = Gray200)
        Results(viewModel = viewModel, navController = navController)
    }
}