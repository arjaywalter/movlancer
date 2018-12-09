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

package com.example.android.codelabs.paging.data

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.arjaywalter.movlancer.db.MovieLocalCache
import com.arjaywalter.movlancer.model.MovieSearchResult
import com.arjaywalter.movlancer.api.MovieService
import com.arjaywalter.movlancer.api.getMovies
import com.arjaywalter.movlancer.api.searchMovies

/**
 * Repository class that works with local and remote data sources.
 */
class MovieRepository(private val service: MovieService,
                      private val cache: MovieLocalCache) {

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // LiveData of network errors.
    private val networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    /**
     * Search repositories whose names match the query.
     */
    fun search(query: String): MovieSearchResult {
        Log.d("MovieRepository", "New query: $query")
        lastRequestedPage = 1
        requestAndSaveData(query)

        // Get data from the local cache
        val data = cache.reposByName(query)

        return MovieSearchResult(data, networkErrors)
    }


    fun requestMore(query: String) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchMovies(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    fun fetchMovies(): MovieSearchResult {
        lastRequestedPage = 1
        fetchAndSaveData()

        // Get data from the local cache
        val data = cache.movies()

        return MovieSearchResult(data, networkErrors)
    }

    private fun fetchAndSaveData() {
        if (isRequestInProgress) return

        isRequestInProgress = true
        getMovies(service, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos, {
                lastRequestedPage++
                isRequestInProgress = false
            })
        }, { error ->
            networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}