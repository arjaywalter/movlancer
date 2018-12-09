package com.arjaywalter.movlancer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.arjaywalter.movlancer.data.MovieDataFactory
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.utils.NetworkState
import com.example.android.codelabs.paging.data.MovieRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MovieViewModel(private val repository: MovieRepository, feedDataFactory: MovieDataFactory) : ViewModel() {

    private val executor: Executor = Executors.newFixedThreadPool(5)
    var networkState: LiveData<NetworkState>? = null
    var moviesPagedList: LiveData<PagedList<Movie>>? = null

    init {

        networkState = Transformations.switchMap(feedDataFactory.mutableLiveData
        ) { dataSource -> dataSource.networkState }

        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .setPageSize(20).build()

        moviesPagedList = LivePagedListBuilder(feedDataFactory, pagedListConfig)
                .setFetchExecutor(executor)
                .build()
    }

//    private val queryLiveData = MutableLiveData<String>()
//    private val repoResult: LiveData<MovieSearchResult> = Transformations.map(queryLiveData, {
//        repository.search(it)
//    })
////    val repos: LiveData<List<Movie>> = Transformations.switchMap(repoResult,
////            { it -> it.data })
//
//    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult,
//            { it -> it.networkErrors })
//
//    private lateinit var movies: LiveData<PagedList<Movie>>
//
//    fun getMovies(): LiveData<PagedList<Movie>> {
//        if (!::movies.isInitialized) {
//            movies = MutableLiveData()
//            loadMovies()
//        }
//        return movies
//    }
//
//    fun loadMovies() {
//        // Do an asynchronous operation to fetch movies.
//        movies = repository.fetchMovies().data
//    }


}