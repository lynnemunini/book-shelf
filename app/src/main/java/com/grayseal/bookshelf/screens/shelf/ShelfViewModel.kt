package com.grayseal.bookshelf.screens.shelf

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.model.MyUser

class ShelfViewModel : ViewModel() {
    var booksInShelf: MutableState<MutableList<Book>> = mutableStateOf(mutableListOf())

    // Get books in a articular shelf
    fun getBooksInAShelf(
        userId: String?,
        context: Context,
        shelfName: String,
        onDone: () -> Unit
    ): MutableList<Book> {
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val shelves = documentSnapshot.toObject<MyUser>()?.shelves
                    if (shelves != null) {
                        val shelf = shelves.find { it.name == shelfName }
                        if (shelf != null) {
                            booksInShelf.value = shelf.books as MutableList<Book>
                        } else {
                            Toast.makeText(
                                context,
                                "Error fetching books",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error fetching books",
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
        return booksInShelf.value
    }
}