package com.grayseal.bookshelf.screens.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.components.NavBar
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.Review
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.screens.shelf.ShelfViewModel
import com.grayseal.bookshelf.ui.theme.Pink500
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavController,
    shelfViewModel: ShelfViewModel
) {
    val user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val userId = user?.uid
    var reviewsLoading by remember {
        mutableStateOf(true)
    }
    var booksReviewed: List<Review> by remember {
        mutableStateOf(emptyList())
    }
    booksReviewed = shelfViewModel.fetchReviews(userId) {
        reviewsLoading = false
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
                    Text(
                        "Reviews",
                        fontFamily = poppinsFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Your Reviews: A curated collection of all the books you have reviewed or rated.",
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Reviews(
                navController = navController,
                userId = userId,
                reviewsLoading = reviewsLoading,
                reviews = booksReviewed,
                shelfViewModel = shelfViewModel,
                onReviewDeleted = { /*TODO*/ }) {

            }
        }
    },
        bottomBar = {
            NavBar(navController = navController)
        })
}

@Composable
fun Reviews(
    navController: NavController,
    userId: String?,
    reviewsLoading: Boolean,
    reviews: List<Review>,
    shelfViewModel: ShelfViewModel,
    onReviewDeleted: () -> Unit,
    onReviewEdited: () -> Unit
) {
    if (reviewsLoading) {
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
        if (reviews.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(items = reviews) { item ->
                    item.book.imageLinks.thumbnail?.let {
                        ReviewCard(
                            shelfViewModel = shelfViewModel,
                            userId = userId,
                            book = item.book,
                            bookTitle = item.book.title,
                            bookAuthor = item.book.authors.joinToString(separator = ", "),
                            rating = item.rating.toString(),
                            review = item.reviewText,
                            imageUrl = it.replace("http", "https"),
                            onReviewDeleted = onReviewDeleted,
                            onReviewEdited = onReviewEdited
                        ) {
                            navController.navigate(route = BookShelfScreens.BookScreen.name + "/${item.book.bookID}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    shelfViewModel: ShelfViewModel,
    userId: String?,
    book: Book,
    bookTitle: String,
    bookAuthor: String,
    rating: String,
    review: String,
    imageUrl: String,
    onReviewDeleted: () -> Unit,
    onReviewEdited: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isDeleting by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        color = Color(0xFFfbf2f0)
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
                            Row {
                                for (i in 0 until rating.toFloat().toInt()) {
                                    androidx.compose.material.Icon(
                                        Icons.Rounded.Star,
                                        contentDescription = "star",
                                        tint = Yellow,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                if ((5 - rating.toFloat()) > 0) {
                                    val unrated = 5 - rating.toFloat().toInt()
                                    if ((rating.toFloat() - rating.toFloat().toInt()) > 0) {
                                        androidx.compose.material.Icon(
                                            Icons.Rounded.StarHalf,
                                            contentDescription = "star",
                                            tint = Yellow,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        for (i in 0 until unrated - 1) {
                                            androidx.compose.material.Icon(
                                                Icons.Rounded.Star,
                                                contentDescription = "star",
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    } else {
                                        for (i in 0 until unrated) {
                                            androidx.compose.material.Icon(
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
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            review,
                            fontFamily = poppinsFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                                    .clickable(onClick = onClick),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material.Icon(
                                    Icons.Rounded.Edit,
                                    contentDescription = "Edit",
                                    tint = Pink500,
                                    modifier = Modifier.size(20.dp)
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
                                androidx.compose.material.Icon(
                                    Icons.Rounded.Delete,
                                    contentDescription = "Delete",
                                    tint = Pink500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}