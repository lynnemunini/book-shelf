package com.grayseal.bookshelf.screens.book

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
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
import com.grayseal.bookshelf.components.ShelvesAlertDialog
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
This composable function represents a screen that displays details of a book.
 * @param navController The navigation controller used to navigate between screens.
 * @param bookViewModel The ViewModel used to retrieve book information.
 * @param bookId The ID of the book to display information for.
 * @return A Composable function that displays book details and allows the user to add the book to a shelf.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookScreen(navController: NavController, bookViewModel: BookViewModel, bookId: String?) {
    var openDialog by remember {
        mutableStateOf(false)
    }

    val bookInfo = produceState<DataOrException<Book, Boolean, Exception>>(
        initialValue = DataOrException(loading = (true))
    ) {
        value = bookId?.let { bookViewModel.getBookInfo(it) }!!
    }.value

    // Book Data
    val book = bookInfo.data


    // Get current user
    val userId = Firebase.auth.currentUser?.uid
    val context = LocalContext.current

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)

    val scope = rememberCoroutineScope()
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    var isFabVisible by remember { mutableStateOf(true) }
    var otherShelfName = ""
    var shelfName = ""

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetElevation = 40.dp,
        sheetBackgroundColor = Color.White,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        // To dismiss bottomSheet when clicking outside on the screen
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    if (sheetState.isExpanded) {
                        sheetState.collapse()
                    }
                })
            },
        floatingActionButton = {
            if (isFabVisible) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            "Add to Shelf",
                            fontFamily = poppinsFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    },
                    modifier = Modifier
                        .padding(bottom = 90.dp),
                    onClick = {
                        isFabVisible = false
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            }
                        }
                    },
                    shape = RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp),
                    backgroundColor = Pink200,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        sheetContent = {
            BottomSheetContent(onSave = { name ->
                shelfName = name
                // Add book to shelf
                if (book != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val added = bookViewModel.addBookToShelf(
                            userId,
                            shelfName,
                            book,
                            context,
                            shelfExists = { ShelfExistsName ->
                                otherShelfName = ShelfExistsName
                                openDialog = true
                            })
                        if (added) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Successfully added book to $shelfName shelf",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (otherShelfName == "") {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Failed to add book to $shelfName shelf",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            })
        }
    ) {
        ShelvesAlertDialog(
            openDialog = openDialog,
            drawable = R.drawable.info,
            title = "Add to Shelf",
            details = "The book is already on the $otherShelfName shelf. Would you like to move it to the $shelfName shelf instead?",
            onDismiss = { openDialog = false },
            onClick = {
                if (userId != null) {
                    bookViewModel.setBookToMove(book!!)
                    CoroutineScope(Dispatchers.IO).launch {
                        bookViewModel.moveBookToShelf(userId, shelfName, otherShelfName)
                    }
                    Toast.makeText(
                        context,
                        "Moving book from $otherShelfName shelf to $shelfName shelf...",
                        Toast.LENGTH_SHORT
                    ).show()
                    openDialog = false
                }
            }
        )
        Box {
            Details(navController, book)
            // transparent overlay on top of content, shown if sheet is expanded
            if (sheetState.isExpanded) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f))
                        .fillMaxSize()
                ) {}
            }
        }
    }
    LaunchedEffect(sheetState.isCollapsed) {
        if (sheetState.isCollapsed) {
            isFabVisible = true
        }
    }
}

/**
A composable function that displays the details of a book. If the book parameter is null, it will display a loading indicator.
 * If the book is not null, the function will display the book's image, title, author, rating, genre, number of pages, and description.
 * @param navController the navigation controller used to navigate to other screens
 * @param book the book to display, or null if data is not yet available
 */
@Composable
fun Details(navController: NavController, book: Book?) {
    var bookTitle: String = "Unavailable"
    var bookAuthor: String = "Unavailable"
    var rating: String = "0"
    var genre: String = "Unavailable"
    var pages: String = "0"
    var description: String = "Preview information not provided"
    var imageUrl =
        "https://media.istockphoto.com/id/1147544807/vector/thumbnail-image-vector-graphic.jpg?s=612x612&w=0&k=20&c=rnCKVbdxqkjlcs3xH87-9gocETqpspHFXu5dIGB4wuM="

    // Check if data is available
    if (book != null) {
        if (book.title.isNotEmpty()) {
            bookTitle = book.title
        }
        if (book.authors[0].isNotEmpty()) {
            bookAuthor = book.authors.joinToString(separator = ", ")

        }
        if (book.ratingsCount.toString().isNotEmpty()) {
            rating = book.averageRating.toString()
        }
        if (book.categories[0].isNotEmpty()) {
            genre = book.categories[0]
            val words = genre.split("/")
                .map { it.trim() } // split the string by "/" and remove extra whitespace
            val smallestWord =
                words.minByOrNull { it.length } // find the smallest word based on length
            genre = smallestWord ?: "" // if there are no words, return an empty string

        }
        if (book.pageCount.toString().isNotEmpty()) {
            pages = book.pageCount.toString()
        }
        if (book.description.isNotEmpty()) {
            val cleanDescription =
                HtmlCompat.fromHtml(book.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            description = cleanDescription.toString()
        }
        if (book.imageLinks.toString().isNotEmpty()) {
            imageUrl = book.imageLinks.thumbnail.toString().trim()
            imageUrl = imageUrl.replace("http", "https")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.sky),
            contentDescription = "backgroundImage",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            TopSection(navController = navController)
            if (book == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LinearProgressIndicator(color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    BookImage(imageUrl = imageUrl)
                    BookDescription(
                        bookTitle = bookTitle,
                        bookAuthor = bookAuthor,
                        rating = rating,
                        genre = genre,
                        pages = pages,
                        description = description
                    )
                }
            }
        }
    }
}


