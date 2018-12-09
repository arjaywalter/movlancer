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

package com.arjaywalter.movlancer.db

import android.arch.lifecycle.LiveData
import android.util.Log
import com.arjaywalter.movlancer.model.Movie
import java.util.concurrent.Executor

/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 */
class MovieLocalCache(
        private val movieDao: MovieDao,
        private val ioExecutor: Executor
) {

    /**
     * Insert a list of repos in the database, on a background thread.
     */
    fun insert(repos: List<Movie>, insertFinished: ()-> Unit) {
        ioExecutor.execute {
            Log.d("MovieLocalCache", "inserting ${repos.size} repos")
            movieDao.insert(repos)
            insertFinished()
        }
    }

    /**
     * Request a LiveData<List<Movie>> from the Dao, based on a name. If the name contains
     * multiple words separated by spaces, then we're emulating the API behavior and allow
     * any characters between the words.
     * @param name repository name
     */
    fun reposByName(name: String): LiveData<List<Movie>> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${name.replace(' ', '%')}%"
        return movieDao.moviesByTitle(query)
    }

    fun movies(): LiveData<List<Movie>> {
        return movieDao.getMovies()
    }}