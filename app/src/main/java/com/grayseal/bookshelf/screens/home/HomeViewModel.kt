package com.grayseal.bookshelf.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.MyUser

class HomeViewModel : ViewModel() {
    var books: MutableState<MutableList<Book>> = mutableStateOf(mutableListOf())

    // suspend function to get book in the reading list
    fun getBookInReadingList(
        userId: String?,
        context: Context,
        onDone: () -> Unit
    ): MutableList<Book> {
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val shelves = documentSnapshot.toObject<MyUser>()?.shelves
                    if (shelves != null) {
                        val shelf = shelves.find { it.name == "Reading Now 📖" }
                        if (shelf != null) {
                            books.value = shelf.books as MutableList<Book>
                        } else {
                            Toast.makeText(
                                context,
                                "Error fetching reading List",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error fetching reading List",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                onDone()
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "$it",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return books.value
    }
}