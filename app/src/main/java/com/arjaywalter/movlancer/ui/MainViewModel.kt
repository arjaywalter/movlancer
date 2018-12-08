package com.arjaywalter.movlancer.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.model.MovieSearchResult
import com.example.android.codelabs.paging.data.MoviesRepository

class MainViewModel(private val repository: MoviesRepository) : ViewModel() {

    private val queryLiveData = MutableLiveData<String>()
    private val repoResult: LiveData<MovieSearchResult> = Transformations.map(queryLiveData, {
        repository.search(it)
    })
    val repos: LiveData<List<Movie>> = Transformations.switchMap(repoResult,
            { it -> it.data })
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult,
            { it -> it.networkErrors })

    private lateinit var movies: LiveData<List<Movie>>

    fun getMovies(): LiveData<List<Movie>> {
        if (!::movies.isInitialized) {
            movies = MutableLiveData()
            loadMovies()
        }
        return movies
    }

     fun loadMovies() {
        // Do an asynchronous operation to fetch movies.
         movies  = repository.fetchMovies().data
    }

    // Using the Paging library
//    val movieList: LiveData<PagedList<Movie>> =
//            LivePagedListBuilder(
//                    movieDao.movies(), /* page size */ 20).build()

}