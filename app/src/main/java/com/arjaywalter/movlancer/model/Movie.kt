package com.arjaywalter.movlancer.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
        @PrimaryKey @field:SerializedName("id") val id: Int?,
        @field:SerializedName("vote_count") var voteCount: Int?,
        @field:SerializedName("video") var video: Boolean?,
        @field:SerializedName("vote_average") var voteAverage: Double?,
        @field:SerializedName("title") var title: String?,
        @field:SerializedName("popularity") var popularity: Double?,
        @field:SerializedName("poster_path") var posterPath: String?,
        @field:SerializedName("original_language") var originalLanguage: String?,
        @field:SerializedName("original_title") var originalTitle: String?,
//        @field:SerializedName("genre_ids") var genreIds: List<Int> = emptyList(),
        @field:SerializedName("backdrop_path") var backdropPath: String?,
        @field:SerializedName("adult") var adult: Boolean?,
        @field:SerializedName("overview") var overview: String?,
        @field:SerializedName("release_date") var releaseDate: String?
)