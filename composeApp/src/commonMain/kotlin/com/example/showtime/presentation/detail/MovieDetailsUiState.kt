package com.example.showtime.presentation.detail

import com.example.showtime.domain.model.movie.Image
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.Movie
import com.example.showtime.domain.model.movie.PersonSummary
import com.example.showtime.domain.model.movie.Video

data class MovieDetailsUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val cast: List<PersonSummary> = emptyList(),
    val backdrops: List<Image> = emptyList(),
    val trailer: Video? = null,
    val imageConfig: ImageConfig? = null,
    val errorMessage: String? = null,
    val actionMessage: String? = null
) {
    val isReady: Boolean
        get() = !isLoading && movie != null && errorMessage == null
}
