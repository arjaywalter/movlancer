package com.arjaywalter.movlancer.db

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.arjaywalter.movlancer.model.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getMovies(): LiveData<List<Movie>>

    // The Int type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM movies")
    fun movies(): DataSource.Factory<Int, Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Movie>)

    @Query("SELECT * FROM movies WHERE title LIKE :queryString")
    fun moviesByTitle(queryString: String): LiveData<List<Movie>>

    @Query("DELETE FROM movies")
    fun deleteAll()

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getMoviesMutable(): MutableList<Movie>
}
