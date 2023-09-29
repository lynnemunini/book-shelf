package com.grayseal.bookshelf.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.data.BottomNavItem
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.*
import com.grayseal.bookshelf.utils.isValidEmail

/**

EmailInput is a composable function that creates an email input field.
 * @param modifier: Modifier, the modifier to be applied to the input field.
 * @param emailState: MutableState<String>, the state object that holds the current value of the input field.
 * @param labelId: String, the label id of the input field
 * @param enabled: Boolean, flag that indicates whether the input field is enabled or not.
 * @param imeAction: ImeAction, the action that should be taken when the input field is activated.
 * @param onAction: KeyboardActions, the action that should be taken when the input field is deactivated.
 * @return None
 */
@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    EmailInputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction
    )
}

/**

NameInput is a composable function that creates an input field for a user's name.
 * @param nameState: MutableState<String>, the state object that holds the current value of the input field.
 * @param labelId: String, the label id of the input field.
 * @param enabled: Boolean, flag that indicates whether the input field is enabled or not.
 * @param imeAction: ImeAction, the action that should be taken when the input field is activated.
 * @param onAction: KeyboardActions, the action that should be taken when the input field is deactivated.
 * @return None
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInput(
    nameState: MutableState<String>,
    labelId: String = "Name",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    var icon by remember {
        mutableStateOf(Icons.Outlined.SentimentSatisfied)
    }
    OutlinedTextField(
        value = nameState.value,
        onValueChange = {
            nameState.value = it
            icon = Icons.Outlined.InsertEmoticon
        },
        placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "Name Icon"
            )
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 14.sp,
            fontFamily = poppinsFamily,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = Yellow,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            placeholderColor = Gray500,
            selectionColors = TextSelectionColors(
                handleColor = Yellow,
                backgroundColor = Pink200
            )
        )
    )
}

/**

PasswordInput is a composable function that creates a password input field.
 * @param modifier: Modifier, the modifier to be applied to the input field.
 * @param passwordState: MutableState<String>, the state object that holds the current value of the password input field.
 * @param labelId: String, the label id of the input field.
 * @param enabled: Boolean, flag that indicates whether the input field is enabled or not.
 * @param passwordVisibility: MutableState<Boolean>, the state object that holds the current visibility of the password.
 * @param imeAction: ImeAction, the action that should be taken when the input field is activated.
 * @return None
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done
) {
    val visualTransformation = if (passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()
    var error by remember {
        mutableStateOf(false)
    }
    if (error) {
        Text(
            "* Password must be at least 6 characters", fontSize = 12.sp,
            fontFamily = poppinsFamily,
            color = Yellow,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {
            passwordState.value = it
            error = passwordState.value.length < 6
        },
        placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Lock Icon"
            )
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 14.sp,
            fontFamily = poppinsFamily,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        trailingIcon = {
            PasswordVisibility(passwordVisibility = passwordVisibility)
        },
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
        },

        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = Yellow,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            placeholderColor = Gray500,
            selectionColors = TextSelectionColors(
                handleColor = Yellow,
                backgroundColor = Pink200
            )
        )
    )
}

/**

PasswordVisibility is a composable function that creates a button to toggle the visibility of the password input field.
 * @param passwordVisibility: MutableState<Boolean>, the state object that holds the current visibility of the password.
 * @return None
 */
@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value = !visible }) {
        if (visible) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = "Visibility Icon",
                tint = iconColor
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = "Visibility Icon",
                tint = iconColor
            )
        }
    }
}

