package com.grayseal.bookshelf.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.navigation.BookShelfScreens
import kotlinx.coroutines.delay

/**
A composable function that represents a splash screen in the app.
 * The splash screen is displayed while the app is starting up, and is responsible for animating
 * the logo image and transitioning to the HomeScreen. The animation is triggered when the function
 * is called, and lasts for 2 seconds before transitioning to the HomeScreen. This function uses the
 * navController to navigate between screens.
 * @param navController The NavController used to navigate between screens.
 */
@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember {
        mutableStateOf(false)
    }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000
        )
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000L)
        navController.popBackStack()
        navController.navigate(route = BookShelfScreens.HomeScreen.name)
    }
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.alpha(alphaAnimation.value),
                painter = painterResource(id = R.drawable.book),
                contentDescription = "Splash Book",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}