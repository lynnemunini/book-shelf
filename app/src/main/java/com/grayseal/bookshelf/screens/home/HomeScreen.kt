package com.grayseal.bookshelf.screens.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
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
import java.io.File
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = hiltViewModel(),
    searchBookViewModel: SearchBookViewModel
) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    val context = LocalContext.current

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.profile)

    var avatar: Bitmap = bitmap

    // Retrieve avatar from dataStore is user has set any
    val imageDataStore = StoreProfileImage(context)
    val session = StoreSession(context)
    val imagePath = imageDataStore.getImagePath.collectAsState(initial = "").value

    /* ------------YOU STOPPED HERE❗❗❗❗------------------ */

    if(imagePath != "") {
        Log.d("IMAGEPATH", imagePath)
        val imageUri = Uri.parse(imagePath)
        Log.d("IMAGEURI", "$imageUri")

        if (!session.isFirstTime) {
            // convert imageUri to bitmap
            imageUri?.let {
                avatar = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images
                        .Media.getBitmap(context.contentResolver, it)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        }
    }
    /* ------------------------------------------------ */

    val scope = rememberCoroutineScope()
    // Retrieve user name from dataStore
    val nameDataStore = StoreUserName(context)
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                user = result.user
                scope.launch {
                    user?.displayName?.let { nameDataStore.saveName(it) }
                }
            },
            onAuthError = {
                user = null
            }
        )
    if (user == null) {
        LoginScreen(navController, launcher, viewModel, nameDataStore)
    } else {
        val name = nameDataStore.getName.collectAsState(initial = "")
        // Main Screen Content

        HomeContent(
            user = user!!,
            name = name.value,
            avatar = avatar,
            navController = navController,
            searchBookViewModel = searchBookViewModel,
            imageDataStore = imageDataStore,
            session = session
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    user: FirebaseUser,
    name: String?,
    avatar: Bitmap,
    navController: NavController,
    searchBookViewModel: SearchBookViewModel,
    imageDataStore: StoreProfileImage,
    session: StoreSession
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = mapOf(
        "Settings" to R.drawable.settings,
        "Log Out" to R.drawable.logout,
        "Delete Account" to R.drawable.delete
    )
    val selectedItem = remember { mutableStateOf(items["Settings"]) }
    var openDialog by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap>(avatar)
    }
    // Retrieve an image from the device gallery
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        Log.d("IMAGEURILAUNCHER", "$uri and ${uri?.path}")
    }

    imageUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images
                .Media.getBitmap(context.contentResolver, it)

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }
        // Update user profile image on dataStore and set firstSession to false
        scope.launch {
            val path = imageUri?: return@launch
            Log.d("PATH", "$path")
            imageDataStore.saveImagePath(path)
            session.setIsFirstTimeLaunch(false)
        }
    }

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
                                }
                                .clickable(onClick = {
                                    launcher.launch("image/*")
                                }),
                            shape = CircleShape,
                        ) {
                            Image(
                                bitmap = bitmap.value.asImageBitmap(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = {
                                        launcher.launch("image/*")
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
                                }
                                .clickable(onClick = {
                                    launcher.launch("image/*")
                                }),
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
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "16 books in your reading list", fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(20.dp))
                    items.forEach { item ->
                        val selectedValue = selectedItem.value
                        val selectedKey = items.entries.find { it.value == selectedValue }?.key
                        AlertDialog(
                            openDialog = openDialog,
                            title = if (selectedKey == "Log Out") {
                                "Confirm Logout"
                            } else {
                                "Delete Account"
                            },
                            details = if (selectedKey == "Log Out") {
                                "Are you sure you want to end your session?"
                            } else {
                                "You are about to delete your account. Is this what you want?"
                            },
                            onDismiss = {
                                openDialog = false
                            },
                            onClick = {
                                if (selectedKey == "Log Out") {
                                    Firebase.auth.signOut()
                                    navController.navigate(BookShelfScreens.HomeScreen.name)
                                } else {
                                    user.delete()
                                    navController.navigate(BookShelfScreens.HomeScreen.name)
                                }
                            }
                        )
                        BookShelfNavigationDrawerItem(
                            icon = {
                                androidx.compose.material3.Icon(
                                    painter = painterResource(id = item.value),
                                    contentDescription = item.key,
                                    modifier = Modifier
                                        .size(40.dp)
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
                                if (item.key == "Log Out" || item.key == "Delete Account") {
                                    openDialog = true
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
                    TopHeader(navController, searchBookViewModel, avatar = avatar) {
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
    avatar: Bitmap,
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
                bitmap = avatar.asImageBitmap(),
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
                        viewModel.loading.value = false
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

@Composable
fun AlertDialog(
    openDialog: Boolean,
    title: String,
    details: String,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            /* Dismiss the dialog when the user clicks outside the dialog or on the back
                   button. */
            onDismissRequest = onDismiss,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "Info",
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        title,
                        fontSize = 18.sp,
                        fontFamily = poppinsFamily,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = details,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = onClick) {
                    Text(
                        "Confirm",
                        fontSize = 15.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text(
                        "Cancel",
                        fontSize = 15.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
