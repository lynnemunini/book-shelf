package com.grayseal.bookshelf.screens.favourite

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.components.NavBar
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.screens.shelf.ShelfViewModel
import com.grayseal.bookshelf.ui.theme.Pink500
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    navController: NavController,
    shelfViewModel: ShelfViewModel
) {
    val user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val userId = user?.uid
    var favouritesLoading by remember {
        mutableStateOf(true)
    }
    var booksInFavourites: List<Book> by remember {
        mutableStateOf(emptyList())
    }
    booksInFavourites = shelfViewModel.fetchFavourites(userId) {
        favouritesLoading = false
    }
    Scaffold(content = { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Favourites",
                            fontFamily = poppinsFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        "Your Favourites: A curated collection of all the books you love and have" +
                                " added to your personal favourites list.",
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Favourites(
                navController = navController,
                userId = userId,
                favouritesLoading = favouritesLoading,
                favourites = booksInFavourites,
                shelfViewModel = shelfViewModel
            ) {
                shelfViewModel.fetchFavourites(userId) {
                    favouritesLoading = false
                }
            }
        }
    },
        bottomBar = {
            NavBar(navController = navController)
        })
}

@Composable
fun Favourites(
    navController: NavController,
    userId: String?,
    favouritesLoading: Boolean,
    favourites: List<Book>,
    shelfViewModel: ShelfViewModel,
    onFavouritesChanged: () -> Unit
) {
    if (favouritesLoading) {
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
        if (favourites.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(items = favourites) { item ->
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
                    if (item.description.isNotEmpty()) {
                        val cleanDescription =
                            HtmlCompat.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        previewText = cleanDescription.toString()
                    }
                    val bookId = item.bookID
                    FavCard(
                        shelfViewModel = shelfViewModel,
                        userId = userId,
                        book = item,
                        bookTitle = title,
                        rating = item.averageRating.toString(),
                        bookAuthor = author,
                        previewText = previewText,
                        imageUrl = imageUrl,
                        onFavouritesChanged = onFavouritesChanged,
                        onClick = {
                            navController.navigate(route = BookShelfScreens.BookScreen.name + "/$bookId")
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emptyshelf),
                    contentDescription = "Empty Shelf",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Text(
                    "Uh oh, you have no favourites!",
                    fontFamily = poppinsFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Text(
                    "Explore books and add them to favourites to show them here",
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FavCard(
    shelfViewModel: ShelfViewModel,
    userId: String?,
    book: Book,
    bookTitle: String,
    bookAuthor: String,
    rating: String,
    previewText: String,
    imageUrl: String,
    onFavouritesChanged: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isDeleting by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 5.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row {
                                for (i in 0 until rating.toFloat().toInt()) {
                                    Icon(
                                        Icons.Rounded.Star,
                                        contentDescription = "star",
                                        tint = Yellow,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                if ((5 - rating.toFloat()) > 0) {
                                    val unrated = 5 - rating.toFloat().toInt()
                                    if ((rating.toFloat() - rating.toFloat().toInt()) > 0) {
                                        Icon(
                                            Icons.Rounded.StarHalf,
                                            contentDescription = "star",
                                            tint = Yellow,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        for (i in 0 until unrated - 1) {
                                            Icon(
                                                Icons.Rounded.Star,
                                                contentDescription = "star",
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    } else {
                                        for (i in 0 until unrated) {
                                            Icon(
                                                Icons.Rounded.Star,
                                                contentDescription = "star",
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontFamily = poppinsFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = 0.4f
                                                )
                                            )
                                        ) {
                                            append(rating.toFloat().toString())
                                        }
                                    },
                                    modifier = Modifier.padding(start = 5.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.End)
                                    .clickable(onClick = onClick),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Read more",
                                    fontFamily = poppinsFamily,
                                    fontSize = 12.sp,
                                    color = Pink500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    Icons.Rounded.ArrowForward,
                                    contentDescription = "Arrow",
                                    tint = Pink500,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .background(color = Pink500)
                        .clip(RoundedCornerShape(topStart = 20.dp))
                        .align(Alignment.Bottom)
                ) {
                    Icon(
                        Icons.Outlined.Remove,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = {
                                isDeleting = true // set the deletion state to true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val done = shelfViewModel.removeFavourite(userId, book)
                                    if (done) {
                                        withContext(Dispatchers.Main) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Book removed from favourites",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            onFavouritesChanged()
                                        }
                                    }
                                    isDeleting = false // set the deletion state to false
                                }
                            })
                    )
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center),
                            color = Yellow
                        )
                    }
                }
            }
        }
    }
}