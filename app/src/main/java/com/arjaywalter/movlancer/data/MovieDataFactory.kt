package com.arjaywalter.movlancer.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.arjaywalter.movlancer.model.Movie

class MovieDataFactory(private val feedDataSource: MovieDataSource) : DataSource.Factory<Long, Movie>() {

    val mutableLiveData: MutableLiveData<MovieDataSource> = MutableLiveData()

    override fun create(): DataSource<Long, Movie> {
        mutableLiveData.postValue(feedDataSource)
        return feedDataSource
    }
}