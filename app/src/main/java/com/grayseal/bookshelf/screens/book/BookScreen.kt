package com.grayseal.bookshelf.screens.book

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardBackspace
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.ui.theme.Pink200
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.loraFamily
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookScreen(navController: NavController) {
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scope = rememberCoroutineScope()
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    var isFabVisible by remember { mutableStateOf(true) } // add this line

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetElevation = 10.dp,
        sheetBackgroundColor = Yellow,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        floatingActionButton = {
            if (isFabVisible) { // modify this line
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            "Add to Shelf",
                            fontFamily = poppinsFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.padding(bottom = 100.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Sharp.Edit,
                            contentDescription = "Add to Shelf",
                            tint = Color.White
                        )
                    },
                    onClick = {
                        isFabVisible = false
                        scope.launch {
                            if (sheetState.isCollapsed) {
                                sheetState.expand()
                            }
                        }
                    },
                    backgroundColor = Yellow,
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
        Details()
    }
    LaunchedEffect(sheetState.isCollapsed) {
        if (sheetState.isCollapsed) {
            isFabVisible = true
        }
    }

}

@Composable
fun Details() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        TopSection()
        BookDescription()
    }

}

@Composable
fun TopSection() {
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = {}),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardBackspace,
                    contentDescription = "Back Icon",
                    modifier = Modifier
                        .size(30.dp)
                        .background(color = Color.Transparent)
                )
            }
            Surface(
                modifier = Modifier
                    .clickable(onClick = {}),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "Back Icon",
                    modifier = Modifier
                        .size(30.dp)
                        .background(color = Color.Transparent)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.book),
                contentDescription = "Book Image",
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(5.dp)
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun BookDescription(
    bookTitle: String = "Deception Point",
    bookAuthor: String = "Dan Brown",
    publishedDate: String = "17-12-2003",
    genre: String = "Fiction",
    description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Tortor dignissim convallis aenean et tortor. Rhoncus mattis rhoncus urna neque viverra justo nec ultrices. Porta nibh venenatis cras sed felis eget velit aliquet sagittis. Sed id semper risus in hendrerit gravida rutrum quisque. Nulla posuere sollicitudin aliquam ultrices. Quisque id diam vel quam elementum pulvinar. Eros in cursus turpis massa tincidunt dui. Est velit egestas dui id ornare arcu. Dui sapien eget mi proin sed. Volutpat lacus laoreet non curabitur. Ullamcorper eget nulla facilisi etiam. Pharetra convallis posuere morbi leo urna molestie at elementum. Tortor posuere ac ut consequat semper viverra."
) {
    // Split the description paragraph after the first three sentences
    val firstThreeSentences =
        description.substringBefore(".").substringBefore(".").substringBefore(".") + "."
    val remainingDescription = description.substringAfter(firstThreeSentences)

    Surface(
        modifier = Modifier.clip(
            shape = RoundedCornerShape(
                topStart = 15.dp,
                topEnd = 15.dp
            )
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        bookTitle,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        bookAuthor,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        publishedDate,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Pink200, shape = RoundedCornerShape(5.dp)),
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
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                    Text(
                        genre,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
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
                        "Genre",
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                    Text(
                        genre,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
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
                        "Genre",
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                    Text(
                        genre,
                        fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Row {
                    Text(
                        text = "Descriptions",
                        fontFamily = poppinsFamily,
                        textAlign = TextAlign.Start,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }
                Row {
                    Text(
                        text = firstThreeSentences.first().toString(),
                        fontFamily = loraFamily,
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 65.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(end = 20.dp).align(Alignment.CenterVertically)
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier.height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "Add to Shelf",
            fontFamily = poppinsFamily,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Divider(
            modifier = Modifier.padding(5.dp)
        )
        Text(
            "Reading now",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Divider(
            modifier = Modifier.padding(5.dp)
        )
        Text(
            "To Read",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Divider(
            modifier = Modifier.padding(5.dp)
        )
        Text(
            "Have Read",
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Divider(
            modifier = Modifier.padding(5.dp)
        )
    }
}