/**

EmailInputField is a composable function that creates an input field for a user's email address.
 * @param modifier: Modifier, the modifier to be applied to the input field.
 * @param valueState: MutableState<String>, the state object that holds the current value of the email input field.
 * @param labelId: String, the label id of the input field.
 * @param enabled: Boolean, flag that indicates whether the input field is enabled or not.
 * @param isSingleLine: Boolean, flag that indicates whether the input field is single or multi-line.
 * @param keyboardType: KeyboardType, the type of keyboard to be used for the input field.
 * @param imeAction: ImeAction, the action that should be taken when the input field is activated.
 * @param onAction: KeyboardActions, the action that should be taken when the input field is deactivated.
 * @return None
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmailInputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    var error by remember {
        mutableStateOf(false)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    if (error) {
        Text(
            "* Invalid email address", fontSize = 12.sp,
            fontFamily = poppinsFamily,
            color = Yellow,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
    OutlinedTextField(
        value = valueState.value,
        onValueChange = {
            valueState.value = it
            error = !isValidEmail(valueState.value)
        },
        placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.AlternateEmail,
                contentDescription = "Email Icon"
            )
        },
        singleLine = isSingleLine,
        textStyle = TextStyle(
            fontSize = 14.sp,
            fontFamily = poppinsFamily,
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth(),
        enabled = enabled,
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = Yellow,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            placeholderColor = Gray500,
            selectionColors = TextSelectionColors(
                handleColor = Yellow,
                backgroundColor = Pink200
            ),
        )
    )
}

/**

SubmitButton is a composable function that creates a button for submitting a form.
 * @param textId: String, the text displayed on the button.
 * @param loading: Boolean, flag that indicates whether the button is in a loading state or not.
 * @param validInputs: Boolean, flag that indicates whether the form inputs are valid or not.
 * @param onClick: () -> Unit, callback function that is called when the button is clicked.
 * @return None
 */
@Composable
fun SubmitButton(textId: String, loading: Boolean, validInputs: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        enabled = !loading && validInputs, shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.LightGray
        ),
        elevation = ButtonDefaults.buttonElevation(10.dp)
    ) {
        if (loading) CircularProgressIndicator(color = Yellow) else
            if (validInputs) {
                Text(
                    text = textId,
                    fontFamily = poppinsFamily,
                    modifier = Modifier.padding(5.dp),
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = textId,
                    fontFamily = poppinsFamily,
                    modifier = Modifier.padding(5.dp),
                    fontSize = 15.sp,
                    color = iconColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
    }
}

/**

ContinueGoogle is a composable function that creates an image of the google icon that can be clicked to continue with google sign in.
 * @param onClick: () -> Unit, callback function that is called when the google icon is clicked
 * @return None
 */
@Composable
fun ContinueGoogle(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.google),
        contentDescription = "Google Icon",
        modifier = Modifier
            .height(28.dp)
            .clickable(onClick = onClick)
    )
}

/**
A Composable function that displays a category with an image and a text label.
 * @param category the name of the category to display
 * @param image the image resource ID to display for the category
 * @param onClick a function to be called when the category is clicked
The category is displayed as a Surface with a circular shape, with the provided image inside it.
The text label for the category is displayed below the image. When the category is clicked,
the onClick function is called.
 */

@Composable
fun Category(category: String, image: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(50.dp)
                .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Category"
            )
        }
        Text(
            category,
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

/**
A Composable function that displays a book with its author and image.
 * @param bookAuthor the author of the book to display
 * @param bookTitle the title of the book to display
 * @param imageUrl the image resource ID to display for the book
 * @param onClick a function to be called when the book is clicked
The book is displayed as a Surface with a rounded corner shape, with the provided image inside it.
The book's title and author are displayed below the image. When the book is clicked, the onClick
function is called.
 */
@Composable
fun Reading(
    genre: String,
    bookAuthor: String,
    bookTitle: String,
    imageUrl: String,
    rating: String,
    onClick: () -> Unit
) {
    var loading by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(5.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = "Book Image",
                contentScale = ContentScale.Inside,
                onLoading = {
                    loading = true
                },
                onSuccess = {
                    loading = false
                }
            )
            if (loading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(25.dp),
                        color = Yellow
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    genre,
                    fontFamily = poppinsFamily,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    bookTitle,
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    "by $bookAuthor",
                    fontFamily = poppinsFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
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

/**
A Composable function that displays a navigation bar with clickable icons and labels.
The navigation bar consists of four items: Home, Shelves, Favourites, and Reviews, each with an
associated icon. The selected item is highlighted with the primary color, while the unselected
items are displayed with the default Material Design colors. When an item is clicked, the
selectedItem variable is updated to reflect the new selection.
 */
@Composable
fun NavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            name = "Home",
            route = BookShelfScreens.HomeScreen.name,
            icon = R.drawable.home,
        ),
        BottomNavItem(
            name = "Shelves",
            route = BookShelfScreens.ShelfScreen.name,
            icon = R.drawable.shelves,
        ),
        BottomNavItem(
            name = "Favourites",
            route = BookShelfScreens.FavouriteScreen.name,
            icon = R.drawable.favourite,
        ),
        BottomNavItem(
            name = "Reviews",
            route = BookShelfScreens.ReviewScreen.name,
            icon = R.drawable.reviews,
        ),
    )

    val backStackEntry = navController.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(30.dp)
                            .background(color = Color.Transparent)
                    )
                },
                label = {
                    Text(
                        item.name, fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                    )
                },
                selected = selected,
                onClick = {
                    when (item.name) {
                        "Home" -> navController.navigate(route = BookShelfScreens.HomeScreen.name)
                        "Shelves" -> navController.navigate(route = BookShelfScreens.ShelfScreen.name)
                        "Favourites" -> navController.navigate(route = BookShelfScreens.FavouriteScreen.name)
                        "Reviews" -> navController.navigate(route = BookShelfScreens.ReviewScreen.name)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)
                ),
                interactionSource = MutableInteractionSource()
            )
        }
    }
}

