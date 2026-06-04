package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.domain.model.User
import com.example.showtime.infrastructure.api.login.dto.UserDTO

fun UserDTO.toDomain(): User = User(
    id = id,
    username = username,
    fullName = full_name
)