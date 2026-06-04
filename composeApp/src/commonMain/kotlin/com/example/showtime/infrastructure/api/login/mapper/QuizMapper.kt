package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.domain.model.LeaderboardEntry
import com.example.showtime.infrastructure.api.login.dto.LeaderboardEntryDTO

fun LeaderboardEntryDTO.toDomain() : LeaderboardEntry =
    LeaderboardEntry(
        rank = rank,
        userId = user_id,
        username = username,
        fullName = full_name,
        score = score,
        playedAt = played_at,
        totalPlays = total_plays
    )