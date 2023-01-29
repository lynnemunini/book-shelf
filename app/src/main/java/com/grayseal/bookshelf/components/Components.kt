package com.grayseal.bookshelf.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grayseal.bookshelf.R
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
    Card(
        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(20.dp)
    ) {
        var color by remember {
            mutableStateOf(Gray500)
        }
        var icon by remember {
            mutableStateOf(Icons.Rounded.SentimentSatisfied)
        }
        OutlinedTextField(
            value = nameState.value,
            onValueChange = {
                color = Pink500
                nameState.value = it
                icon = Icons.Rounded.InsertEmoticon
            },
            placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = "Name Icon",
                    tint = color
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFamily,
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            ),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Yellow,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
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
    Card(
        modifier = Modifier.padding(bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(20.dp)
    ) {
        var color by remember {
            mutableStateOf(Gray500)
        }
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = {
                color = Pink500
                passwordState.value = it
                error = passwordState.value.length < 6
            },
            placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Lock Icon",
                    tint = color
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFamily,
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth(),
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
                cursorColor = Yellow,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
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
                imageVector = Icons.Rounded.Visibility,
                contentDescription = "Visibility Icon",
                tint = Gray500
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.VisibilityOff,
                contentDescription = "Visibility Icon",
                tint = Gray500
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
    Card(
        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(20.dp)
    ) {
        var color by remember {
            mutableStateOf(Gray500)
        }
        OutlinedTextField(
            value = valueState.value,
            onValueChange = {
                color = Pink500
                valueState.value = it
                error = !isValidEmail(valueState.value)
            },
            placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.AlternateEmail,
                    contentDescription = "Email Icon",
                    tint = color
                )
            },
            singleLine = isSingleLine,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFamily,
                color = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            keyboardActions = KeyboardActions {
                keyboardController?.hide()
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Yellow,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        androidx.compose.material3.Button(
            onClick = onClick, modifier = Modifier
                .fillMaxWidth(),
            enabled = !loading && validInputs, shape = RoundedCornerShape(10.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Pink500
            )
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp)) else
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
                        color = Gray500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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


@Composable
fun Category(category: String, image: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(50.dp)
                .background(color = Pink500, shape = CircleShape),
            shape = CircleShape,
            color = Pink200
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
            color = Gray700.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
fun Reading(bookAuthor: String, bookTitle: String, image: Int) {
    Column {
        Surface(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .weight(1f, fill = false)
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Book Image",
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            bookTitle,
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = Gray700.copy(alpha = 0.8f),
        )
        Text(
            bookAuthor,
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            color = Gray700.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
fun NavBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Shelves", "Favourites", "Reviews")
    val navBarItems = mapOf(
        items[0] to Icons.Rounded.Home,
        items[1] to Icons.Rounded.MenuBook,
        items[2] to Icons.Rounded.FavoriteBorder,
        items[3] to Icons.Rounded.Reviews
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    navBarItems[item]?.let {
                        Icon(
                            it,
                            contentDescription = item,
                        )
                    }
                },
                label = {
                    Text(
                        item, fontFamily = poppinsFamily,
                        fontSize = 12.sp,
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Purple500,
                    selectedTextColor = Purple500,
                    unselectedTextColor = Gray700.copy(alpha = 0.4f),
                    unselectedIconColor = Gray700.copy(alpha = 0.4f),
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)
                )
            )
        }
    }
}