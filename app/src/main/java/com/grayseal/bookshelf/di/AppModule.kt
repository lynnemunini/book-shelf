package com.grayseal.bookshelf.di

import com.grayseal.bookshelf.network.BooksAPI
import com.grayseal.bookshelf.repository.BookRepository
import com.grayseal.bookshelf.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
A Dagger module that provides dependencies for the BookShelf app.
This module provides a singleton instance of the BooksAPI interface and a BookRepository object that depends on BooksAPI.
*/
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
    A Dagger provider method that provides a singleton instance of the BookRepository class.
    * @param api the BooksAPI dependency to be injected into the BookRepository constructor.
    * @return a singleton instance of the BookRepository class that depends on BooksAPI.
    */
    @Singleton
    @Provides
    fun provideBookRepository(api: BooksAPI) = BookRepository(api)

    /**
    A Dagger provider method that provides a singleton instance of the BooksAPI interface.
    * @return a singleton instance of the BooksAPI interface created using a Retrofit builder.
     */
    @Singleton
    @Provides
    fun provideBookApi(): BooksAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksAPI::class.java)
    }
}