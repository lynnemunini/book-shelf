package com.grayseal.bookshelf.screens.login

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val context = getApplication<Application>().applicationContext
    private val dataStore = StoreUserName(context)
    val displayName = dataStore.getName

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        if(_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    home()
                }
                    else{
                    Log.d("TAG", "createUserWithEmailAndPassword: ${task.result}")
                }
                    _loading.value = false
                    
                }
        }

    }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithEmailAndPassword: SUCCESS!! ${task.result.toString()}")
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