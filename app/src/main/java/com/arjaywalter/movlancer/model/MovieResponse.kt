package com.arjaywalter.movlancer.model

import com.arjaywalter.movlancer.model.Movie
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieResponse(

        @SerializedName("page") var page: Int? = null,
        @SerializedName("total_results") var totalResults: Int = 0,
        @SerializedName("total_pages") var totalPages: Int = 0,
        @SerializedName("results") var results: List<Movie> = emptyList()

)