/**
A composable function that creates a search input field with an optional modifier.
 * @param modifier The modifier to apply to the input field. Defaults to [Modifier].
 * @param valueState The mutable state of the search input field's value.
 * @param labelId The string resource ID for the search input field's label.
 * @param enabled Determines if the search input field is enabled or not.
 * @param isSingleLine Determines if the search input field should only have a single line.
 * @param keyBoardType The keyboard type to be used with the search input field. Defaults to [KeyboardType.Ascii].
 * @param imeAction The IME action for the search input field. Defaults to [ImeAction.Done].
 * @param onAction The keyboard actions to perform with the search input field. Defaults to [KeyboardActions.Default].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    // Make a modifier optional
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean,
    keyBoardType: KeyboardType = KeyboardType.Ascii,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = {
            valueState.value = it
        },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Search",
                modifier = Modifier
                    .size(30.dp)
                    .background(color = Color.Transparent),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
            )
        },
        singleLine = isSingleLine,
        textStyle = TextStyle(fontSize = 14.sp, fontFamily = poppinsFamily),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyBoardType, imeAction = imeAction),
        keyboardActions = onAction,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = Yellow,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            selectionColors = TextSelectionColors(
                handleColor = Yellow,
                backgroundColor = Pink200
            ),
            placeholderColor = Gray500
        ),
    )
}

/**

A composable function that displays a search card with book details.
The card contains an image, book title, author, and a preview text.
Clicking the card triggers the onClick function.
 * @param bookTitle The title of the book.
 * @param bookAuthor The author of the book.
 * @param previewText The preview text of the book.
 * @param imageUrl The URL of the book image.
 * @param onClick The function to be executed when the card is clicked.
 */
@Composable
fun SearchCard(
    bookTitle: String,
    bookAuthor: String,
    previewText: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(5.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 5.dp),
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
    }
}

/**

Composable function that displays a history card.
 * @param text the text to be displayed in the card.
 * @param onClick a lambda function that is called when the card is clicked.
 */
@Composable
fun HistoryCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(30.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Yellow),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(3.dp),
        ) {
            Text(
                text = if (text.length > 15) text.substring(0, 15) + "..." else text,
                overflow = TextOverflow.Ellipsis,
                fontFamily = poppinsFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A composable function that displays an alert dialog with a title, details, a confirm button,
 * and a cancel button.
 * @param openDialog A boolean that represents whether the alert dialog should be displayed.
 * @param title A string representing the title of the alert dialog.
 * @param details A string representing the details of the alert dialog.
 * @param onDismiss A lambda function that is called when the user dismisses the alert dialog.
 * @param onClick A lambda function that is called when the user confirms the alert dialog.
 */
@Composable
fun ShelvesAlertDialog(
    openDialog: Boolean,
    title: String,
    details: String,
    drawable: Int,
    color: Color = Pink500,
    size: Dp = 30.dp,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    if (openDialog) {
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
            text = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = details,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp,
                    fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Left
                )
            },
            confirmButton = {
                TextButton(onClick = onClick) {
                    Text(
                        "Confirm",
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
            }
        )
    }
}