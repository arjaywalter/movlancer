package com.arjaywalter.movlancer

import com.arjaywalter.movlancer.api.MovieService.Companion.POSTER_BASE_URL

fun String.getPosterUrl(): String {
    return POSTER_BASE_URL + this
}