package com.grayseal.bookshelf.screens.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.MyUser
import com.grayseal.bookshelf.model.Review
import com.grayseal.bookshelf.model.Shelf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**

The LoginScreenViewModel class is responsible for handling the login and registration process for the application. It uses FirebaseAuth to authenticate and create users, and it also uses a StateFlow and MutableLiveData to keep track of the user's name and loading status.
 * @param application the application context
 * @property auth the FirebaseAuth instance used for authentication
 * @property _loading a MutableLiveData object that keeps track of the loading status
 * @property loading a LiveData object that is exposed for observing the loading status
 * @property context the application context
 * @property dataStore a StoreUserName object that is used to store the user's name
 * @property _name a MutableStateFlow object that keeps track of the user's name
 * @property name a StateFlow object that is exposed for observing the user's name
 * @constructor Creates a new LoginScreenViewModel instance with the specified application context.
 * @function createUserWithEmailAndPassword creates a new user with the specified email and password, and also creates a new user in the FirebaseFirestore database with the specified display name.
 * @param email the email of the new user
 * @param password the password of the new user
 * @param home a lambda function that is called when the user is successfully created
 * @function createUser creates a new user in the FirebaseFirestore database with the specified display name.
 * @param displayName the display name of the new user
 * @function signInWithEmailAndPassword signs in the user with the specified email and password, and calls the specified lambda function when the sign in is successful.
 * @param email the email of the user
 * @param password the password of the user
 * @param home a lambda function that is called when the user is successfully signed in
 */
class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    // TODO FIX: This field leaks a context object
    private val context = getApplication<Application>().applicationContext
    private val dataStore = StoreUserName(context)

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    init {
        viewModelScope.launch {
            dataStore.getName
                .onEach { value ->
                    if (value != null) {
                        _name.value = value
                    }
                }
                .launchIn(this)
        }
    }

    /**

    Creates a new user with the provided email and password.
     * @author Lynne Munini
     * @param email The email to be associated with the new user account.
     * @param password The password for the new user account.
     * @param home A function to be called upon successful creation of the user account.
     */
    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = name.value
                        createUser(displayName)
                        home()
                    } else {
                        Log.e("TAG", "createUserWithEmailAndPassword: ${task.result}")
                    }
                    _loading.value = false
                }
        }
    }

    /**

    createUser is a private function that creates a new user in Firebase Firestore.
     * @param displayName a string representing the display name of the user.
    The function first gets the userId of the current user from the auth object.
    Then, it creates a new MyUser object.
    Finally, it adds the user object to the "users" collection in Firebase Firestore.
     */
    private fun createUser(displayName: String) {
        val userId = Firebase.auth.currentUser?.uid
        val readingList: List<Book> = mutableListOf(
        )
        val shelves: List<Shelf> = mutableListOf(
            Shelf("reading", readingList)
        )
        val searchHistory: MutableList<String> = mutableListOf()
        val reviews: List<Review> = mutableListOf()
        val favourites: List<Book> = readingList
        if (userId != null) {
            val user = MyUser(
                userID = userId.toString(),
                displayName = displayName,
                avatar = "",
                shelves = shelves,
                searchHistory = searchHistory,
                reviews = reviews,
                favourites = favourites
            ).toMap()
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.set(user)
        }
    }

    /**
    signInWithEmailAndPassword is a function that allows a user to sign in to their account using their email and password.
     * @param email: String, the user's email address
     * @param password: String, the user's password
     * @param home: () -> Unit, a lambda function that represents the action to be taken when the sign in is successful.
     * @return None
     */
    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit,
        onError: (String) -> Unit
    ) =
        viewModelScope.launch {
            try {
                _loading.value = true
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // HomeScreen
                            home()
                        }
                        _loading.value = false
                    }
                    .addOnFailureListener { exception ->
                        // Handle network errors, including timeouts
                        when (exception) {
                            is FirebaseAuthException -> {
                                when (exception.errorCode) {
                                    "ERROR_USER_NOT_FOUND" -> {
                                        onError("Email does not exist")
                                    }
                                    "ERROR_WRONG_PASSWORD" -> {
                                        onError("Incorrect email or password")
                                    }
                                    else -> {
                                        onError("Unknown error: ${exception.localizedMessage}")
                                    }
                                }
                            }
                            is FirebaseNetworkException -> {
                                onError("Network error: ${exception.localizedMessage}")
                            }
                            else -> {
                                onError("Unknown error: ${exception.localizedMessage}")
                            }
                        }
                        _loading.value = false
                    }
            } catch (e: RuntimeExecutionException) {
                Log.e("TAG", "signInWithEmailAndPassword: ${e.message}")
            }
        }
}