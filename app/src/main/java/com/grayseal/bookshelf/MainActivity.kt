package com.grayseal.bookshelf

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.firestore.FirebaseFirestore
import com.grayseal.bookshelf.ui.theme.BookShelfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookShelfTheme {
                val db = FirebaseFirestore.getInstance()
                val user: MutableMap<String, Any> = HashMap()
                user["firstName"] = "Gray"
                user["lastName"] = "Seal"
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            Log.d("DB", "onCreate: ${it.id}")
                        }.addOnFailureListener{
                            Log.d("DB", "onFail: $it")
                        }
                    Greeting("Welcome to your Book Shelf")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BookShelfTheme {
        Greeting("Android")
    }
}