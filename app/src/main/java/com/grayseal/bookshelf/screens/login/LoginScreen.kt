package com.grayseal.bookshelf.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.components.EmailInput
import com.grayseal.bookshelf.components.PasswordInput
import com.grayseal.bookshelf.navigation.BookShelfNavigation
import com.grayseal.bookshelf.ui.theme.BookShelfTheme
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun LoginScreen(navController: NavHostController) {
    Text("You're in.")
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, password -> }
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val password = rememberSaveable {
        mutableStateOf("")
    }
    val passwordVisibility = rememberSaveable {
        mutableStateOf(false)
    }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val modifier = Modifier
        .height(250.dp)
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome Back,",
            fontFamily = poppinsFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Log in to continue",
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            color = Color.LightGray
        )
        Image(
            painter = painterResource(id = R.drawable.reading_book),
            contentDescription = "Login Illustration"
        )
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            })
            PasswordInput(modifier = Modifier.focusRequester(passwordFocusRequest),
                passwordState = password,
                labelId = "Password",
                enabled = !loading,
                passwordVisibility = passwordVisibility,
                onAction = KeyboardActions {
                    if (!valid) return@KeyboardActions
                    onDone(email.value.trim(), password.value.trim())
                })

        }
    }
}

