package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.domain.model.movie.PersonSummary
import com.example.showtime.infrastructure.api.login.dto.movie.PersonSummaryDTO

fun PersonSummaryDTO.toDomain(): PersonSummary = PersonSummary(
    imdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath
)
