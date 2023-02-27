package com.grayseal.bookshelf.screens.shelf

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.MyUser
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun BooksInShelfScreen(navController: NavController, shelfName: String?) {
    /*Get books in this shelf*/
    val user by remember { mutableStateOf(Firebase.auth.currentUser) }
    var booksInShelf by remember {
        mutableStateOf(mutableListOf<Book>())
    }
    val context = LocalContext.current
    val userId = user?.uid

    var loading by remember {
        mutableStateOf(true)
    }
    if (userId != null) {
        val db = FirebaseFirestore.getInstance().collection("users").document(userId)
        db.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val shelves = documentSnapshot.toObject<MyUser>()?.shelves
                if (shelves != null) {
                    val shelf = shelves.find { it.name == shelfName }
                    if (shelf != null) {
                        val books = shelf.books as MutableList<Book>
                        booksInShelf = books

                    } else {
                        Toast.makeText(context, "Error fetching reading List", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(context, "Error fetching reading List", Toast.LENGTH_SHORT).show()
            }
            loading = false
        }.addOnFailureListener {
            Toast.makeText(context, "Error fetching reading List", Toast.LENGTH_SHORT).show()
        }
    }
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
            androidx.compose.material.Text(
                shelfName.toString(),
                fontFamily = poppinsFamily,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 30.dp)
            )
        }
        BooksInShelfItems(
            booksInShelf = booksInShelf,
            navController = navController,
            loading = loading
        )
    }
}

@Composable
fun BooksInShelfItems(booksInShelf: List<Book>, navController: NavController, loading: Boolean) {
    if (loading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(color = Yellow)
        }
    } else {
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(items = booksInShelf) { item ->
                var title = "Title information unavailable"
                var author = "Author names not on record"
                var previewText = "Preview information not provided"
                var imageUrl =
                    "https://media.istockphoto.com/id/1147544807/vector/thumbnail-image-vector-graphic.jpg?s=612x612&w=0&k=20&c=rnCKVbdxqkjlcs3xH87-9gocETqpspHFXu5dIGB4wuM="
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
                if (item.searchInfo.isNotEmpty()) {
                    val cleanDescription =
                        HtmlCompat.fromHtml(item.searchInfo, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    previewText = cleanDescription.toString()
                }
                val bookId = item.bookID
                BookCard(
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

@Composable
fun BookCard(
    bookTitle: String,
    bookAuthor: String,
    previewText: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .build(),
                        contentDescription = "Book Image",
                        contentScale = ContentScale.FillHeight
                    )
                    Column {
                        Text(
                            bookTitle,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = poppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            bookAuthor,
                            overflow = TextOverflow.Clip,
                            fontFamily = poppinsFamily,
                            fontSize = 12.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        )
                        Text(
                            previewText,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = poppinsFamily,
                            fontSize = 13.sp,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material.Icon(
                        Icons.Rounded.Comment,
                        contentDescription = "Review",
                        tint = Yellow,
                        modifier = Modifier.size(20.dp)
                    )

                    androidx.compose.material.Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = "Favourite",
                        tint = Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    androidx.compose.material.Icon(
                        Icons.Rounded.DeleteForever,
                        contentDescription = "Remove",
                        tint = Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}