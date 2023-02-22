package com.grayseal.bookshelf

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**

This class represents the BookShelf Application class.
It is annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class BookShelfApplication: Application()