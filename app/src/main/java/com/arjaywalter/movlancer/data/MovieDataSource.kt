package com.arjaywalter.movlancer.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.util.Log

import com.arjaywalter.movlancer.model.MovieResponse
import com.arjaywalter.movlancer.api.MovieService
import com.arjaywalter.movlancer.api.MovieService.Companion.API_KEY
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.utils.NetworkState

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MovieDataSource(private val movieService: MovieService) : PageKeyedDataSource<Long, Movie>() {

    /*
     * Step 1: Initialize the restApiFactory.
     * The networkState and initialLoading variables
     * are for updating the UI when data is being fetched
     * by displaying a progress bar
     */

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val initialLoading: MutableLiveData<NetworkState> = MutableLiveData()

    /*
     * Step 2: This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Long>,
                             callback: PageKeyedDataSource.LoadInitialCallback<Long, Movie>) {

        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)

        movieService.getMovies(1, API_KEY)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                        if (response.isSuccessful) {
                            callback.onResult(response.body()!!.results, null, 2L)
                            initialLoading.postValue(NetworkState.LOADED)
                            networkState.postValue(NetworkState.LOADED)

                        } else {
                            initialLoading.postValue(NetworkState(NetworkState.Status.FAILED, response.message()))
                            networkState.postValue(NetworkState(NetworkState.Status.FAILED, response.message()))
                        }
                    }

                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        val errorMessage = if (t == null) "unknown error" else t.message
                        networkState.postValue(NetworkState(NetworkState.Status.FAILED, errorMessage!!))
                    }
                })
    }


    override fun loadBefore(params: PageKeyedDataSource.LoadParams<Long>,
                            callback: PageKeyedDataSource.LoadCallback<Long, Movie>) {

    }


    /*
     * Step 3: This method it is responsible for the subsequent call to load the data page wise.
     * This method is executed in the background thread
     * We are fetching the next page data from the api
     * and passing it via the callback method to the UI.
     * The "params.key" variable will have the updated value.
     */
    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Long>,
                           callback: PageKeyedDataSource.LoadCallback<Long, Movie>) {

        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize)

        networkState.postValue(NetworkState.LOADING)

        movieService.getMovies(params.key.toInt(), API_KEY).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                /*
                 * If the request is successful, then we will update the callback
                 * with the latest feed items and
                 * "params.key+1" -> set the next key for the next iteration.
                 */
                if (response.isSuccessful) {
                    val nextKey = if (params.key == response.body()?.totalResults?.toLong()) null else params.key + 1
                    callback.onResult(response.body()!!.results, nextKey)
                    networkState.postValue(NetworkState.LOADED)

                } else
                    networkState.postValue(NetworkState(NetworkState.Status.FAILED, response.message()))
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                val errorMessage = if (t == null) "unknown error" else t.message
                networkState.postValue(NetworkState(NetworkState.Status.FAILED, errorMessage!!))
            }
        })
    }

    companion object {

        private val TAG = MovieDataSource::class.java.simpleName
    }
}
