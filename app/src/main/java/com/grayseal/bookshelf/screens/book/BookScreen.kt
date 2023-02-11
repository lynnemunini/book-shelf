package com.grayseal.bookshelf.screens.book

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardBackspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.ui.theme.Pink200
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun BookScreen(navController: NavController) {
    Details()
}

@Preview(showBackground = true)
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
    Surface(modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    description,
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}