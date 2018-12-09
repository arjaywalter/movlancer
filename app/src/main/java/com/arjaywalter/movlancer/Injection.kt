/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arjaywalter.movlancer

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.arjaywalter.movlancer.data.MovieDataFactory
import com.arjaywalter.movlancer.data.MovieDataSource
import com.arjaywalter.movlancer.db.MovieLocalCache
import com.arjaywalter.movlancer.db.MovieDatabase
import com.arjaywalter.movlancer.ui.ViewModelFactory
import com.arjaywalter.movlancer.api.MovieService
import com.example.android.codelabs.paging.data.MovieRepository
import java.util.concurrent.Executors

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
object Injection {
    /**
     * Creates an instance of [MovieDataSource] based on the [MovieService]
     */
    private fun provideFeedDataSource(context: Context): MovieDataSource {
        val database = MovieDatabase.getInstance(context)
        return MovieDataSource(MovieService.create(), database.moviesDao(), Executors.newSingleThreadExecutor())
    }
    /**
     * Creates an instance of [MovieDataSource] based on the [MovieService]
     */
    private fun provideFeedDataFactory(context: Context): MovieDataFactory {
        return MovieDataFactory(provideFeedDataSource(context))
    }
    /**
     * Creates an instance of [MovieLocalCache] based on the database DAO.
     */
    private fun provideCache(context: Context): MovieLocalCache {
        val database = MovieDatabase.getInstance(context)
        return MovieLocalCache(database.moviesDao(), Executors.newSingleThreadExecutor())
    }

    /**
     * Creates an instance of [MovieRepository] based on the [MovieService] and a
     * [MovieLocalCache]
     */
    private fun provideMovieRepository(context: Context): MovieRepository {
        return MovieRepository(MovieService.create(), provideCache(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideMovieRepository(context), provideFeedDataFactory(context))
    }

}