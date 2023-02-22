package com.grayseal.bookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.grayseal.bookshelf.navigation.BookShelfNavigation
import com.grayseal.bookshelf.ui.theme.BookShelfTheme
import dagger.hilt.android.AndroidEntryPoint

/**

An Android activity that serves as the main entry point for the app.
The activity extends [ComponentActivity], which provides a base class for activities that enables integration with Hilt.
The activity is annotated with [@AndroidEntryPoint] to allow Hilt to provide dependencies to the activity and its related classes.
The [onCreate] method is overridden to set the content view of the activity to the top-level UI element [MyApp], which is the entry point of the Compose UI hierarchy.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

/**

A composable function that represents the main entry point of the BookShelf application.
It sets up the application theme using [BookShelfTheme], creates a surface that fills the entire
screen and adds the [BookShelfNavigation] composable that handles the app's navigation logic.
 */
@Composable
fun MyApp() {
    BookShelfTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BookShelfNavigation()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}