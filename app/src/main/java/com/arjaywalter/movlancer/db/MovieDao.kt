package com.arjaywalter.movlancer.db

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import com.arjaywalter.movlancer.model.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getMovies(): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(vararg movies: Movie)

    @Update
    fun updateMovies(vararg movies: Movie)

    @Delete
    fun deleteMovies(vararg movies: Movie)

    @Query("SELECT * FROM movies")
    fun loadAllMovies(): Array<Movie>

    // The Int type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM movies")
    fun movies(): DataSource.Factory<Int, Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Movie>)

    // Do a similar query as the search API:
    // Look for repos that contain the query string in the name or in the description
    // and order those results descending, by the number of stars and then by name

    /*@Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE " +
            ":queryString) ORDER BY stars DESC, name ASC")*/
    @Query("SELECT * FROM movies WHERE title LIKE :queryString")
    fun moviesByTitle(queryString: String): LiveData<List<Movie>>
}
