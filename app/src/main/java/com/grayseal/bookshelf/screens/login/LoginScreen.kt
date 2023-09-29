package com.grayseal.bookshelf.screens.login

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.components.EmailInput
import com.grayseal.bookshelf.components.NameInput
import com.grayseal.bookshelf.components.PasswordInput
import com.grayseal.bookshelf.components.SubmitButton
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Gray500
import com.grayseal.bookshelf.ui.theme.Yellow
import com.grayseal.bookshelf.ui.theme.poppinsFamily
import com.grayseal.bookshelf.utils.isValidEmail
import kotlinx.coroutines.launch

/**
Composable function to display a login screen.
 * @param navController The NavController used to navigate to different screens.
 * @param launcher The ManagedActivityResultLauncher used to handle activity results.
 * @param viewModel The viewModel containing logic for login and creating a user.
 * @param dataStore The dataStore used to store and retrieve user information.
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

    // Create a local mutable state to hold the value of the loading flag
    var loading by remember { mutableStateOf(false) }

    // Observe the value of the loading LiveData and update the local state accordingly
    viewModel.loading.observeForever {
        loading = it
    }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background_no_bg),
            contentDescription = "backgroundImage",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            val text: String = if (showLoginForm.value) {
                "Welcome Back"
            } else {
                "Create Account"
            }
            Text(
                text,
                fontFamily = poppinsFamily,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                    .clip(RoundedCornerShape(10.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (showLoginForm.value) UserForm(
                    showLoginForm = showLoginForm,
                    launcher = launcher,
                    dataStore = dataStore,
                    loading = loading,
                    isCreateAccount = false
                ) { email, password ->
                    // Login to Firebase Account
                    viewModel.signInWithEmailAndPassword(email, password,
                        home = { navController.navigate(BookShelfScreens.HomeScreen.name) },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                else {
                    UserForm(
                        showLoginForm = showLoginForm,
                        launcher = launcher,
                        dataStore = dataStore,
                        loading = loading,
                        isCreateAccount = true
                    ) { email, password ->
                        // Create FireBase Account
                        viewModel.createUserWithEmailAndPassword(email, password,
                            home = {
                                navController.navigate(BookShelfScreens.HomeScreen.name)
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
Composable function to display a user form for login or account creation.
 * @param showLoginForm A MutableState that holds a boolean indicating whether the login form should be shown or not.
 * @param launcher The ManagedActivityResultLauncher used to handle activity results.
 * @param dataStore The dataStore used to store and retrieve user information.
 * @param loading A boolean indicating whether the form is in loading state or not.
 * @param isCreateAccount A boolean indicating whether the form is for account creation or login.
 * @param onDone A function to be called when the form is submitted, takes in email and password as arguments.
 */
@Composable
fun UserForm(
    showLoginForm: MutableState<Boolean>,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    dataStore: StoreUserName,
    loading: Boolean,
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

    val valid: Boolean = if (!isCreateAccount) {
        remember(email.value, password.value) {
            (email.value.trim().isNotEmpty()
                    && password.value.trim().isNotEmpty()
                    && password.value.length >= 6) && isValidEmail(email.value)
        }
    } else {
        remember(email.value, password.value, name.value) {
            (email.value.trim().isNotEmpty()
                    && password.value.trim().isNotEmpty()
                    && name.value.trim().isNotEmpty()
                    && password.value.length >= 6) && isValidEmail(email.value)
        }
    }

    val token = stringResource(id = R.string.default_web_client_id)
    val context = LocalContext.current

    // a coroutine scope
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (isCreateAccount) {
                NameInput(nameState = name, enabled = !loading)
            }
            EmailInput(emailState = email, enabled = !loading)
            PasswordInput(
                modifier = Modifier.focusRequester(passwordFocusRequest),
                passwordState = password,
                labelId = "Password",
                enabled = !loading,
                passwordVisibility = passwordVisibility,
            )
            if (isCreateAccount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                    color = Yellow,
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
                                    color = Yellow,
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
                /*Text(
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
                }*/
                Row(
                    modifier = Modifier.padding(top = 8.dp, bottom = 50.dp),
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
                        color = MaterialTheme.colorScheme.primary,
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