/**
A composable function that displays the top section of the book details screen.
 * @param navController The NavController used to navigate back to the previous screen.
 */
@Composable
fun TopSection(navController: NavController) {
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(80.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(enabled = true, onClick = {
                        navController.popBackStack()
                    }),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(
                    width = 0.9.dp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(color = Color.Transparent, shape = CircleShape),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
            Text(
                "Book details",
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
A composable function that displays a book image using the provided [imageUrl].
 * @param imageUrl The URL of the book image to display.
 */
@Composable
fun BookImage(imageUrl: String) {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = "Book Image",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .scale(2.5f)
            )

        }
    }
}

/**
A composable function that displays the details of a book.
 * @param bookTitle The title of the book.
 * @param bookAuthor The author of the book.
 * @param rating The rating of the book, out of 5.
 * @param genre The genre of the book.
 * @param pages The number of pages in the book.
 * @param description A short description of the book.
 */
@Composable
fun BookDescription(
    bookTitle: String,
    bookAuthor: String,
    rating: String,
    genre: String,
    pages: String,
    description: String
) {
    val time = (pages.toInt() * 1.7).toInt().toString()
    var restOfText by remember { mutableStateOf("") }
    val firstParagraph = description.substringBefore("\n\n")
    val remainingDescription = description.substringAfter(firstParagraph).substringAfter("\n\n")

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                shape = RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp
                )
            ),
        color = MaterialTheme.colorScheme.background

    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        bookTitle,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "by $bookAuthor",
                        fontFamily = poppinsFamily,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until rating.toFloat().toInt()) {
                    Icon(Icons.Rounded.Star, contentDescription = "star", tint = Yellow)
                }
                if ((5 - rating.toFloat()) > 0) {
                    val unrated = 5 - rating.toFloat().toInt()
                    if ((rating.toFloat() - rating.toFloat().toInt()) > 0) {
                        Icon(
                            Icons.Rounded.StarHalf,
                            contentDescription = "star",
                            tint = Yellow
                        )
                        for (i in 0 until unrated - 1) {
                            Icon(
                                Icons.Rounded.Star,
                                contentDescription = "star",
                                tint = Color.LightGray
                            )
                        }
                    } else {
                        for (i in 0 until unrated) {
                            Icon(
                                Icons.Rounded.Star,
                                contentDescription = "star",
                                tint = Color.LightGray
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
                        withStyle(
                            style = SpanStyle(
                                fontFamily = poppinsFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.2f
                                )
                            )
                        ) {
                            append(" / 5.0")
                        }
                    },
                    modifier = Modifier.padding(start = 5.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(vertical = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(10.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Genre",
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    )
                    Text(
                        genre,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Clip,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                        .padding(vertical = 5.dp)
                )
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Pages",
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    )
                    Text(
                        "$pages pages",
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()  //fill the max height
                        .width(1.dp)
                        .padding(vertical = 5.dp)
                )
                Column(
                    modifier = Modifier
                        .width(100.dp)
                        .padding(top = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Time",
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    )
                    Text(
                        "$time min",
                        textAlign = TextAlign.Center,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = "Descriptions",
                        fontFamily = poppinsFamily,
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                Row {
                    Text(
                        text = firstParagraph.first().toString(),
                        fontFamily = loraFamily,
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = firstParagraph.drop(1),
                        textAlign = TextAlign.Justify,
                        fontFamily = poppinsFamily,
                        maxLines = 4,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        onTextLayout = { layoutResult ->
                            if (layoutResult.lineCount > 3) {
                                // get the text beyond the 4 lines
                                restOfText =
                                    firstParagraph.drop(1).substring(layoutResult.getLineEnd(3))
                            }
                        }
                    )
                }
                if (restOfText.isNotEmpty()) {
                    Text(
                        text = restOfText,
                        fontFamily = poppinsFamily,
                        textAlign = TextAlign.Justify,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = remainingDescription,
                    fontFamily = poppinsFamily,
                    textAlign = TextAlign.Justify,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

/**
Composable function that creates the content of the bottom sheet to display options for adding a book to a bookshelf.
 * The function creates a Column that contains a Row and several Text elements separated by Divider elements. The Row contains a single Divider element
 * styled with rounded corners. The Text elements display options for adding the book to various bookshelves, including "Reading now," "To Read," and "Have Read."
 */

@Composable
fun BottomSheetContent(onSave: (String) -> Unit) {
    Column(
        modifier = Modifier.height(220.dp).background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Divider(
                Modifier
                    .width(60.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
        }
        Text(
            text = "Add to book shelf?",
            fontFamily = poppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp)
        )
        Divider(
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    onSave("Reading Now 📖")
                })
        ) {
            Text(
                "Reading Now 📖",
                fontFamily = poppinsFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        Divider(
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    onSave("To Read 📌")
                })
        ) {
            Text(
                "To Read 📌",
                fontFamily = poppinsFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        Divider(
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    onSave("Have Read 📚")
                })
        ) {
            Text(
                "Have Read 📚",
                fontFamily = poppinsFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        Divider(
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}