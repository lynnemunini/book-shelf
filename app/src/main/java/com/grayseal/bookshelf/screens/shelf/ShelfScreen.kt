package com.grayseal.bookshelf.screens.shelf

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.MyUser
import com.grayseal.bookshelf.model.Shelf
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun ShelfScreen(navController: NavController) {
    val userId = Firebase.auth.currentUser?.uid
    var shelves: List<Shelf> by remember {
        mutableStateOf(mutableListOf())
    }
    var loading by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    // Get shelves from firestore
    if (userId != null) {
        val db = FirebaseFirestore.getInstance().collection("users").document(userId)
        db.get().addOnSuccessListener { documentSnapShot ->
            val userShelves = documentSnapShot.toObject<MyUser>()?.shelves
            if (userShelves != null){
                shelves = userShelves
            }else{
                Toast.makeText(context, "Error fetching Shelves", Toast.LENGTH_SHORT)
                    .show()
            }
            loading = false
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
            Text(
                "Shelves",
                fontFamily = poppinsFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 30.dp)
            )
        }
        if (!loading) {
            if (shelves.isNotEmpty()) {
                BookShelf(navController = navController, shelves = shelves)
            } else {
                Text("No shelves available")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material.LinearProgressIndicator(color = Yellow)
            }
        }
    }
}

@Composable
fun BookShelf(navController: NavController, shelves: List<Shelf>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(items = shelves) { shelf ->
            ShelfItem(
                shelfName = shelf.name,
                shelfBooks = shelf.books,
                total = shelf.books.size,
                onClick = {
                    navController.navigate(route = BookShelfScreens.BooksInShelfScreen.name + "/${shelf.name}")
                }
            )
        }
    }
}

@Composable
fun ShelfItem(
    shelfName: String,
    shelfBooks: List<Book>,
    total: Int,
    onClick: () -> Unit
) {
    val booksTitles = mutableListOf<String>()
    shelfBooks.forEach {
        booksTitles.add(it.title)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(color = Color.Transparent)
            .clickable(onClick = onClick),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coverimage),
                    contentDescription = "Shelf Cover",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                )
            }
            Column {
                Row {
                    androidx.compose.material3.Text(
                        shelfName,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    androidx.compose.material3.Text(
                        "($total)",
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
                Row {
                    androidx.compose.material3.Text(
                        booksTitles.joinToString(separator = ", "),
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}