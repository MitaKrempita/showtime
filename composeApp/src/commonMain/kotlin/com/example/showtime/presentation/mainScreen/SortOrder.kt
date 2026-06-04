package com.example.showtime.presentation.mainScreen

enum class SortOrder(
    val label: String,
    val apiValue: String
) {
    Ascending("Ascending", "asc"),
    Descending("Descending", "desc")
}