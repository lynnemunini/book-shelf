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
import com.grayseal.bookshelf.model.Review
import com.grayseal.bookshelf.model.Shelf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ShelfViewModel : ViewModel() {
    var booksInShelf: MutableState<MutableList<Book>> = mutableStateOf(mutableListOf())
    var shelves: MutableState<List<Shelf>> = mutableStateOf(mutableListOf())
    var favourites: MutableState<List<Book>> = mutableStateOf(mutableListOf())
    var reviews: MutableState<List<Review>> = mutableStateOf(mutableListOf())

    // Get books in a particular shelf
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

    // Get shelves belonging to a particular user
    fun getShelves(
        userId: String?,
        context: Context,
        onDone: () -> Unit
    ): List<Shelf> {
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userShelves = documentSnapshot.toObject<MyUser>()?.shelves
                    if (userShelves != null) {
                        shelves.value = userShelves
                    } else {
                        Toast.makeText(context, "Error fetching Shelves", Toast.LENGTH_SHORT)
                            .show()
                    }
                    onDone()
                }
            }
        }
        return shelves.value
    }

    // Delete a book from a shelf
    suspend fun deleteABookInShelf(userId: String?, book: Book, shelfName: String?): Boolean = withContext(Dispatchers.IO){
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userShelves =
                        documentSnapshot.toObject<MyUser>()?.shelves as MutableList<Shelf>
                    val shelf = userShelves.find { it.name == shelfName }
                    if (shelf != null) {
                        val books = shelf.books as MutableList<Book>
                        books.remove(book)
                        shelf.books = books
                        // Update shelves
                        val index = userShelves.indexOfFirst { it.name == shelfName }
                        userShelves[index] = shelf
                        db.update("shelves", userShelves).await()
                        return@withContext true
                    }
                }
            }
        }
        return@withContext false
    }

    suspend fun addFavourite(
        userId: String?,
        book: Book,
    ): Boolean = withContext(Dispatchers.IO){
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val favourites =
                        documentSnapshot.toObject<MyUser>()?.favourites as MutableList<Book>
                    favourites.add(book)
                    db.update("favourites", favourites).await()
                    return@withContext true
                }
            }
        }
        return@withContext false
    }

    // Remove book from favourites
    suspend fun removeFavourite(
        userId: String?,
        book: Book,
    ): Boolean = withContext(Dispatchers.IO){
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val favourites =
                        documentSnapshot.toObject<MyUser>()?.favourites as MutableList<Book>
                    favourites.remove(book)
                    db.update("favourites", favourites).await()
                    return@withContext true
                }
            }
        }
        return@withContext false
    }

    // Fetch favourites
    fun fetchFavourites(
        userId: String?,
        onDone: () -> Unit
    ): List<Book> {
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    favourites.value =
                        documentSnapshot.toObject<MyUser>()?.favourites as MutableList<Book>
                    onDone()
                }
            }
        }
        return favourites.value
    }

    // Reviews

    // Add a review to firestore
    suspend fun addReview(
        userId: String?,
        review: Review,
    ): Boolean = withContext(Dispatchers.IO){
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val reviews =
                        documentSnapshot.toObject<MyUser>()?.reviews as MutableList<Review>
                    reviews.add(review)
                    db.update("reviews", reviews).await()
                    return@withContext true
                }
            }
        }
        return@withContext false
    }

    // Delete review from firestore
    suspend fun removeReview(
        userId: String?,
        review: Review,
    ): Boolean = withContext(Dispatchers.IO){
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val reviews =
                        documentSnapshot.toObject<MyUser>()?.reviews as MutableList<Review>
                    reviews.remove(review)
                    db.update("reviews", reviews).await()
                    return@withContext true
                }
            }
        }
        return@withContext false
    }

    // Fetch reviews
    fun fetchReviews(
        userId: String?,
        onDone: () -> Unit
    ): List<Review> {
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    reviews.value =
                        documentSnapshot.toObject<MyUser>()?.reviews as MutableList<Review>
                    onDone()
                }
            }
        }
        return reviews.value
    }

    // Update review
    suspend fun updateReview(
        userId: String?,
        review: Review,
        onDone: () -> Unit
    ): Boolean = withContext(Dispatchers.IO){
        if (userId != null) {
            val db = FirebaseFirestore.getInstance().collection("users").document(userId)
            db.get().await().let { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val reviews =
                        documentSnapshot.toObject<MyUser>()?.reviews as MutableList<Review>
                    val index = reviews.indexOfFirst { it.book.bookID == review.book.bookID }
                    reviews[index] = review
                    db.update("reviews", reviews).await()
                    onDone()
                    return@withContext true
                }
            }
        }
        return@withContext false
    }
}