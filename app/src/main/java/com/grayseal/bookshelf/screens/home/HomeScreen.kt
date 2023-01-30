package com.grayseal.bookshelf.screens.home

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.components.Category
import com.grayseal.bookshelf.components.NavBar
import com.grayseal.bookshelf.components.Reading
import com.grayseal.bookshelf.data.categories
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.screens.login.LoginScreen
import com.grayseal.bookshelf.screens.login.LoginScreenViewModel
import com.grayseal.bookshelf.screens.login.StoreUserName
import com.grayseal.bookshelf.ui.theme.*
import com.grayseal.bookshelf.utils.rememberFirebaseAuthLauncher
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = StoreUserName(context)
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                user = result.user
                scope.launch {
                    user?.displayName?.let { dataStore.saveName(it) }
                }
            },
            onAuthError = {
                user = null
            }
        )
    if (user == null) {
        LoginScreen(navController, launcher, viewModel, dataStore)
    } else {
        // Main Screen Content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val name = dataStore.getName.collectAsState(initial = "")
            Text("Welcome ${name.value}")
            Button(onClick = {
                // TODO sign out
                Firebase.auth.signOut()
                navController.navigate(BookShelfScreens.HomeScreen.name)
            }) {
                Text("Sign Out")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeContent() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
// icons to mimic drawer destinations
    val items = mapOf(
        "Settings" to Icons.Rounded.Settings,
        "Log Out" to Icons.Rounded.Logout,
        "Delete Account" to Icons.Rounded.DeleteForever
    )
    val selectedItem = remember { mutableStateOf(items["Settings"]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = Color.White,
                drawerTonalElevation = 0.dp,
            ) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wall_burst),
                        contentDescription = "Profile Picture"
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Hi, Lynne!", fontFamily = loraFamily,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "16 books in your reading list", fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = Gray500
                    )
                    Spacer(Modifier.height(30.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                androidx.compose.material3.Icon(
                                    item.value,
                                    contentDescription = null
                                )
                            },
                            label = {
                                androidx.compose.material3.Text(
                                    item.key,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp
                                )
                            },
                            selected = item.value == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item.value
                            },
                            shape = RectangleShape,
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.background,
                                unselectedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
        scrimColor = Color.Transparent,
        content = {
            Scaffold(content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp)
                ) {
                    TopHeader()
                    MainCard()
                    Categories()
                    ReadingList()
                }
            },
                bottomBar = {
                    NavBar()
                })
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopHeader(displayName: String = "Lynne") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .background(color = Color.Transparent, shape = CircleShape),
            shape = CircleShape,
        ) {
            Image(
                painter = painterResource(id = R.drawable.wall_burst),
                contentDescription = "Profile Picture"
            )
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(width = 0.9.dp, color = Gray200)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = iconColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .background(color = Color.Transparent)
                )
            }
        }
    }
}

@Preview
@Composable
fun MainCard() {
    Card(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.card),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Track your", fontFamily = poppinsFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    "reading activity", fontFamily = poppinsFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .background(color = Pink200, shape = RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = Color.Transparent, shape = CircleShape),
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = Pink200)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wall_burst),
                                contentDescription = "Book Picture"
                            )
                        }
                        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                            Text(
                                "Book Title", fontFamily = poppinsFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Pink700
                            )
                            Text(
                                "Continue reading", fontFamily = poppinsFamily,
                                fontSize = 12.sp,
                                color = Pink700
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Surface(
                                modifier = Modifier.size(30.dp),
                                shape = CircleShape,
                                color = Color.Transparent
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LibraryAdd,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .background(color = Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Categories() {
    Text(
        "Categories",
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        color = Color.Black.copy(alpha = 0.75f),
        modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)
    )
    val keysList = categories.keys.toList()
    LazyRow(
        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp, end = 0.dp, start = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        itemsIndexed(items = keysList) { index: Int, item: String ->
            if (index == 0) {
                Spacer(modifier = Modifier.width(20.dp))
                categories[item]?.let { Category(category = item, image = it) }
            } else {
                Category(category = item, image = categories[item]!!)
            }
        }
    }
}

@Composable
fun ReadingList() {
    Text(
        "Reading List",
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        color = Color.Black.copy(alpha = 0.75f),
        modifier = Modifier.padding(start = 20.dp, end = 20.dp)
    )
    val keysList = categories.keys.toList()
    LazyRow(
        modifier = Modifier
            .padding(top = 10.dp, start = 0.dp, end = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        itemsIndexed(items = keysList) { index: Int, item: String ->
            if (index == 0) {
                Spacer(modifier = Modifier.width(20.dp))
                Reading(
                    bookAuthor = "Dan Brown",
                    bookTitle = "Deception Point",
                    image = R.drawable.loginillustration
                )
            } else {
                Reading(
                    bookAuthor = "Dan Brown",
                    bookTitle = "Deception Point",
                    image = R.drawable.loginillustration
                )
            }
        }
    }
}

