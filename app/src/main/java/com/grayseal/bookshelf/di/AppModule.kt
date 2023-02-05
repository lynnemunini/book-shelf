package com.grayseal.bookshelf.di

import com.grayseal.bookshelf.model.Book
import com.grayseal.bookshelf.network.BooksAPI
import com.grayseal.bookshelf.repository.BookRepository
import com.grayseal.bookshelf.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBookRepository(api: BooksAPI) = BookRepository(api)

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