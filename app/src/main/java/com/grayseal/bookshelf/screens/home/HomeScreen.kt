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
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
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
import com.grayseal.bookshelf.screens.search.SearchBookViewModel
import com.grayseal.bookshelf.ui.theme.*
import com.grayseal.bookshelf.utils.rememberFirebaseAuthLauncher
import com.grayseal.bookshelf.widgets.BookShelfNavigationDrawerItem
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = hiltViewModel(),
    searchBookViewModel: SearchBookViewModel
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
        val name = dataStore.getName.collectAsState(initial = "")
        // Main Screen Content
        HomeContent(user = user!!, name = name.value, navController, searchBookViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    user: FirebaseUser,
    name: String?,
    navController: NavController,
    searchBookViewModel: SearchBookViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = mapOf(
        "Settings" to R.drawable.settings,
        "Log Out" to R.drawable.logout,
        "Delete Account" to R.drawable.delete
    )
    val selectedItem = remember { mutableStateOf(items["Settings"]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerTonalElevation = 0.dp,
            ) {
                Spacer(Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConstraintLayout {
                        val (profile, edit) = createRefs()
                        Surface(
                            modifier = Modifier
                                .size(60.dp)
                                .background(color = Color.Transparent, shape = CircleShape)
                                .constrainAs(profile) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                },
                            shape = CircleShape,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .size(25.dp)
                                // Clip image to be shaped as a circle
                                .clip(CircleShape)
                                .constrainAs(edit) {
                                    top.linkTo(profile.bottom)
                                    end.linkTo(profile.absoluteRight)
                                    baseline.linkTo(profile.baseline)
                                    bottom.linkTo(profile.bottom)
                                },
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = Color.White)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pen),
                                contentDescription = "Update Profile Picture",
                                modifier = Modifier
                                    .padding(3.dp)
                            )
                        }

                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Hi, ${name}!", fontFamily = loraFamily,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(15.dp))
                    Text(
                        "16 books in your reading list", fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(15.dp))
                    Divider(color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(20.dp))
                    items.forEach { item ->
                        BookShelfNavigationDrawerItem(
                            icon = {
                                androidx.compose.material3.Icon(
                                    painter = painterResource(id = item.value),
                                    contentDescription = item.key,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(color = Color.Transparent)
                                )
                            },
                            label = {
                                androidx.compose.material3.Text(
                                    item.key,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 17.sp
                                )
                            },
                            selected = item.value == selectedItem.value,
                            onClick = {
                                selectedItem.value = item.value
                                scope.launch { drawerState.close() }
                                if (item.key == "Log Out") {
                                    Firebase.auth.signOut()
                                    navController.navigate(BookShelfScreens.HomeScreen.name)
                                } else if (item.key == "Delete Account") {
                                    user.delete()
                                    navController.navigate(BookShelfScreens.HomeScreen.name)
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.background,
                                unselectedContainerColor = Color.Transparent,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground
                            )
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
                    TopHeader(navController, searchBookViewModel) {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                    MainCard()
                    Categories(navController = navController)
                    ReadingList(onClick = {
                        navController.navigate(route = BookShelfScreens.BookScreen.name + "/bookId")
                    })
                }
            },
                bottomBar = {
                    NavBar()
                })
        }
    )
}

@Composable
fun TopHeader(
    navController: NavController,
    viewModel: SearchBookViewModel,
    onProfileClick: () -> Unit
) {
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
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = true,
                        onClick = {
                            onProfileClick()
                        },
                    ),
                contentScale = ContentScale.FillBounds
            )
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(enabled = true, onClick = {
                        viewModel.listOfBooks.value = listOf()
                        navController.navigate(route = BookShelfScreens.SearchScreen.name)
                    }),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(
                    width = 0.9.dp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(color = Color.Transparent, shape = CircleShape),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
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
            if (isSystemInDarkTheme()) {
                Image(
                    painter = painterResource(id = R.drawable.darkcard),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.card),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Track your", fontFamily = poppinsFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    "reading activity", fontFamily = poppinsFamily,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(15.dp)
                        ),
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
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
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
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                "Continue reading", fontFamily = poppinsFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            if (isSystemInDarkTheme()) {
                                Surface(
                                    modifier = Modifier.size(60.dp),
                                    shape = CircleShape,
                                    color = Color.Transparent
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.darkchart),
                                        contentDescription = "Add",
                                        modifier = Modifier
                                            .background(color = Color.Transparent)
                                    )
                                }
                            } else {
                                Surface(
                                    modifier = Modifier.size(60.dp),
                                    shape = CircleShape,
                                    color = Color.Transparent
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.lightchart),
                                        contentDescription = "Add",
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
}

@Composable
fun Categories(navController: NavController) {
    Text(
        "Categories",
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onBackground,
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
                categories[item]?.let {
                    Category(
                        category = item,
                        image = it,
                        onClick = { navController.navigate(route = BookShelfScreens.CategoryScreen.name + "/$item") })
                }
            } else {
                Category(
                    category = item,
                    image = categories[item]!!,
                    onClick = { navController.navigate(route = BookShelfScreens.CategoryScreen.name + "/$item") })
            }
        }
    }
}

@Composable
fun ReadingList(onClick: () -> Unit) {
    Text(
        "Reading List",
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onBackground,
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
                    image = R.drawable.profile,
                    onClick = onClick
                )
            } else {
                Reading(
                    bookAuthor = "Dan Brown",
                    bookTitle = "Deception Point",
                    image = R.drawable.profile,
                    onClick = onClick
                )
            }
        }
    }
}

