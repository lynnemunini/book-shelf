package com.grayseal.bookshelf.screens.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.grayseal.bookshelf.model.MyUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * gdfghjkjsxcvbkyutrdfmnbvcxfghjuytrsd
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


    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        if(_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    val displayName = name.value
                    Log.e("DISPLAY NAME", "createUserWithEmailAndPassword: $displayName" )
                    createUser(displayName)
                    home()
                }
                    else{
                    Log.e("TAG", "createUserWithEmailAndPassword: ${task.result}")
                }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String) {
        val userId = auth.currentUser?.uid
        val user = MyUser(userId = userId.toString(), displayName = displayName, avatarUrl = "", quote = "Yoh!", profession = "Android Developer", id = null).toMap()
        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // HomeScreen
                        home()
                    } else {
                        Log.e("TAG", "signInWithEmailAndPassword: ${task.result}")
                    }
                }
        } catch (e: java.lang.Exception) {
            Log.e("TAG", "signInWithEmailAndPassword: ${e.message}")
        }

    }
}