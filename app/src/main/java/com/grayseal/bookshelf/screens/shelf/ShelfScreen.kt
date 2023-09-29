package com.grayseal.bookshelf.screens.shelf

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import com.grayseal.bookshelf.model.Review
import com.grayseal.bookshelf.model.Shelf
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Gray500
import com.grayseal.bookshelf.ui.theme.Pink500
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(navController: NavController, shelfViewModel: ShelfViewModel) {
    val userId = Firebase.auth.currentUser?.uid
    var shelves: List<Shelf> by remember {
        mutableStateOf(mutableListOf())
    }
    var loading by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    // Get shelves from firestore
    shelves = shelfViewModel.getShelves(userId, context, onDone = {
        loading = false
    })
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
                Text(
                    "Shelves",
                    fontFamily = poppinsFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            if (!loading) {
                if (shelves.isNotEmpty()) {
                    ShelfBooks(
                        userId = userId,
                        navController = navController,
                        shelfViewModel = shelfViewModel,
                        shelves = shelves
                    )
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
                    LinearProgressIndicator(color = Yellow)
                }
            }
        }
    },
        bottomBar = {
            NavBar(navController = navController)
        })
}

@Composable
fun ShelfBooks(
    userId: String?,
    navController: NavController,
    shelves: List<Shelf>,
    shelfViewModel: ShelfViewModel
) {
    val shelfNames = shelves.map { it.name }
    val (selectedTab, setSelectedTab) = remember { mutableStateOf(0) }
    var booksInShelf by remember {
        mutableStateOf(mutableListOf<Book>())
    }
    val context = LocalContext.current

    var booksLoading by remember {
        mutableStateOf(true)
    }
    var favouritesLoading by remember {
        mutableStateOf(true)
    }
    var booksInFavourites: List<Book> by remember {
        mutableStateOf(emptyList())
    }
    var reviewsLoading by remember {
        mutableStateOf(true)
    }
    var booksReviewed: List<Review> by remember {
        mutableStateOf(emptyList())
    }
    booksInFavourites = shelfViewModel.fetchFavourites(userId) {
        favouritesLoading = false
    }
    booksReviewed = shelfViewModel.fetchReviews(userId) {
        reviewsLoading = false
    }
    val selectedShelf = if (selectedTab >= 0 && selectedTab < shelves.size) {
        shelves[selectedTab]
    } else {
        Shelf("", emptyList())
    }
    booksInShelf = shelfViewModel.getBooksInAShelf(userId, context, selectedShelf.name) {
        booksLoading = false
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            shelfNames.forEachIndexed { index, name ->
                Tab(
                    text = {
                        Text(
                            name,
                            fontFamily = poppinsFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        )
                    },
                    selected = selectedTab == index,
                    onClick = {
                        setSelectedTab(index)
                        favouritesLoading = true
                        reviewsLoading = true
                        booksLoading = true
                    }
                )
            }
        }
        if (booksLoading && favouritesLoading && reviewsLoading) {
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
            if (booksInShelf.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
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
                        if (item.description.isNotEmpty()) {
                            val cleanDescription =
                                HtmlCompat.fromHtml(
                                    item.description,
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                            previewText = cleanDescription.toString()
                        }
                        val bookId = item.bookID
                        val favourite = booksInFavourites.contains(item)
                        val reviewed = booksReviewed.any { it.book.bookID == item.bookID }
                        BookCard(
                            shelfViewModel,
                            userId = userId,
                            book = item,
                            shelfName = selectedShelf.name,
                            favourite = favourite,
                            reviewed = reviewed,
                            bookTitle = title,
                            bookAuthor = author,
                            previewText = previewText,
                            imageUrl = imageUrl,
                            onClick = {
                                navController.navigate(route = BookShelfScreens.BookScreen.name + "/$bookId")
                            },
                            onShelfChanged = {
                                booksInShelf =
                                    shelfViewModel.getBooksInAShelf(
                                        userId,
                                        context,
                                        selectedShelf.name,
                                        onDone = {
                                            booksLoading = false
                                        })
                            },
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
                        "Uh oh, you have no books in the shelf",
                        fontFamily = poppinsFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Text(
                        "Explore books and add them to the shelf to show them here",
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BookCard(
    shelfViewModel: ShelfViewModel,
    userId: String?,
    book: Book,
    shelfName: String?,
    favourite: Boolean,
    reviewed: Boolean,
    bookTitle: String,
    bookAuthor: String,
    previewText: String,
    imageUrl: String,
    onShelfChanged: () -> Unit,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    var favourited by remember {
        mutableStateOf(favourite)
    }
    val favIcon = if (favourited) {
        Icons.Rounded.Favorite
    } else {
        Icons.Rounded.FavoriteBorder
    }
    var isReviewed by remember {
        mutableStateOf(reviewed)
    }
    var isDeleting by remember { mutableStateOf(false) }
    // State to hold the visibility of the review dialog
    var showReviewDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        ReviewDialog(
            shelfViewModel = shelfViewModel,
            userId = userId,
            book = book,
            showReviewDialog = showReviewDialog,
            title = "Review",
            drawable = R.drawable.book,
            onDismiss = { showReviewDialog = false }) {
            isReviewed = true
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        if (!isReviewed) {
                            androidx.compose.material.Icon(
                                Icons.Outlined.RateReview,
                                contentDescription = "Review",
                                tint = Pink500,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(onClick = {
                                        // Show field to enter review
                                        showReviewDialog = true
                                    })
                            )
                        } else {
                            Text(
                                "Reviewed",
                                fontFamily = poppinsFamily,
                                fontSize = 12.sp,
                                color = Pink500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        androidx.compose.material.Icon(
                            favIcon,
                            contentDescription = "Favourite",
                            tint = Pink500,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(
                                    onClick = {
                                        if (!favourited) {
                                            favourited = true
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val success = shelfViewModel.addFavourite(
                                                    userId = userId,
                                                    book = book
                                                )
                                                if (success) {
                                                    withContext(Dispatchers.Main) {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Added to favourites",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Something went wrong!",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                    }
                                                }
                                            }
                                        } else {
                                            favourited = false
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val success = shelfViewModel.removeFavourite(
                                                    userId = userId,
                                                    book = book
                                                )
                                                if (success) {
                                                    withContext(Dispatchers.Main) {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Removed from favourites",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        Toast
                                                            .makeText(
                                                                context,
                                                                "Something went wrong!",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                            .show()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Box {
                            androidx.compose.material.Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Remove",
                                tint = Pink500,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable(onClick = {
                                        isDeleting = true // set the deletion state to true
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val done = shelfViewModel.deleteABookInShelf(
                                                userId, book,
                                                shelfName
                                            )
                                            if (done) {
                                                withContext(Dispatchers.Main) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Book deleted from shelf",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                    onShelfChanged()
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
                    Column(Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onClick),
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
                            androidx.compose.material.Icon(
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDialog(
    shelfViewModel: ShelfViewModel,
    userId: String?,
    book: Book,
    showReviewDialog: Boolean,
    title: String,
    drawable: Int,
    color: Color = Pink500,
    size: Dp = 30.dp,
    onDismiss: () -> Unit,
    onReviewed: () -> Unit
) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    val maxRating = 5
    val context = LocalContext.current
    if (showReviewDialog) {
        AlertDialog(
            /* Dismiss the dialog when the user clicks outside the dialog or on the back
                   button. */
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(5.dp),
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = drawable),
                        contentDescription = "Info",
                        modifier = Modifier
                            .size(size)
                            .align(Alignment.CenterVertically),
                        colorFilter = ColorFilter.tint(color)
                    )
                    Text(
                        title,
                        fontSize = 16.sp,
                        fontFamily = poppinsFamily,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val done = shelfViewModel.addReview(
                            userId,
                            Review(book, rating.toDouble(), reviewText)
                        )
                        if (done) {
                            onReviewed()
                            withContext(Dispatchers.Main) {
                                Toast
                                    .makeText(
                                        context,
                                        "Reviewed!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast
                                    .makeText(
                                        context,
                                        "Failed",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    }
                    onDismiss()
                }) {
                    Text(
                        "Review",
                        fontSize = 14.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        "Cancel",
                        fontSize = 15.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        maxLines = 2,
                        placeholder = {
                            Text(
                                "Describe your experience",
                                fontFamily = poppinsFamily,
                                textAlign = TextAlign.Start
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(5.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = MaterialTheme.colorScheme.outline,
                            placeholderColor = Gray500,
                            cursorColor = Yellow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..maxRating).forEach { ratingValue ->
                                val icon =
                                    if (rating >= ratingValue) Icons.Rounded.Star else Icons.Rounded.StarBorder
                                Icon(
                                    icon,
                                    contentDescription = "Rating $ratingValue",
                                    tint = if (rating >= ratingValue) Yellow else MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.6f
                                    ),
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clickable { rating = ratingValue }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}