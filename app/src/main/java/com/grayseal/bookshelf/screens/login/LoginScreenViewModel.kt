package com.grayseal.bookshelf.screens.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
     * @param onError: (String) -> Unit, a lambda function that represents the action to be taken when an error occurs.
     */
    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val displayName = name.value
                            createUser(displayName)
                            // HomeScreen
                            home()
                        }
                        _loading.value = false
                    }
                    .addOnFailureListener { exception ->
                        // Handle network errors, including timeouts
                        when (exception) {
                            is FirebaseAuthException -> {
                                if (exception.message?.contains("email address is already in use") == true) {
                                    onError("Email already exists")
                                } else {
                                    onError("Unknown error: ${exception.localizedMessage}")
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
            } catch (e: Exception) {
                // Log.e("TAG", "signInWithEmailAndPassword: ${e.message}")
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
        val bookList: MutableList<Book> = mutableListOf(
        )
        val shelves: MutableList<Shelf> = mutableListOf(
            Shelf("Reading Now 📖", bookList),
            Shelf("To Read 📌", bookList),
            Shelf("Have Read 📚", bookList)
        )
        val searchHistory: MutableList<String> = mutableListOf()
        val reviews: MutableList<Review> = mutableListOf()
        val favourites: MutableList<Book> = mutableListOf()

        if (userId != null) {
            val user = MyUser(
                userID = userId.toString(),
                displayName = displayName,
                avatar = "Image not set",
                shelves = shelves,
                searchHistory = searchHistory,
                reviews = reviews,
                favourites = favourites
            )
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.set(user)
        }
    }

    /**
    signInWithEmailAndPassword is a function that allows a user to sign in to their account using their email and password.
     * @param email: String, the user's email address
     * @param password: String, the user's password
     * @param home: () -> Unit, a lambda function that represents the action to be taken when the sign in is successful.
     * @param onError: (String) -> Unit, a lambda function that represents the action to be taken when an error occurs.
     * @return None
     */
    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        home: () -> Unit,
        onError: (String) -> Unit
    ) {
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
            } catch (e: Exception) {
                // Log.e("TAG", "signInWithEmailAndPassword: ${e.message}")
            }
        }
    }
}