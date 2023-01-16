package com.grayseal.bookshelf.screens.login

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.components.EmailInput
import com.grayseal.bookshelf.components.PasswordInput
import com.grayseal.bookshelf.components.SubmitButton
import com.grayseal.bookshelf.navigation.BookShelfNavigation
import com.grayseal.bookshelf.ui.theme.BookShelfTheme
import com.grayseal.bookshelf.ui.theme.Pink500
import com.grayseal.bookshelf.ui.theme.poppinsFamily

@Composable
fun LoginScreen(navController: NavHostController) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showLoginForm.value) UserForm(
            textIntro = "Welcome Back",
            textDesc = "Log in to continue",
            loading = false,
            isCreateAccount = false
        ) { email, password ->
            // TODO: Login to Firebase Account
        }
        else {
            UserForm(
                textIntro = "Create Your Account",
                textDesc = "Sign up and get started",
                loading = false,
                isCreateAccount = true
            ) { email, password ->
                // TODO: Create FireBase Account
            }
        }
        Row(
            modifier = Modifier.padding(15.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val text = if (showLoginForm.value) "Sign Up" else "Login"
            val desc =
                if (showLoginForm.value) "Don't have an account?" else "Already have an account?"
            Text(text = desc, fontFamily = poppinsFamily)
            Text(text, fontFamily = poppinsFamily, color = Pink500, modifier = Modifier
                .clickable {
                    showLoginForm.value = !showLoginForm.value
                }
                .padding(start = 5.dp), fontWeight = FontWeight.Bold)
        }
        if (!showLoginForm.value) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                fontFamily = poppinsFamily
                            )
                        ) {
                            append("By using this app, you agree to our ")
                        }
                    },
                    textAlign = TextAlign.Center
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Pink500,
                                fontSize = 12.sp,
                                fontFamily = poppinsFamily
                            )
                        ) {
                            append("Terms of Use ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                fontFamily = poppinsFamily
                            )
                        ) {
                            append("and ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Pink500,
                                fontSize = 12.sp,
                                fontFamily = poppinsFamily
                            )
                        ) {
                            append("Privacy Policy")
                        }
                    },
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserForm(
    textIntro: String,
    textDesc: String,
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
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background),
    ) {
        Text(
            textIntro,
            fontFamily = poppinsFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            textDesc,
            fontFamily = poppinsFamily,
            fontSize = 14.sp,
            color = Color.LightGray
        )
        Image(
            painter = painterResource(id = R.drawable.reading_book),
            contentDescription = "Login Illustration",
            modifier = Modifier.scale(0.8f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    keyboardController?.hide()
                })
            SubmitButton(
                textId = if (isCreateAccount) "Create Account" else "Login",
                loading = loading,
                validInputs = valid
            ) {
                onDone(email.value.trim(), password.value.trim())
                keyboardController?.hide()
            }

        }
    }
}


