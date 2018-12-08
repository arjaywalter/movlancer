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

package com.example.android.codelabs.paging.api

import android.util.Log
import com.arjaywalter.movlancer.api.MovieResponse
import com.arjaywalter.movlancer.model.Movie
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val TAG = "MovieService"
private const val IN_QUALIFIER = "in:name,description"

/**
 * Search repos based on a query.
 * Trigger a request to the Github searchRepo API with the following params:
 * @param query searchRepo keyword
 * @param page request page index
 * @param itemsPerPage number of repositories to be returned by the Github API per page
 *
 * The result of the request is handled by the implementation of the functions passed as params
 * @param onSuccess function that defines how to handle the list of repos received
 * @param onError function that defines how to handle request failure
 */
fun searchRepos(
        service: MovieService,
        query: String,
        page: Int,
        itemsPerPage: Int,
        onSuccess: (repos: List<Movie>) -> Unit,
        onError: (error: String) -> Unit) {
    Log.d(TAG, "query: $query, page: $page, itemsPerPage: $itemsPerPage")

    val apiQuery = query + IN_QUALIFIER

    service.searchRepos(apiQuery, page, itemsPerPage).enqueue(
            object : Callback<RepoSearchResponse> {
                override fun onFailure(call: Call<RepoSearchResponse>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<RepoSearchResponse>?,
                        response: Response<RepoSearchResponse>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val repos = response.body()?.items ?: emptyList()
                        onSuccess(repos)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )
}


fun getMovies(
        service: MovieService,
        page: Int,
        itemsPerPage: Int,
        onSuccess: (repos: List<Movie>) -> Unit,
        onError: (error: String) -> Unit) {
    Log.d(TAG, "page: $page, itemsPerPage: $itemsPerPage")

//    val apiQuery = query + IN_QUALIFIER

    service.getMovies(page = page).enqueue(
            object : Callback<MovieResponse> {
                override fun onFailure(call: Call<MovieResponse>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieResponse>?,
                        response: Response<MovieResponse>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val repos = response.body()?.results ?: emptyList()
                        onSuccess(repos)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )
}


/**
 * Github API communication setup via Retrofit.
 */
interface MovieService {
    /**
     * Get repos ordered by stars.
     */
    @GET("search/repositories?sort=stars")
    fun searchRepos(@Query("q") query: String,
                    @Query("page") page: Int,
                    @Query("per_page") itemsPerPage: Int): Call<RepoSearchResponse>

    @GET("movie/popular")
    fun getMovies(
            @Query("page") page: Int,
            @Query("api_key") apiKey: String? = API_KEY): Call<MovieResponse>


    companion object {
        //        private const val BASE_URL = "https://api.github.com/"
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        private const val API_KEY = "e92fb0a1dede792e7f761056e036978c"

        fun create(): MovieService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MovieService::class.java)
        }
    }
}