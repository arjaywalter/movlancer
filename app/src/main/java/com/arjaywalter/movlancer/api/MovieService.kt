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

package com.arjaywalter.movlancer.api

import android.util.Log
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.model.MovieResponse
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
 * Search based on a query.
 * @param query search keyword
 * @param page request page index
 * @param itemsPerPage number of items to be returned by the API per page
 *
 * The result of the request is handled by the implementation of the functions passed as params
 * @param onSuccess function that defines how to handle the list of repos received
 * @param onError function that defines how to handle request failure
 */
fun searchMovies(
        service: MovieService,
        query: String,
        page: Int,
        itemsPerPage: Int,
        onSuccess: (repos: List<Movie>) -> Unit,
        onError: (error: String) -> Unit) {
    Log.d(TAG, "query: $query, page: $page, itemsPerPage: $itemsPerPage")

    val apiQuery = query + IN_QUALIFIER

    service.searchRepos(apiQuery, page, itemsPerPage).enqueue(
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
     * Get popular movies.
     */
    @GET("movie/popular")
    fun getMovies(
            @Query("page") page: Int,
            @Query("api_key") apiKey: String? = API_KEY): Call<MovieResponse>


    /**
     * Search movies.
     */
    @GET("search/repositories?sort=stars")
    fun searchRepos(@Query("q") query: String,
                    @Query("page") page: Int,
                    @Query("per_page") itemsPerPage: Int): Call<MovieResponse>


    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "e92fb0a1dede792e7f761056e036978c"
        var POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

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