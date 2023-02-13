package com.grayseal.bookshelf.screens.book

import android.util.Log
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.data.DataOrException
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.ui.theme.Pink200
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.loraFamily
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import kotlinx.coroutines.launch
import kotlin.math.log

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookScreen(navController: NavController, bookViewModel: BookViewModel, bookId: String?) {
    val bookInfo = produceState<DataOrException<Book, Boolean, Exception>>(
        initialValue = DataOrException(loading = (true))
    ) {
        value = bookId?.let { bookViewModel.getBookInfo(it) }!!
    }.value
    Log.d("BOOKINFO", "BookScreen: $bookInfo")
    // Book Data
    val book = bookInfo.data

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scope = rememberCoroutineScope()
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    var isFabVisible by remember { mutableStateOf(true) } // add this line

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
                    modifier = Modifier.padding(bottom = 100.dp),
                    onClick = {
                        isFabVisible = false
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            }
                        }
                    },
                    backgroundColor = Pink200,
                    shape = RoundedCornerShape(15.dp),
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        sheetContent = {
            BottomSheetContent()
        }
    ) {
        Details(navController, book)
    }
    LaunchedEffect(sheetState.isCollapsed) {
        if (sheetState.isCollapsed) {
            isFabVisible = true
        }
    }

}

@Composable
fun Details(navController: NavController, book: Book?) {
    var bookTitle: String = "Unavailable"
    var bookAuthor: String = "Unavailable"
    var rating: String = "0"
    var genre: String = "Unavailable"
    var pages: String = "0"
    var time: String = "0"
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
        }
        if (book.pageCount.toString().isNotEmpty()) {
            pages = book.pageCount.toString()
        }
        if (book.description.isNotEmpty()) {
            description = book.description
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
        Column {
            TopSection(navController = navController)
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                if(book != null) {
                    BookImage(imageUrl = imageUrl)
                    BookDescription(
                        bookTitle = bookTitle,
                        bookAuthor = bookAuthor,
                        rating = rating,
                        genre = genre,
                        pages = pages,
                        time = time,
                        description = description
                    )
                }
                else{
                    CircularProgressIndicator(color = Yellow)
                }
            }
        }
    }
}

@Composable
fun TopSection(navController: NavController) {
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(70.dp)
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

@Composable
fun BookImage(imageUrl: String) {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = "Book Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(5.dp)
                    )
            )
        }
    }

}

@Composable
fun BookDescription(
    bookTitle: String,
    bookAuthor: String,
    rating: String,
    genre: String,
    pages: String,
    time: String,
    description: String
) {
    // Split the description paragraph after the first three sentences
    val firstThreeSentences =
        description.substringBefore(".").substringBefore(".").substringBefore(".") + "."
    val remainingDescription = description.substringAfter(firstThreeSentences)

    Surface(
        modifier = Modifier.fillMaxSize().clip(
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp
            )
        ),
        color = Color(0xFFfbf2f0)
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
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "by $bookAuthor",
                        fontFamily = poppinsFamily,
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
                    .padding(vertical = 10.dp)
                    .background(
                        color = Color(0xFFf9f9f9),
                        shape = RoundedCornerShape(10.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
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
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.line),
                    contentDescription = "Separator"
                )
                Column(
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
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.line),
                    contentDescription = "Separator"
                )
                Column(
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
                        time,
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
                        text = firstThreeSentences.first().toString(),
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
                        text = firstThreeSentences.drop(1),
                        textAlign = TextAlign.Justify,
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row {
                    Text(
                        text = remainingDescription,
                        fontFamily = poppinsFamily,
                        textAlign = TextAlign.Justify,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier.height(220.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
        Text(
            "Reading now 📖",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Divider(
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Text(
            "To Read 📌",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Divider(
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Text(
            "Have Read 📚",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Divider(
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}
