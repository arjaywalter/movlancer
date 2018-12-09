package com.arjaywalter.movlancer.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.arjaywalter.movlancer.Injection
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.R
import com.arjaywalter.movlancer.utils.NetworkState
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val movieClickListener = object : (Movie) -> Unit {
        override fun invoke(it: Movie) {

        }
    }

    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MovieListAdapter(movieClickListener)
        recyclerView.adapter = adapter


        // Get the ViewModel.
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory(this)).get(MovieViewModel::class.java)


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.moviesPagedList?.observe(this, Observer<PagedList<Movie>> { movies ->
            // Update the UI
            adapter.submitList(movies)
        })

        viewModel.networkState?.observe(this, Observer<NetworkState> { networkState ->
            networkState?.let { adapter.setNetworkState(it) }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }
}
