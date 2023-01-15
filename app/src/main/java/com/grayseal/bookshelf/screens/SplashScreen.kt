package com.grayseal.bookshelf.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.navigation.BookShelfScreens
import com.grayseal.bookshelf.ui.theme.Purple500
import com.grayseal.bookshelf.ui.theme.Purple700
import com.grayseal.bookshelf.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var startAnimation by remember {
        mutableStateOf(false)
    }
    val alphaAnimation = animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(4000L)
        navController.popBackStack()
        navController.navigate(route = BookShelfScreens.HomeScreen.name)
    }
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Purple500),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.alpha(alphaAnimation.value),
                painter = painterResource(id = R.drawable.book),
                contentDescription = "Splash Book"
            )
        }
    }
}