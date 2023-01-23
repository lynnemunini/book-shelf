package com.grayseal.bookshelf.screens.login

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.components.*
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Gray500
import com.grayseal.bookshelf.ui.theme.Pink500
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import com.grayseal.bookshelf.utils.isValidEmail
import kotlinx.coroutines.launch

/**

Composable function to display a login screen.

@param navController The NavController used to navigate to different screens.

@param launcher The ManagedActivityResultLauncher used to handle activity results.

@param viewModel The viewModel containing logic for login and creating a user.

@param dataStore The dataStore used to store and retrieve user information.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    viewModel: LoginScreenViewModel,
    dataStore: StoreUserName,
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 15.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showLoginForm.value) UserForm(
            showLoginForm = showLoginForm,
            launcher = launcher,
            dataStore = dataStore,
            textIntro = "Welcome Back,",
            textDesc = "Log in to continue",
            loading = false,
            isCreateAccount = false
        ) { email, password ->
            // Login to Firebase Account
            viewModel.signInWithEmailAndPassword(email, password) {
                navController.navigate(BookShelfScreens.HomeScreen.name)
            }
        }
        else {
            UserForm(
                showLoginForm = showLoginForm,
                launcher = launcher,
                dataStore = dataStore,
                textIntro = "Create Your Account",
                textDesc = "Sign up and get started",
                loading = false,
                isCreateAccount = true
            ) { email, password ->
                // Create FireBase Account
                viewModel.createUserWithEmailAndPassword(email, password) {
                    navController.navigate(BookShelfScreens.HomeScreen.name)
                }
            }
        }
    }
}

/**

Composable function to display a user form for login or account creation.

@param showLoginForm A MutableState that holds a boolean indicating whether the login form should be shown or not.

@param launcher The ManagedActivityResultLauncher used to handle activity results.

@param dataStore The dataStore used to store and retrieve user information.

@param textIntro The introduction text to be displayed above the form

@param textDesc The description text to be displayed above the form

@param loading A boolean indicating whether the form is in loading state or not.

@param isCreateAccount A boolean indicating whether the form is for account creation or login.

@param onDone A function to be called when the form is submitted, takes in email and password as arguments.
 */
@Composable
fun UserForm(
    showLoginForm: MutableState<Boolean>,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    dataStore: StoreUserName,
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
    val name = rememberSaveable {
        mutableStateOf("")
    }
    val passwordVisibility = rememberSaveable {
        mutableStateOf(false)
    }
    val passwordFocusRequest = FocusRequester.Default
    val valid = remember(email.value, password.value) {
        (email.value.trim().isNotEmpty()
                && password.value.trim().isNotEmpty()
                && password.value.length >= 6) && isValidEmail(email.value)
    }
    val token = stringResource(id = R.string.default_web_client_id)
    val context = LocalContext.current

    // a coroutine scope
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background), verticalArrangement = Arrangement.Center
    ) {
        Text(
            textIntro,
            fontFamily = poppinsFamily,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            textDesc,
            fontFamily = poppinsFamily,
            fontSize = 13.sp,
            color = Gray500
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCreateAccount) {
                Image(
                    painter = painterResource(id = R.drawable.loginillustration),
                    contentDescription = "Login Illustration",
                    modifier = Modifier.size(200.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.loginillustration),
                    contentDescription = "Login Illustration",
                    modifier = Modifier.size(300.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isCreateAccount) {
                    NameInput(nameState = name, enabled = !loading)
                }
                EmailInput(emailState = email, enabled = !loading)
                PasswordInput(modifier = Modifier.focusRequester(passwordFocusRequest),
                    passwordState = password,
                    labelId = "Password",
                    enabled = !loading,
                    passwordVisibility = passwordVisibility,
                    )
                if (isCreateAccount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Gray500,
                                        fontSize = 12.sp,
                                        fontFamily = poppinsFamily
                                    )
                                ) {
                                    append("By signing up, you agree to our ")
                                }
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
                                        color = Gray500,
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
                            }
                        )
                    }
                }
                SubmitButton(
                    textId = if (isCreateAccount) "Create Account" else "Log in",
                    loading = loading,
                    validInputs = valid
                ) {
                    if (isCreateAccount) {
                        onDone(email.value.trim(), password.value.trim())
                        name.value.trim()
                        // Instantiate the StoreUserName class
                        scope.launch {
                            dataStore.saveName(name.value)
                        }
                    } else {
                        onDone(email.value.trim(), password.value.trim())
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (isCreateAccount) "Or sign up with..." else "Or, log in with...",
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp,
                        color = Gray500,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                    Row(horizontalArrangement = Arrangement.Center) {
                        ContinueGoogle {
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(token)
                                    .requestEmail()
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    }
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val text = if (showLoginForm.value) "Sign Up" else "Log in"
                        val desc =
                            if (showLoginForm.value) "Don't have an account?" else "Already have an account?"
                        Text(
                            text = desc,
                            fontFamily = poppinsFamily,
                            fontSize = 14.sp,
                            color = Gray500
                        )
                        Text(text,
                            fontFamily = poppinsFamily,
                            fontSize = 14.sp,
                            color = Pink500,
                            modifier = Modifier
                                .clickable {
                                    showLoginForm.value = !showLoginForm.value
                                }
                                .padding(start = 5.dp),
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


